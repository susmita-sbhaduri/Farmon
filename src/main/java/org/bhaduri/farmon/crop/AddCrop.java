/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.crop;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "addCrop")
@ViewScoped
public class AddCrop implements Serializable {
    private List<Integer> selectedHarvestIds;
    List<HarvestDTO> activeHarvests;
    private String cropname;
    private String unit;
    /**
     * Creates a new instance of AddCrop
     */
    public AddCrop() {
    }
    public String fillValues() throws NamingException {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callActiveHarvestListService(farmondto);
        
        activeHarvests = farmondto.getHarvestlist();
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(activeHarvests==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active harvests.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    
    public String goToSaveCrop() {
        String redirectUrl = "/secured/crop/addcrop?faces-redirect=true";
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient(); 
        
        FacesMessage message = null;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        if (selectedHarvestIds == null || selectedHarvestIds.isEmpty()) {            
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select at least one harvest.",
                    "Select at least one harvest.");
            f.addMessage("harvests", message);
            return redirectUrl;
        }

        if (cropname.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cropname is mandatory.",
                    "Cropname is mandatory.");
            f.addMessage("cropname", message);
            return redirectUrl;
        }

        if (unit.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unit cannot be empty.",
                    "Unit cannot be empty.");
            f.addMessage("unit", message);
            return redirectUrl;
        } 
        CropDTO croprec = new CropDTO();
        farmondto = clientService.callMaxCropIdService(farmondto);
        int cropid = Integer.parseInt(farmondto.getCroprec().getCropId());
        if (cropid == 0) {
            cropid = 1;
        } else {
            cropid = cropid + 1;
        }
        croprec.setCropId(String.valueOf(cropid));
        croprec.setCropName(cropname);
        croprec.setTotalStock("0");
        croprec.setUnit(unit);
        
        restopasswithname.setResourceName(resname);
        farmondto.setFarmresourcerec(restopasswithname);
        farmondto = clientService.callResidForNameService(farmondto);
        FarmresourceDTO existingreswithName = farmondto.getFarmresourcerec();
        if (existingreswithName != null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "This Resource already exists, go to Edit Resource for this one.");
            f.addMessage(null, message);
            return redirectUrl;

        }
        
        redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
        
        int resid = -1; //initialisation issue
        int resres = -1;
        FarmresourceDTO resAddBean = new FarmresourceDTO();
        if (existingreswithName == null) { // there is no record in Farmresource with the given resourcename
            farmondto = clientService.callMaxCropIdService(farmondto);
            resid = Integer.parseInt(farmondto.getFarmresourcerec().getResourceId());
            if (resid == 0) {
                resid = 1;
            } else {
                resid = resid + 1;
            }
            resAddBean.setResourceId(String.valueOf(resid));
            resAddBean.setResourceName(resname);
            resAddBean.setUnit(unit);
            resAddBean.setAvailableAmt(String.format("%.2f", 0.00));
//            if ("crop".equalsIgnoreCase(rescat) && !unitcrop.isEmpty()){
//                resAddBean.setCropwtunit(unitcrop);
//            }
//            if ("other".equalsIgnoreCase(rescat)){
//                resAddBean.setCropwtunit(null);
//            }
            farmondto.setFarmresourcerec(resAddBean);
            farmondto = clientService.callAddFarmresService(farmondto);
            resres = farmondto.getResponses().getFarmon_ADD_RES();
            if (resres == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Resource added successfully");
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
            resShopUpdBean.setResourceId(String.valueOf(resid));
            resShopUpdBean.setResourceName(resname);

            resShopUpdBean.setRate(String.format("%.2f", 0.00));
            resShopUpdBean.setStockPerRate(String.format("%.2f", 0.00));
            farmondto.setShopresrec(resShopUpdBean);
            farmondto = clientService.callAddShopresService(farmondto);
            int shopres = farmondto.getResponses().getFarmon_ADD_RES();
            if (shopres == SUCCESS) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Shopresource record added successfully");
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
        }

        return redirectUrl;
    }
    public List<Integer> getSelectedHarvestIds() {
        return selectedHarvestIds;
    }

    public void setSelectedHarvestIds(List<Integer> selectedHarvestIds) {
        this.selectedHarvestIds = selectedHarvestIds;
    }

    public List<HarvestDTO> getActiveHarvests() {
        return activeHarvests;
    }

    public void setActiveHarvests(List<HarvestDTO> activeHarvests) {
        this.activeHarvests = activeHarvests;
    }
    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
}
