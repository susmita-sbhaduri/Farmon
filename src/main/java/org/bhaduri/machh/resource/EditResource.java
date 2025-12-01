/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.resource;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import static java.util.Collections.list;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "editResource")
@ViewScoped
public class EditResource implements Serializable {
    private String selectedRes;
    private String resname;
    private String unit;
    private int selectedIndex;
    private List<ShopResDTO> shopreslist;
    private FarmresourceDTO farmresrec;
    private List<ShopDTO> shoplist;
    /**
     * Creates a new instance of EditResource
     */
    public EditResource() {
    }
    public void fillExistingDetails() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        resname = farmondto.getFarmresourcerec().getResourceName();
        unit = farmondto.getFarmresourcerec().getUnit();
        
        ShopResDTO shopresrec = new ShopResDTO();
        shopresrec.setResourceId(selectedRes);
        farmondto.setShopresrec(shopresrec);
        farmondto = clientService.callDistictShopResService(farmondto);        
        shopreslist = farmondto.getShopreslist();
        
        farmondto = clientService.callShopListService(farmondto);
        shoplist = farmondto.getShoplist();
        // Remove from shoplist all shops that are ALREADY in shopreslist
        shoplist.removeIf(shop -> {
            for (ShopResDTO res : shopreslist) {  // nested loop check
                if (res.getShopId().equals(shop.getShopId())) {
                    return true;  // remove this shop
                }
            }
            return false;
        });
        // Add "none" option at index 0
        ShopDTO noneOption = new ShopDTO();
        noneOption.setShopName("--");
        noneOption.setShopId("");  // Empty ID for "none"    
        shoplist.add(0, noneOption);  // Now index 0 = "none"
    }
    
    public String saveEditedRes(){
        String redirectUrl = "/secured/resource/editresource?faces-redirect=true&selectedRes=" + selectedRes;
        
        FacesMessage message = null;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        

        if (resname.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Resource name cannot be empty.",
                    "Resource name cannot be empty.");
            f.addMessage("resname", message);
            return redirectUrl;
        }
        if (unit.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unit cannot be empty.",
                    "Unit cannot be empty.");
            f.addMessage("unit", message);
            return redirectUrl;
        }
        
        redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
        
//        construction of farmresource record
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient(); 
        FarmresourceDTO resUpdBean = new FarmresourceDTO();
        FarmresourceDTO farmrestokeep = new FarmresourceDTO();
        resUpdBean.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(resUpdBean);
        farmondto = clientService.callResnameForIdService(farmondto);
        resUpdBean = farmondto.getFarmresourcerec();
        
        farmrestokeep = farmondto.getFarmresourcerec(); // kept for rolling back the update for shopres insert failure
        
        resUpdBean.setResourceName(resname);
        resUpdBean.setUnit(unit);
//        resUpdBean.setAvailableAmt(String.format("%.2f", 0.00));
        farmondto.setFarmresourcerec(resUpdBean);
        farmondto = clientService.callEditFarmresService(farmondto);
        int resres = farmondto.getResponses().getFarmon_EDIT_RES();
        if (resres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Resource updated successfully");
            f.addMessage(null, message);
        } else {
            if (resres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "Duplicate record error for farmresource table");
                f.addMessage(null, message);
            }
            if (resres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "Failure on insert in farmresource table");
                f.addMessage(null, message);
            }
            return redirectUrl;
        } 
        
        
        ShopDTO selectedShop = shoplist.get(selectedIndex);        
        if (!selectedShop.getShopId().isEmpty()) {
            ShopResDTO resShopUpdBean = new ShopResDTO();
            farmondto = clientService.callMaxShopResIdService(farmondto);
            int shopresid = Integer.parseInt(farmondto.getShopresrec().getId());
            if (shopresid == 0) {
                shopresid = 1;
            } else {
                shopresid = shopresid + 1;
            }
            resShopUpdBean.setId(String.valueOf(shopresid));
            resShopUpdBean.setShopId(selectedShop.getShopId());
            resShopUpdBean.setShopName(selectedShop.getShopName());
            resShopUpdBean.setResourceId(selectedRes);
            resShopUpdBean.setResourceName(resUpdBean.getResourceName());
            resShopUpdBean.setRate(String.format("%.2f", 0.00));
            resShopUpdBean.setStockPerRate(String.format("%.2f", 0.00));
            farmondto.setShopresrec(resShopUpdBean);
            farmondto = clientService.callAddShopresService(farmondto);
            int shopres = farmondto.getResponses().getFarmon_ADD_RES();
            if (shopres == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Shop linked to the resource successfully");
                f.addMessage(null, message);
            } else {
                if (shopres == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Duplicate record error for shopresource table");
                    f.addMessage(null, message);
                }
                if (shopres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on insert in shopresource table");
                    f.addMessage(null, message);
                }
                if (resres == SUCCESS) { //farmresource record is added
                    farmondto.setFarmresourcerec(farmrestokeep);
                    farmondto = clientService.callEditFarmresService(farmondto);
                    int delres = farmondto.getResponses().getFarmon_EDIT_RES();

                    if (delres == DB_SEVERE) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "Farmresource record could not be rolled back.");
                        f.addMessage(null, message);
                    }
                }
                return redirectUrl;
            }
        }
       
        return redirectUrl;
    }

    public String getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    public String getResname() {
        return resname;
    }

    public void setResname(String resname) {
        this.resname = resname;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<ShopResDTO> getShopreslist() {
        return shopreslist;
    }

    public void setShopreslist(List<ShopResDTO> shopreslist) {
        this.shopreslist = shopreslist;
    }

    public FarmresourceDTO getFarmresrec() {
        return farmresrec;
    }

    public void setFarmresrec(FarmresourceDTO farmresrec) {
        this.farmresrec = farmresrec;
    }

    public List<ShopDTO> getShoplist() {
        return shoplist;
    }

    public void setShoplist(List<ShopDTO> shoplist) {
        this.shoplist = shoplist;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    
}
