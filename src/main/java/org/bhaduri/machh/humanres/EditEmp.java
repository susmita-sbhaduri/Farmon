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
import org.farmon.farmondto.EmployeeDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "editEmp")
@ViewScoped
public class EditEmp implements Serializable {
    private String selectedEmp;
    private String name;
    private String address;
    private String phno;
    private float salary;
    private Date sdate;
    private Date edate;
    private EmployeeDTO empRec;
    
    public EditEmp() {
    }
    public void fillValues() throws ParseException {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        empRec = new EmployeeDTO();
        empRec.setId(selectedEmp);
        farmondto.setEmprec(empRec);
        farmondto = clientService.callEmpNameforIdService(farmondto);        
        empRec = farmondto.getEmprec();        
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        name = empRec.getName();
        address = empRec.getAddress();
        phno = empRec.getPhno();
        salary = Float.parseFloat(empRec.getSalary());
        sdate = sdf.parse(empRec.getSdate());
        if (empRec.getEdate() != null) {
            edate = sdf.parse(empRec.getEdate());
        } else {
            edate = null;
        }
    }
    public String goToSaveEmp() {
        
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/humanresource/maintainemp?faces-redirect=true";
        
        if (salary == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Salary is mandatory field.");
            f.addMessage("empsal", message);
            return redirectUrl;
        }
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();       
        
        empRec.setSalary(String.format("%.2f",salary));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        empRec.setSdate(sdf.format(sdate));
        if(edate == null) {
           empRec.setEdate(null); 
        }else{
           empRec.setEdate(sdf.format(edate));
        }
        farmondto.setEmprec(empRec);
        farmondto = clientService.callEditEmpService(farmondto);
        int empeditres = farmondto.getResponses().getFarmon_EDIT_RES();
        if (empeditres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Employee updated successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (empeditres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_NON_EXISTING));
                f.addMessage(null, message);
            } 
            if (empeditres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }

    public Date getEdate() {
        return edate;
    }

    public void setEdate(Date edate) {
        this.edate = edate;
    }

    public EmployeeDTO getEmpRec() {
        return empRec;
    }

    public void setEmpRec(EmployeeDTO empRec) {
        this.empRec = empRec;
    }
    
}
