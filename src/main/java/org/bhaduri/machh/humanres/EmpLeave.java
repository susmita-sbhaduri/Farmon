/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.humanres;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.farmon.farmondto.EmpLeaveDTO;
import org.farmon.farmondto.EmployeeDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "empLeave")
@ViewScoped
public class EmpLeave implements Serializable {
    private List<EmployeeDTO> employees;
    private int selectedIndex;
    private Date leaveDt = new Date();
    private String comments;
    /**
     * Creates a new instance of EmpLeave
     */
    public EmpLeave() {
    }
    public String fillValues() throws IOException {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String redirectUrl = contextPath + "/faces/secured/humanresource/createemp.xhtml";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callGetActiveEmpService(farmondto);
        
        employees = farmondto.getEmplist();
        if (employees.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active employee.");
            f.addMessage(null, message);
            // Do the redirect here:
            f.getExternalContext().redirect(redirectUrl);
            f.responseComplete();
            return null; // Return null after a programmatic redirect
        } 
        else return null;        
    }
    
    public String saveEmpLeave(){
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/humanresource/empleave?faces-redirect=true";
        
        if (leaveDt == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Date of leave is a mandatory field.");
            f.addMessage("empsal", message);
            return redirectUrl;
        }
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callMaxLeaveIdService(farmondto);
        EmpLeaveDTO leaveRec = new EmpLeaveDTO();
        int maxid = Integer.parseInt(farmondto.getEmpleaverec().getId());

        if (maxid == 0) {
            leaveRec.setId("1");
        } else {
            leaveRec.setId(String.valueOf(maxid + 1));
        }
        leaveRec.setEmpid(employees.get(selectedIndex).getId());
               
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        leaveRec.setLeavedate(sdf.format(leaveDt));
        leaveRec.setComments(comments);
        
        farmondto.setEmpleaverec(leaveRec);
        farmondto = clientService.callAddEmpLeaveService(farmondto);
        int response = farmondto.getResponses().getFarmon_ADD_RES();
        if (response == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Employee leave added successfully");
            f.addMessage(null, message);
//            return "/secured/userhome?faces-redirect=true";
            return redirectUrl;
        } else {  
            if (response == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_DUPLICATE));
                f.addMessage(null, message);
            } 
            if (response == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
            return "/secured/userhome?faces-redirect=true";
        }
        
    }
    
    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDTO> employees) {
        this.employees = employees;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public Date getLeaveDt() {
        return leaveDt;
    }

    public void setLeaveDt(Date leaveDt) {
        this.leaveDt = leaveDt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    
}
