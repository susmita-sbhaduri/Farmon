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
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;

/**
 *
 * @author sb
 */
@Named(value = "editBuyer")
@ViewScoped
public class EditBuyer implements Serializable {
    private String selectedBuyer;
    private String name;
    private String address;
    private String phno;
    private String atime;
    private BuyerDTO buyerRec;
    public EditBuyer() {
    }
    public void fillValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        buyerRec = new BuyerDTO();
        buyerRec.setBuyerId(selectedBuyer);
        farmondto.setBuyerrec(buyerRec);
        farmondto = clientService.callBuyerforIdService(farmondto);        
        buyerRec = farmondto.getBuyerrec();
        name = buyerRec.getBuyerName();
        address = buyerRec.getLocation();
        phno = buyerRec.getContact();
        atime = buyerRec.getAvailabilityTime();
    }
    
    public String goToSaveBuyer() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/buyer/maintainbuyer?faces-redirect=true";
        
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
        buyerRec.setLocation(address);
        buyerRec.setContact(phno);
        buyerRec.setAvailabilityTime(atime);
        farmondto.setBuyerrec(buyerRec);
        farmondto = clientService.callEditBuyerService(farmondto);
        int buyereditres = farmondto.getResponses().getFarmon_EDIT_RES();
        if (buyereditres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Buyer updated successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (buyereditres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_NON_EXISTING));
                f.addMessage(null, message);
            } 
            if (buyereditres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
        }
        return redirectUrl;
    }
    
    public String getSelectedBuyer() {
        return selectedBuyer;
    }

    public void setSelectedBuyer(String selectedBuyer) {
        this.selectedBuyer = selectedBuyer;
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

    public BuyerDTO getBuyerRec() {
        return buyerRec;
    }

    public void setBuyerRec(BuyerDTO buyerRec) {
        this.buyerRec = buyerRec;
    }
    
    
}
