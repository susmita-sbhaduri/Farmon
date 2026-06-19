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
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.ProductStageDTO;

/**
 *
 * @author sb
 */
@Named(value = "editCropStage")
@ViewScoped
public class EditCropStage implements Serializable {

    private String cropId;
    private String cropname;
    private String cropProdId;
    private String cropprodname;    
    private List<ProductStageDTO> stageEntries;
    private int maxStageId;
    
    public EditCropStage() {
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
        
        stageEntries = new ArrayList<>();
        ProductStageDTO prodstagerec = new ProductStageDTO();
        prodstagerec.setCropId(cropId);
        prodstagerec.setProductId(cropProdId);
        farmondto.setProdstagerec(prodstagerec);
        farmondto = clientService.callStagesPerCropProdService(farmondto);
        stageEntries = farmondto.getProdstagelist();
        maxStageId = 0;
        if (stageEntries.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No stages exist for this crop and product.");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            return null;
//            for (ProductStageDTO stage : stageEntries) {
//                String currentIdStr = stage.getProdStageId();
//                int currentId = Integer.parseInt(currentIdStr);
//                if (currentId > maxStageId) {
//                    maxStageId = currentId;
//                }
//            }
        }             
    }
    
    public String saveStages() {
        String redirectUrl = "/secured/crop/mntnprodstage?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        int sqlFlag = 0;
        ProductStageDTO stagetoedit = new ProductStageDTO();
        for (ProductStageDTO stage : stageEntries) {        
            if (stage.getProdStageName() == null || stage.getProdStageName().trim().isEmpty()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "No Stage name would be empty.");
                f.addMessage(null, message);
                return redirectUrl;
            }
            stagetoedit.setId(String.valueOf(stage.getId()));
            stagetoedit.setCropId(cropId);
            stagetoedit.setProductId(cropProdId);
            stagetoedit.setCropName(cropname);
            stagetoedit.setProductName(cropprodname);
            stagetoedit.setProdStageId(String.valueOf(stage.getProdStageId()));
            stagetoedit.setProdStageName(stage.getProdStageName());
            farmondto.setProdstagerec(stagetoedit);
            farmondto = clientService.callEditProdStageService(farmondto);
            int stageeditres = farmondto.getResponses().getFarmon_EDIT_RES();
            
            if (stageeditres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Failure to edit stage.");
                f.addMessage(null, message);
                break;
            }       
        }
        if (sqlFlag == stageEntries.size()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Stages updated for Crop Product successfully.");
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
