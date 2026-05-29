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
@Named(value = "deleteBuyer")
@ViewScoped
public class DeleteBuyer implements Serializable {

    private String selectedBuyer;
    private String name;
    private String address;
    private String phno;
    private String atime;
    private BuyerDTO buyerRec;
    public DeleteBuyer() {
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
    
    public String goToDeleteBuyer() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/buyer/maintainbuyer?faces-redirect=true";
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto.setBuyerrec(buyerRec);
        farmondto = clientService.callDelBuyerService(farmondto);
        int buyerdelres = farmondto.getResponses().getFarmon_DEL_RES();
        if (buyerdelres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Buyer deleted successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (buyerdelres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_NON_EXISTING));
                f.addMessage(null, message);
            } 
            if (buyerdelres == DB_SEVERE) {
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
