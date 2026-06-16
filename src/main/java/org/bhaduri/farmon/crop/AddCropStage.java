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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.ProductStageDTO;

/**
 *
 * @author sb
 */
@Named(value = "addCropStage")
@ViewScoped
public class AddCropStage implements Serializable {
    private String cropId;
    private String cropname;
    private String cropProdId;
    private String cropprodname;    
    private List<ProductStageDTO> stageEntries;
    private int maxStageId;
    
    public AddCropStage() {
    }
    public void fillValues() throws NamingException {
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
        
        stageEntries = new ArrayList<>();
        ProductStageDTO prodstagerec = new ProductStageDTO();
        prodstagerec.setCropId(cropId);
        prodstagerec.setProductId(cropProdId);
        farmondto.setProdstagerec(prodstagerec);
        farmondto = clientService.callStagesPerCropProdService(farmondto);
        stageEntries = farmondto.getProdstagelist();
        maxStageId = 0;
        if (stageEntries.isEmpty()) {
            maxStageId = 1;
        } else {            
            for (ProductStageDTO stage : stageEntries) {
                String currentIdStr = stage.getProdStageId();
                int currentId = Integer.parseInt(currentIdStr);
                if (currentId > maxStageId) {
                    maxStageId = currentId;
                }
            }
        }
        
        ProductStageDTO freshRow = new ProductStageDTO();
        freshRow.setCropId(cropId);
        freshRow.setProductId(cropProdId);
        freshRow.setProdStageName("");
        stageEntries.add(freshRow);
             
    }
    
    public void onStageInputChanged(int rowIndex) {

        int lastRowIndex = stageEntries.size() - 1;

        // Verify the user is typing in the final dynamic entry box
        if (rowIndex == lastRowIndex) {
            ProductStageDTO lastRow = stageEntries.get(rowIndex);

            // If they typed an actual stage name, instantiate the next blank row block
            if (lastRow.getProdStageName() != null && !lastRow.getProdStageName().trim().isEmpty()) {

                ProductStageDTO freshRow = new ProductStageDTO();
                freshRow.setCropId(lastRow.getCropId());
                freshRow.setProductId(lastRow.getProductId());
                freshRow.setProdStageName(""); // Blank text field placeholder

                stageEntries.add(freshRow);
            }
        }
    }
    
    public String saveStages() {
        String redirectUrl = "/secured/crop/MntnProdStage?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        // 1. Remove the trailing empty input row first
        if (stageEntries != null && !stageEntries.isEmpty()) {
            ProductStageDTO lastRow = stageEntries.get(stageEntries.size() - 1);
            if (lastRow.getProdStageName() == null || lastRow.getProdStageName().trim().isEmpty()) {
                stageEntries.remove(stageEntries.size() - 1);
            }
        }

        // 2. Separate the newly added stages from the old ones
        List<ProductStageDTO> newlyAddedStages = new ArrayList<>();

        for (ProductStageDTO stage : stageEntries) {
            // If prodStageId is null or empty, it means the user just typed this row on the screen!
            if (stage.getProdStageId() == null || stage.getProdStageId().trim().isEmpty()) {
                newlyAddedStages.add(stage);
            }
        }
        
        if (newlyAddedStages.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Please provide stage to add.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        int sqlFlag = 0;
        // 3. Send ONLY the new stages to your database client service
        if (!newlyAddedStages.isEmpty()) {
            FarmonDTO farmondto = new FarmonDTO();
            FarmonClient clientService = new FarmonClient();
            ProductStageDTO newstage = new ProductStageDTO();
            farmondto = clientService.callMaxProdStagesIdService(farmondto);
            int stageid = Integer.parseInt(farmondto.getProdstagerec().getId());
            if (stageid == 0) {
                stageid = 1;
            } else {
                stageid = stageid + 1;
            }
            // Put the filtered list of new stages into your DTO payload
            for (ProductStageDTO stagetoadd : newlyAddedStages) {
                
                
                newstage.setId(String.valueOf(stageid));
                newstage.setCropId(cropId);
                newstage.setProductId(cropProdId);
                newstage.setCropName(cropname);
                newstage.setProductName(cropprodname);
                newstage.setProdStageId(String.valueOf(maxStageId));
                newstage.setProdStageName(stagetoadd.getProdStageName());
                farmondto.setProdstagerec(newstage);
                farmondto = clientService.callAddProdStageService(farmondto);
                int stageaddres = farmondto.getResponses().getFarmon_ADD_RES();
                if (stageaddres == SUCCESS) {
                    sqlFlag = sqlFlag + 1;
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Failure to add stage.");
                    f.addMessage(null, message);
                    break;
                }
                newstage = new ProductStageDTO();
                maxStageId = maxStageId+1;
                stageid = stageid + 1;
            }
        }
        if (sqlFlag == newlyAddedStages.size()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Stages added for Crop Product successfully.");
            f.addMessage(null, message);
        }

        return redirectUrl;
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

    public List<ProductStageDTO> getStageEntries() {
        return stageEntries;
    }

    public void setStageEntries(List<ProductStageDTO> stageEntries) {
        this.stageEntries = stageEntries;
    }

    public int getMaxStageId() {
        return maxStageId;
    }

    public void setMaxStageId(int maxStageId) {
        this.maxStageId = maxStageId;
    }
    
    
}
