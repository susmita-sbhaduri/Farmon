/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.shop;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.ShopDTO;

/**
 *
 * @author sb
 */
@Named(value = "addShop")
@ViewScoped
public class AddShop implements Serializable {
    private String name;
    private String address;
    private String phno;
    private String atime;
    
    public AddShop() {
    }
    public String goToSaveShop() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/shop/addshop?faces-redirect=true";
        if (name == null || name.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Vendor name is mandatory field.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        if (address == null || address.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Vendor address is mandatory field.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        if (phno == null || phno.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Vendor contact number is mandatory field.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callMaxShopIdService(farmondto);
        ShopDTO shopToAdd = new ShopDTO();
        int maxid = Integer.parseInt(farmondto.getShoprec().getShopId());
        if (maxid == 0 ) {
            shopToAdd.setShopId("1");
        } else {
            shopToAdd.setShopId(String.valueOf(maxid + 1));
        }
        shopToAdd.setShopName(name);
        shopToAdd.setLocation(address);
        shopToAdd.setContact(phno);
        shopToAdd.setAvailabilityTime(atime);
        
        farmondto.setShoprec(shopToAdd);
        farmondto = clientService.callAddShopService(farmondto);
        int shopaddres = farmondto.getResponses().getFarmon_ADD_RES();
        if (shopaddres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Shop added successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (shopaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_DUPLICATE));
                f.addMessage(null, message);
            } 
            if (shopaddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
            return "/secured/shop/maintainshop?faces-redirect=true";
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
