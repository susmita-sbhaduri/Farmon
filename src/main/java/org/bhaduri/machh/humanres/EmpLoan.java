/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.humanres;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.farmon.farmondto.EmpExpDTO;
import org.farmon.farmondto.EmployeeDTO;
import org.farmon.farmondto.ExpenseDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "empLoan")
@ViewScoped
public class EmpLoan implements Serializable {
    private String selectedEmp;
    private String selectedEmpName;
    private float totalLoan;
    private Date sdate = new Date();
    private String outstanding;
    private float payback;
    private EmpExpDTO empexpUpd;
    private boolean newRecord = false;
    private boolean readOnlyCondition;
    /**
     * Creates a new instance of EmpLoan
     */
    public EmpLoan() {
    }
    public void fillValues() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        EmployeeDTO empRec = new EmployeeDTO();
        empRec.setId(selectedEmp);
        farmondto.setEmprec(empRec);
        farmondto = clientService.callEmpNameforIdService(farmondto);        
        empRec = farmondto.getEmprec();
        selectedEmpName = empRec.getName();
        
        EmpExpDTO empLoanRec = new EmpExpDTO();
        empLoanRec.setEmpid(selectedEmp);
        empLoanRec.setExpcategory("LOAN");
        farmondto.setEmpexprec(empLoanRec);
        farmondto = clientService.callActiveEmpExpService(farmondto);
        empLoanRec = farmondto.getEmpexprec();
//        List<EmpExpDTO> empLoanRecs = masterDataService.getEmpActiveExpRecs(selectedEmp, "LOAN");
        if(empLoanRec == null){
            totalLoan = 0;
            outstanding = String.format("%.2f", totalLoan);
            newRecord = true;
            readOnlyCondition = false;
        } else {
//          at one time only one loan will be active hence 0th record is taken out
//          This is an existing loan. hence all the records are populated and made read only
            totalLoan = Float.parseFloat(empLoanRec.getTotal());
            outstanding = empLoanRec.getOutstanding();
            sdate =  sdf.parse(empLoanRec.getSdate());
            readOnlyCondition = true;
        }
    }
    public void calculateOutstanding(){
        outstanding = String.format("%.2f", totalLoan);
    }
    
    public String saveDetails(){
        int sqlFlag = 0;
        String redirectUrl = "/secured/humanresource/maintainemp?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        if (newRecord && (Float.parseFloat(outstanding) == totalLoan)
                && (totalLoan > 0)) {
            
            if (sdate == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "Loan start date is a mandatory field.");
                f.addMessage(null, message);
                return "/secured/humanresource/emploan?faces-redirect=true&selectedEmp=" + selectedEmp;
            }
//              Construction of expense record, as newRecord is true 2 records would be inserted in 
//              two tables.            
            ExpenseDTO expenseRec = new ExpenseDTO();
            farmondto = clientService.callMaxExpIdService(farmondto);
            int expenseid = Integer.parseInt(farmondto.getExpenserec().getExpenseId());
            if (expenseid == 0) {
                expenseRec.setExpenseId("1");
            } else {
                expenseRec.setExpenseId(String.valueOf(expenseid + 1));
            }
            expenseRec.setDate(sdf.format(sdate));
            expenseRec.setExpenseRefId(selectedEmp); //######empid as ref id
            expenseRec.setExpenseType("LOAN");
            expenseRec.setExpenditure(String.format("%.2f", totalLoan));
            expenseRec.setCommString("LOAN");
            
            //Construction of empexpense record
            EmpExpDTO empexpRec = new EmpExpDTO();
            farmondto = clientService.callMaxEmpExpIdService(farmondto);
            int empexpid = Integer.parseInt(farmondto.getEmpexprec().getId());
            if (empexpid == 0) {
                empexpRec.setId("1");
            } else {
                empexpRec.setId(String.valueOf(empexpid + 1));
            }
            empexpRec.setTotal(String.format("%.2f", totalLoan));
            empexpRec.setOutstanding(outstanding);
            empexpRec.setExpcategory("LOAN");
            empexpRec.setSdate(sdf.format(sdate));
            empexpRec.setEmpid(selectedEmp);

            farmondto.setExpenserec(expenseRec);
            farmondto = clientService.callAddExpService(farmondto);
            int expres = farmondto.getResponses().getFarmon_ADD_RES();
            if (expres == SUCCESS) {
                sqlFlag = sqlFlag+1;
            } else {
                if (expres == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Duplicate record error for expense table");
                    f.addMessage(null, message);                    
                }
                if (expres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on insert in expense table");
                    f.addMessage(null, message);
                }
            }
            if (sqlFlag == 1) {
                farmondto.setEmpexprec(empexpRec);
                farmondto = clientService.callAddEmpExpService(farmondto);
                int insempexp = farmondto.getResponses().getFarmon_ADD_RES();
                if (insempexp == SUCCESS) {                    
                    sqlFlag = sqlFlag + 1;
                } else {
                    if (insempexp == DB_DUPLICATE) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "Duplicate record error for empexpense table");
                        f.addMessage(null, message);

                    }
                    if (insempexp == DB_SEVERE) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "Failure on insert in empexpense table");
                        f.addMessage(null, message);
                    }
                }
            }
            if (sqlFlag == 2) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Loan added successfully");
                f.addMessage(null, message);
                
            }
        }
        return redirectUrl;
    }

    public String getSelectedEmp() {
        return selectedEmp;
    }

    public void setSelectedEmp(String selectedEmp) {
        this.selectedEmp = selectedEmp;
    }

    public String getSelectedEmpName() {
        return selectedEmpName;
    }

    public void setSelectedEmpName(String selectedEmpName) {
        this.selectedEmpName = selectedEmpName;
    }

    public float getTotalLoan() {
        return totalLoan;
    }

    public void setTotalLoan(float totalLoan) {
        this.totalLoan = totalLoan;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }


    public String getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(String outstanding) {
        this.outstanding = outstanding;
    }

    public boolean isNewRecord() {
        return newRecord;
    }

    public void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
    }

    public EmpExpDTO getEmpexpUpd() {
        return empexpUpd;
    }

    public void setEmpexpUpd(EmpExpDTO empexpUpd) {
        this.empexpUpd = empexpUpd;
    }

    public boolean isReadOnlyCondition() {
        return readOnlyCondition;
    }

    public void setReadOnlyCondition(boolean readOnlyCondition) {
        this.readOnlyCondition = readOnlyCondition;
    }
    
}
