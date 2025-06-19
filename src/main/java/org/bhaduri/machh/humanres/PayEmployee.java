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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.EmpExpDTO;
import org.bhaduri.machh.DTO.EmployeeDTO;
import org.bhaduri.machh.DTO.ExpenseDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_DUPLICATE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "payEmployee")
@ViewScoped
public class PayEmployee implements Serializable {
    private String selectedEmp;
    private String selectedEmpName;
    private EmployeeDTO empRec;
    private float salary;
    private Date paydate = new Date();    
    private float payback;
    private float bonus;
    private float leave;
    private boolean readOnlyCondition = false;
    /**
     * Creates a new instance of PayEmployee
     */
    public PayEmployee() {
    }
    public void fillValues() throws NamingException {

        MasterDataServices masterDataService = new MasterDataServices();     
        
        empRec = masterDataService.getEmpNameForId(selectedEmp);
        selectedEmpName = empRec.getName();
        salary = Float.parseFloat(empRec.getSalary()); 
//        If the employee has LOAN then EmpExpense record will have a record. Based on this, Loan Repayment
//        field is based editable. If there is no record then this field becomes readonly
        List<EmpExpDTO> empLoanRecs = masterDataService.getEmpActiveExpRecs(selectedEmp, "LOAN");
        if(empLoanRecs.isEmpty()){            
           readOnlyCondition = true;
        } 
        bonus = 0;
        leave = 0;
    }
    
    public String payEmp() throws NamingException {
        String redirectUrl = "/secured/humanresource/maintainemp?faces-redirect=true"; 
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        int resp;
        if (salary > 0) {
            resp = createExpenseRec("SALARY", empRec.getSalary());
            if (resp == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Salary added successfully");
                f.addMessage(null, message);
            } else {
                if (resp == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Duplicate record error for expense table");
                    f.addMessage(null, message);
                }
                if (resp == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on insert in expense table");
                    f.addMessage(null, message);
                }

            }
        }
        if (bonus > 0) {
            resp = createExpenseRec("BONUS", String.format("%.2f", bonus));
            if (resp == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Bonus added successfully");
                f.addMessage(null, message);
            } else {
                if (resp == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Duplicate record error for expense table");
                    f.addMessage(null, message);
                }
                if (resp == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on insert in expense table");
                    f.addMessage(null, message);
                }

            }
        }
        if (leave > 0) {
            resp = createExpenseRec("LEAVE", String.format("%.2f", leave));
            if (resp == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Leave allowance added successfully");
                f.addMessage(null, message);
            } else {
                if (resp == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Duplicate record error for expense table");
                    f.addMessage(null, message);
                }
                if (resp == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on insert in expense table");
                    f.addMessage(null, message);
                }

            }
        }
        // there is loan hence one can enter payback and Loan Repayment is an editable field. 
        if(readOnlyCondition==false){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            MasterDataServices masterDataService = new MasterDataServices();
            String expCat = "LOAN";
        //The closed loan that is the enddate field in empexpense record is not filled up, is taken out.
        //One load can be active at a time. Hence 0th record is selected.
            EmpExpDTO empexpUpd = masterDataService.getEmpActiveExpRecs(selectedEmp, expCat).get(0);
//            float totalLoan = Float.parseFloat(empexpUpd.getTotal());
        //Based on the repayment amount outstngcalc is calculated.
            float outstngcalc = Float.parseFloat(empexpUpd.getOutstanding()) - payback;
            empexpUpd.setOutstanding(String.format("%.2f", outstngcalc));
        //if outstngcalc = 0 then loan is closed with the repayment date  
            if(outstngcalc==0){
                empexpUpd.setEdate(sdf.format(paydate));
            }
            int empexpupd = masterDataService.editEmpExpRecord(empexpUpd);
            if (empexpupd == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Loan updated successfully");
                f.addMessage(null, message);
            } else {
                if (empexpupd == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "This expexpense record does not exist.");
                    f.addMessage(null, message);
                }
                if (empexpupd == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on updating expexpense record.");
                    f.addMessage(null, message);
                }
                
            }
           // If there is a payback then PAYBACK record is inserted in empexpense
            if (payback > 0) {
                //Construction of empexpense record
                EmpExpDTO empexpRec = new EmpExpDTO();
                int empexpid = masterDataService.getMaxEmpExpenseId();
                if (empexpid == 0 || empexpid == DB_SEVERE) {
                    empexpRec.setId("1");
                } else {
                    empexpRec.setId(String.valueOf(empexpid + 1));
                }
                empexpRec.setEmpid(selectedEmp);
                empexpRec.setExpcategory("PAYBACK");
                empexpRec.setTotal(String.format("%.2f", payback));
                empexpRec.setOutstanding(String.format("%.2f", 0.0));
                empexpRec.setSdate(sdf.format(paydate));
                empexpRec.setEdate(null);
                int paybackadd = masterDataService.addEmpExpRecord(empexpRec);
                if (paybackadd == SUCCESS) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Payback updated successfully");
                    f.addMessage(null, message);
                } else {
                    if (empexpupd == DB_DUPLICATE) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "This expexpense record already exist.");
                        f.addMessage(null, message);
                    }
                    if (empexpupd == DB_SEVERE) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "Failure on updating expexpense record.");
                        f.addMessage(null, message);
                    }

                }
            }
        }
        return redirectUrl;

    }
    
    public int createExpenseRec(String empexptype, String expenseAmt) throws NamingException{
        MasterDataServices masterDataService = new MasterDataServices();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ExpenseDTO expenseRec = new ExpenseDTO();
        int expenseid = masterDataService.getNextIdForExpense();
        if (expenseid == 0 || expenseid == DB_SEVERE) {
            expenseRec.setExpenseId("1");
        } else {
            expenseRec.setExpenseId(String.valueOf(expenseid + 1));
        }
        expenseRec.setDate(sdf.format(paydate));
        expenseRec.setExpenseRefId(selectedEmp); //######empid as ref id
        expenseRec.setExpenseType(empexptype);
        expenseRec.setExpenditure(expenseAmt);
        expenseRec.setCommString(empexptype);
        int expres = masterDataService.addExpenseRecord(expenseRec);
        return expres;
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

    public EmployeeDTO getEmpRec() {
        return empRec;
    }

    public void setEmpRec(EmployeeDTO empRec) {
        this.empRec = empRec;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public Date getPaydate() {
        return paydate;
    }

    public void setPaydate(Date paydate) {
        this.paydate = paydate;
    }

    public float getPayback() {
        return payback;
    }

    public void setPayback(float payback) {
        this.payback = payback;
    }

    public float getBonus() {
        return bonus;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public float getLeave() {
        return leave;
    }

    public void setLeave(float leave) {
        this.leave = leave;
    }

    public boolean isReadOnlyCondition() {
        return readOnlyCondition;
    }

    public void setReadOnlyCondition(boolean readOnlyCondition) {
        this.readOnlyCondition = readOnlyCondition;
    }    
    
}
