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
import java.util.List;
import org.farmon.farmondto.EmployeeDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainEmp")
@ViewScoped
public class MaintainEmp implements Serializable {

    private EmployeeDTO selectedEmp;
    List<EmployeeDTO> emps;
    
    
    
    public MaintainEmp() {
    }
    public String fillValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callGetActiveEmpService(farmondto);
        emps = farmondto.getEmplist();
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(emps==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active employees.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    
//    public String payEmployee() throws NamingException {
//        String redirectUrl ="/secured/humanresource/maintainemp?faces-redirect=true";
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true);
//        MasterDataServices masterDataService = new MasterDataServices();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        ExpenseDTO expenseRec = new ExpenseDTO();
//        int expenseid = masterDataService.getNextIdForExpense();
//        if (expenseid == 0 || expenseid == DB_SEVERE) {
//            expenseRec.setExpenseId("1");
//        } else {
//            expenseRec.setExpenseId(String.valueOf(expenseid + 1));
//        }
//        Date paymentdt = new Date();
//        expenseRec.setDate(sdf.format(paymentdt));
//        expenseRec.setExpenseRefId(String.valueOf(selectedEmp.getId())); //######empid as ref id
//        
//        expenseRec.setCommString("");        
//        if ("salary".equals(selectedEmp.getPaymentcat())) {
//            expenseRec.setExpenseType("SALARY");            
//        }
//        if ("bonus".equals(selectedEmp.getPaymentcat())) {
//            expenseRec.setExpenseType("BONUS");            
//        }
//        if ("leave".equals(selectedEmp.getPaymentcat())) {
//            expenseRec.setExpenseType("LEAVE");            
//        }
//        if("salary".equals(selectedEmp.getPaymentcat())
//                ||"bonus".equals(selectedEmp.getPaymentcat())
//                ||"leave".equals(selectedEmp.getPaymentcat())){
//            expenseRec.setExpenditure(selectedEmp.getPayment());
//        } else {
//            redirectUrl = "/secured/harvest/resourceapply?faces-redirect=true&selectedEmpId=" + selectedEmp.getId();
//            return redirectUrl;
//        }
//        int expres = masterDataService.addExpenseRecord(expenseRec);
//        if (expres == SUCCESS) {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", 
//                    "Payment added successfully");
//            f.addMessage(null, message);
//        } else {
//            if (expres == DB_DUPLICATE) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                        "Duplicate record error for expense table");
//                f.addMessage(null, message);
//            }
//            if (expres == DB_SEVERE) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                        "Failure on insert in expense table");
//                f.addMessage(null, message);
//            }
//
//        }
//        return redirectUrl;
//            
//    }
    
    public String payEmployee(){
        String redirectUrl = "/secured/humanresource/payemployee?faces-redirect=true&selectedEmp=" + selectedEmp.getId();
        return redirectUrl;
    }
    
    public String loanDetails(){
        String redirectUrl = "/secured/humanresource/emploan?faces-redirect=true&selectedEmp=" + selectedEmp.getId();
        return redirectUrl;
    }
    
    public String goToEditEmp(){
        String redirectUrl = "/secured/humanresource/empedit?faces-redirect=true&selectedEmp=" + selectedEmp.getId();
        return redirectUrl;
    }
    public EmployeeDTO getSelectedEmp() {
        return selectedEmp;
    }

    public void setSelectedEmp(EmployeeDTO selectedEmp) {
        this.selectedEmp = selectedEmp;
    }

    public List<EmployeeDTO> getEmps() {
        return emps;
    }

    public void setEmps(List<EmployeeDTO> emps) {
        this.emps = emps;
    }
    
    
}
