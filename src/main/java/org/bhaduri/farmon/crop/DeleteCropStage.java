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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.GrowthStageDTO;
import org.farmon.farmondto.ProductStageDTO;

/**
 *
 * @author sb
 */
@Named(value = "deleteCropStage")
@ViewScoped
public class DeleteCropStage implements Serializable {
    private String cropId;
    private String cropname;
    private String cropProdId;
    private String cropprodname;
    private List<ProductStageDTO> stageEntries; 
    private boolean selectAllChecked; // Tracks the header "Select All" toggle state
    // Tracks checkbox states without modifying the ProductStageDTO class
    private Map<String, Boolean> checkedMap = new HashMap<>();
    private boolean conditionalDeleteAllowed;
    public DeleteCropStage() {
    }
    public String fillValues() throws NamingException {
        String redirectUrl = "/secured/crop/mntnprodstage?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        CropDTO croprec = new CropDTO();
        croprec.setCropId(cropId);
        farmondto.setCroprec(croprec);
        farmondto = clientService.callCropRecService(farmondto);
        cropname = farmondto.getCroprec().getCropName();
        
        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(cropId);
        cropprodrec.setProductId(cropProdId);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodForCropProdService(farmondto);
        cropprodname = farmondto.getCropprodrec().getProductName();
        
        GrowthStageDTO growthstagerec = new GrowthStageDTO();
        growthstagerec.setCropId(cropId);
        growthstagerec.setProductId(cropProdId);
        growthstagerec.setCurrentStageId("1");
        farmondto.setGrowthstagerec(growthstagerec);
        farmondto = clientService.callGrthStgsForCropPrdStgidService(farmondto);
        if(farmondto.getGrowthstagelist().isEmpty()){
            conditionalDeleteAllowed = true;
        } else conditionalDeleteAllowed = false;
        
        stageEntries = new ArrayList<>();
        ProductStageDTO prodstagerec = new ProductStageDTO();
        prodstagerec.setCropId(cropId);
        prodstagerec.setProductId(cropProdId);
        farmondto.setProdstagerec(prodstagerec);
        farmondto = clientService.callStagesPerCropProdService(farmondto);
        stageEntries = farmondto.getProdstagelist();
        
        if (stageEntries.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No stages exist for this crop and product.");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            return null;
        }
        
        
    }
    // Triggered automatically when clicking the header checkbox
    public void toggleSelectAll() {
        if (stageEntries != null) {
            for (ProductStageDTO stage : stageEntries) {
                if (stage.getId() != null) {
                    checkedMap.put(stage.getId(), selectAllChecked);
                }
            }
        }
    }
    
    public String saveStages() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        if (conditionalDeleteAllowed) {            
            // 1. Identify selected rows by scanning our helper Map
            List<ProductStageDTO> selectedStages = new ArrayList<>();
            for (ProductStageDTO stage : stageEntries) {
                Boolean isChecked = checkedMap.get(stage.getId());
                if (isChecked != null && isChecked) {
                    selectedStages.add(stage);
                }
            }

            // SCENARIO 1: No checkboxes are checked -> Show warning message, stay on page
            if (selectedStages.isEmpty()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Select atleast one stage to delete.");
                f.addMessage(null, message);
                return null;
            }

            // Create a temporary list to track rows that successfully deleted from the DB
            List<ProductStageDTO> successfullyDeleted = new ArrayList<>();
            int sqlFlag = 0;
            ProductStageDTO stagetodelete = new ProductStageDTO();
            for (ProductStageDTO stage : selectedStages) {
                stagetodelete.setId(String.valueOf(stage.getId()));
                farmondto.setProdstagerec(stagetodelete);
                farmondto = clientService.callDeleteProdStageService(farmondto);
                int stagedelres = farmondto.getResponses().getFarmon_DEL_RES();
                if (stagedelres == SUCCESS) {
                    sqlFlag = sqlFlag + 1;
                    // 1. Remove from checkbox tracking map instantly
                    checkedMap.remove(stage.getId());

                    // 2. Add to our local success list so we can purge it from the dataTable list
                    successfullyDeleted.add(stage);
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                            "Failure to delete stage.");
                    f.addMessage(null, message);
                    break;
                }
            }

            if (sqlFlag == selectedStages.size()) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Stages deleted for Crop Product successfully.");
                f.addMessage(null, message);
                if (stageEntries.size() > selectedStages.size()) {
                    selectAllChecked = false;
                    stageEntries.removeAll(successfullyDeleted);
                    return null;
                }
                if (stageEntries.size() == selectedStages.size()) {
                    return "/secured/crop/mntnprodstage?faces-redirect=true";
                }
            }
        } else {
            GrowthStageDTO growthstagerec = new GrowthStageDTO();
            growthstagerec.setCropId(cropId);
            growthstagerec.setProductId(cropProdId);
            growthstagerec.setCurrentStageId(stageEntries.get(stageEntries.size()-1).getProdStageId());
            farmondto.setGrowthstagerec(growthstagerec);
            farmondto = clientService.callGrthStgsForCropPrdStgidService(farmondto);
            if (farmondto.getGrowthstagelist().isEmpty()) {
                ProductStageDTO stagetodelete = new ProductStageDTO();
                stagetodelete.setId(String.valueOf(stageEntries.get(stageEntries.size()-1).getId()));
                farmondto.setProdstagerec(stagetodelete);
                farmondto = clientService.callDeleteProdStageService(farmondto);
                int stagedelres = farmondto.getResponses().getFarmon_DEL_RES();
                if (stagedelres == SUCCESS) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Stages deleted for Crop Product successfully.");
                    f.addMessage(null, message);
                    return "/secured/crop/mntnprodstage?faces-redirect=true";
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Stage cannot be deleted as there are existing crop products in this stage.");
                f.addMessage(null, message);
                return "/secured/crop/mntnprodstage?faces-redirect=true";
            }
        }
        return "/secured/crop/mntnprodstage?faces-redirect=true";
    }
    public List<ProductStageDTO> getStageEntries() {
        return stageEntries;
    }

    public void setStageEntries(List<ProductStageDTO> stageEntries) {
        this.stageEntries = stageEntries;
    }

    public boolean isSelectAllChecked() {
        return selectAllChecked;
    }

    public void setSelectAllChecked(boolean selectAllChecked) {
        this.selectAllChecked = selectAllChecked;
    }

    public Map<String, Boolean> getCheckedMap() {
        return checkedMap;
    }

    public void setCheckedMap(Map<String, Boolean> checkedMap) {
        this.checkedMap = checkedMap;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public String getCropProdId() {
        return cropProdId;
    }

    public void setCropProdId(String cropProdId) {
        this.cropProdId = cropProdId;
    }

    public String getCropprodname() {
        return cropprodname;
    }

    public void setCropprodname(String cropprodname) {
        this.cropprodname = cropprodname;
    }

    public boolean isConditionalDeleteAllowed() {
        return conditionalDeleteAllowed;
    }

    public void setConditionalDeleteAllowed(boolean conditionalDeleteAllowed) {
        this.conditionalDeleteAllowed = conditionalDeleteAllowed;
    }
    
}
