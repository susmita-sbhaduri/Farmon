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
import javax.naming.NamingException;
import org.farmon.farmondto.EmployeeDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.bhaduri.machh.services.MasterDataServices;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "employeeAdd")
@ViewScoped
public class EmployeeAdd implements Serializable {
    private String name;
    private String address;
    private String phno;
    private float salary;
    private Date sdate = new Date();
    private Date edate;
    /**
     * Creates a new instance of EmployeeAdd
     */
    public EmployeeAdd() {
    }
    
    public String goToSaveEmp() throws NamingException {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/humanresource/createemp?faces-redirect=true";
        if (name == null || name.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Name is mandatory field.");
            f.addMessage("empname", message);
            return redirectUrl;
        }
        if (address == null || address.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Address is mandatory field.");
            f.addMessage("empaddr", message);
            return redirectUrl;
        }
        
        if (phno == null || phno.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Contact number is mandatory field.");
            f.addMessage("empph", message);
            return redirectUrl;
        }
        
        if (salary == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Salary number is mandatory field.");
            f.addMessage("empsal", message);
            return redirectUrl;
        }
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callMaxEmpIdService(farmondto);
        MasterDataServices masterDataService = new MasterDataServices();
        EmployeeDTO empToAdd = new EmployeeDTO();
        int maxid = Integer.parseInt(farmondto.getEmprec().getId());
        if (maxid == 0 ) {
            empToAdd.setId("1");
        } else {
            empToAdd.setId(String.valueOf(maxid + 1));
        }
        empToAdd.setName(name);
        empToAdd.setAddress(address);
        empToAdd.setPhno(phno);
        empToAdd.setSalary(String.format("%.2f",salary));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        empToAdd.setSdate(sdf.format(sdate));
        if(edate == null) {
           empToAdd.setEdate(null); 
        }else{
           empToAdd.setEdate(sdf.format(edate));
        }
        farmondto.setEmprec(empToAdd);
        farmondto = clientService.callAddEmpService(farmondto);
        int empaddres = farmondto.getResponses().getFarmon_ADD_RES();
        if (empaddres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Employee added successfully");
            f.addMessage(null, message);
//            return "/secured/userhome?faces-redirect=true";
            return redirectUrl;
        } else {  
            if (empaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_DUPLICATE));
                f.addMessage(null, message);
            } 
            if (empaddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
            return "/secured/userhome?faces-redirect=true";
        }
        
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

    
    
}
