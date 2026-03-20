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
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.ShopDTO;

/**
 *
 * @author sb
 */
@Named(value = "deleteShop")
@ViewScoped
public class DeleteShop implements Serializable {
    private String selectedShop;
    private String name;
    private String address;
    private String phno;
    private String atime;
    private ShopDTO shopRec;
    /**
     * Creates a new instance of DeleteShop
     */
    public DeleteShop() {
    }
    public void fillValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        shopRec = new ShopDTO();
        shopRec.setShopId(selectedShop);
        farmondto.setShoprec(shopRec);
        farmondto = clientService.callShopforIdService(farmondto);        
        shopRec = farmondto.getShoprec();
        name = shopRec.getShopName();
        address = shopRec.getLocation();
        phno = shopRec.getContact();
        atime = shopRec.getAvailabilityTime();
    }
    
    public String goToDeleteShop() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/shop/maintainshop?faces-redirect=true";
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto.setShoprec(shopRec);
        farmondto = clientService.callDelShopService(farmondto);
        int shopdelres = farmondto.getResponses().getFarmon_DEL_RES();
        if (shopdelres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Shop deleted successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (shopdelres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_NON_EXISTING));
                f.addMessage(null, message);
            } 
            if (shopdelres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
        }
        return redirectUrl;
    }

    public String getSelectedShop() {
        return selectedShop;
    }

    public void setSelectedShop(String selectedShop) {
        this.selectedShop = selectedShop;
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

    public ShopDTO getShopRec() {
        return shopRec;
    }

    public void setShopRec(ShopDTO shopRec) {
        this.shopRec = shopRec;
    }
    
    
}
