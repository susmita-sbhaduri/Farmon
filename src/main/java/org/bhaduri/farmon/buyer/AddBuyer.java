/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.buyer;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.BuyerDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;

/**
 *
 * @author sb
 */
@Named(value = "addBuyer")
@ViewScoped
public class AddBuyer implements Serializable {
    private String name;
    private String address;
    private String phno;
    private String atime;
    public AddBuyer() {
    }
    public String goToSaveBuyer() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/buyer/addbuyer?faces-redirect=true";
        if (name == null || name.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Buyer name is mandatory field.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        if (address == null || address.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Buyer address is mandatory field.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        if (phno == null || phno.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Buyer contact number is mandatory field.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callMaxBuyerIdService(farmondto);
        BuyerDTO buyerToAdd = new BuyerDTO();
        int maxid = Integer.parseInt(farmondto.getBuyerrec().getBuyerId());
        if (maxid == 0 ) {
            buyerToAdd.setBuyerId("1");
        } else {
            buyerToAdd.setBuyerId(String.valueOf(maxid + 1));
        }
        buyerToAdd.setBuyerName(name);
        buyerToAdd.setLocation(address);
        buyerToAdd.setContact(phno);
        buyerToAdd.setAvailabilityTime(atime);
        
        farmondto.setBuyerrec(buyerToAdd);
        farmondto = clientService.callAddBuyerService(farmondto);
        int buyeraddres = farmondto.getResponses().getFarmon_ADD_RES();
        if (buyeraddres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Buyer added successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (buyeraddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_DUPLICATE));
                f.addMessage(null, message);
            } 
            if (buyeraddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
            return "/secured/buyer/maintainbuyer?faces-redirect=true";
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

    public String getAtime() {
        return atime;
    }

    public void setAtime(String atime) {
        this.atime = atime;
    }
    
}
