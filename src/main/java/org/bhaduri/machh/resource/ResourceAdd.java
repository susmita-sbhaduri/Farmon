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
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.FarmresourceDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "resourceAdd")
@ViewScoped
public class ResourceAdd implements Serializable {
    private String resid;
    private String resname;
    private int selectedIndex;
    private List<ShopDTO> shoplist;
    private float rate;
    private String unit;
    private String rescat;
    private String unitcrop;
    private boolean unitcropReadonly = true; // default as readonly

    public String fillExistingDetails() throws NamingException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient(); 
        farmondto = clientService.callMaxFarmresIdService(farmondto);
//        MasterDataServices masterDataService = new MasterDataServices();
        resid = farmondto.getFarmresourcerec().getResourceId();
        if(resid.equals("0")){
            resid="1";
        } else {
            int residInt = Integer.parseInt(resid);
            residInt = residInt + 1;
            resid = String.valueOf(residInt);
        }
        farmondto = clientService.callShopListService(farmondto);
        shoplist = farmondto.getShoplist();
        String redirectUrl;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true); 
        if (shoplist.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "No shops to be linked.");
//            f.addMessage("othershopid", message);
            f.addMessage(null, message);
//            redirectUrl = "/secured/shop/reshoplist?faces-redirect=true&resourceId=" + resourceId + "&resourceName=" + resourceName;
            redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
            return redirectUrl;
        }
        else return null;
    }
    public void onResourceCatChange() {
        if ("other".equalsIgnoreCase(rescat)) {
            unitcropReadonly = true;
            unitcrop = ""; // optionally clear the field
        } 
        if ("crop".equalsIgnoreCase(rescat)) {
            unitcropReadonly = false;
        } 
    }
    public String goToSaveRes() throws NamingException {
        String redirectUrl = "/secured/resource/addinventory?faces-redirect=true";
        
        FacesMessage message = null;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        ShopDTO selectedShop = shoplist.get(selectedIndex);

        if (resname.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Resource name cannot be empty.",
                    "Resource name cannot be empty.");
            f.addMessage("unit", message);
            return redirectUrl;
        }

        if (selectedShop.getShopId().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "First add shop.",
                    "First add shop.");
            f.addMessage("shopid", message);
            return redirectUrl;
        }

        if (unit.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unit cannot be empty.",
                    "Unit cannot be empty.");
            f.addMessage("unit", message);
            return redirectUrl;
        }
        
        
        
        if ("crop".equalsIgnoreCase(rescat) && unitcrop.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Unit for crop is mandatory.");
            f.addMessage("unitcrop", message);
            return redirectUrl;
        }
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient(); 
        FarmresourceDTO restopasswithname = new FarmresourceDTO();
        restopasswithname.setResourceName(resname);
        farmondto.setFarmresourcerec(restopasswithname);
        farmondto = clientService.callResidForNameService(farmondto);
//        MasterDataServices masterDataService = new MasterDataServices();
        FarmresourceDTO existingreswithName = farmondto.getFarmresourcerec();
        // in Farmresource table, ideally there should be unique id and and associated resourcename
        if (existingreswithName != null) {
            String residexisting = existingreswithName.getResourceId();
            //for a newly added resourceid+shop id combination ideally there should be one record but as 
            //resourceacquire keeps on adding records in shopresource so there might be many records in ShopRes table.
            ShopResDTO inputrec = new ShopResDTO();
            inputrec.setResourceId(residexisting);
            inputrec.setShopId(selectedShop.getShopId());
            farmondto.setShopresrec(inputrec);
            farmondto = clientService.callShopResListService(farmondto);
            
            List<ShopResDTO> existingResShopIdList = farmondto.getShopreslist();
            //There is no need to add a record in Farmresource and also ShopRes table.
            if (!existingResShopIdList.isEmpty()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Same Resource and Shop combition already exists.");
                f.addMessage(null, message);
                return redirectUrl;
            }
        }
        
        redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
        
        int resres = 999; //initialisation issue
        FarmresourceDTO resAddBean = new FarmresourceDTO();
        if (existingreswithName == null) { // there is no record in Farmresource with the given resourcename
            resAddBean.setResourceId(resid);
            resAddBean.setResourceName(resname);
            resAddBean.setUnit(unit);
            resAddBean.setAvailableAmt(String.format("%.2f", 0.00));
            if ("crop".equalsIgnoreCase(rescat) && !unitcrop.isEmpty()){
                resAddBean.setCropwtunit(unitcrop);
            }
            if ("other".equalsIgnoreCase(rescat)){
                resAddBean.setCropwtunit(null);
            }
            farmondto.setFarmresourcerec(resAddBean);
            farmondto = clientService.callAddFarmresService(farmondto);
            resres = farmondto.getResponses().getFarmon_ADD_RES();
            if (resres != SUCCESS) {
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
        } 
        

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
        if (existingreswithName == null) {//new resource added in Farmresource and ShopRes record has to be added
            resShopUpdBean.setResourceId(resid);
            resShopUpdBean.setResourceName(resname);
        } else {//resource is not new for ShopRes record(Farmresource+shopid) is new
            resShopUpdBean.setResourceId(existingreswithName.getResourceId());
            resShopUpdBean.setResourceName(existingreswithName.getResourceName());
        }
        resShopUpdBean.setRate(String.format("%.2f", 0.00));
        resShopUpdBean.setStockPerRate(String.format("%.2f", 0.00));
        farmondto.setShopresrec(resShopUpdBean);
        farmondto = clientService.callAddShopresService(farmondto);
        int shopres = farmondto.getResponses().getFarmon_ADD_RES();
        if (shopres != SUCCESS) {
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
                farmondto.setFarmresourcerec(resAddBean);
                farmondto = clientService.callDelFarmresService(farmondto);
                int delres = farmondto.getResponses().getFarmon_DEL_RES();
                
                if (delres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Farmresource record could not be deleted");
                    f.addMessage(null, message);
                }
            }
            return redirectUrl;
        }
        if (resres == SUCCESS) {
            if (shopres == SUCCESS) {//new resource added in Farmresource and ShopRes record ghas to be added
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Resource added successfully");
                f.addMessage(null, message);
            }
        } else {//resource is not new for ShopRes record(Farmresource+shopid) is new
            if (shopres == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Resource added successfully");
                f.addMessage(null, message);
            }
        }
        return redirectUrl;
    }

    
    public ResourceAdd() {
    }

    public String getResid() {
        return resid;
    }

    public void setResid(String resid) {
        this.resid = resid;
    }

    public String getResname() {
        return resname;
    }

    public void setResname(String resname) {
        this.resname = resname;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }    

    public List<ShopDTO> getShoplist() {
        return shoplist;
    }

    public void setShoplist(List<ShopDTO> shoplist) {
        this.shoplist = shoplist;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRescat() {
        return rescat;
    }

    public void setRescat(String rescat) {
        this.rescat = rescat;
    }

    public String getUnitcrop() {
        return unitcrop;
    }

    public void setUnitcrop(String unitcrop) {
        this.unitcrop = unitcrop;
    }

    public boolean isUnitcropReadonly() {
        return unitcropReadonly;
    }

    public void setUnitcropReadonly(boolean unitcropReadonly) {
        this.unitcropReadonly = unitcropReadonly;
    }
    
}
