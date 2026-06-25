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
import java.util.Date;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.ProductStageDTO;

/**
 *
 * @author sb
 */
@Named(value = "addGrowthStage")
@ViewScoped
public class AddGrowthStage implements Serializable {

    private String harvestId;
    private HarvestDTO harvestRecord;
    private String cropId;
    private String cropname;
    private String cropProdId;
    private String cropprodname; 
    private ProductStageDTO firststage;
//    private List<ProductStageDTO> stageEntries;    
    
    private String selectedStageId;
    private String count;
    private Date date = new Date();
    
    public AddGrowthStage() {
    }
    public void fillValues() {
//        String redirectUrl = "/secured/crop/maintaingrowthstage?faces-redirect=true";
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        
        harvestRecord = new HarvestDTO();
        harvestRecord.setHarvestid(harvestId);
        farmondto.setHarvestrecord(harvestRecord);
        farmondto = clientService.callHarvestRecService(farmondto);
        harvestRecord = farmondto.getHarvestrecord();
        
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
        
        ProductStageDTO stagerec = new ProductStageDTO();
        stagerec.setCropId(cropId);
        stagerec.setProductId(cropProdId);
        stagerec.setProdStageId("1");
        farmondto.setProdstagerec(stagerec);
        farmondto = clientService.callStageForCropProdStgidService(farmondto);
        firststage = farmondto.getProdstagerec();
    }

    public String getHarvestId() {
        return harvestId;
    }

    public void setHarvestId(String harvestId) {
        this.harvestId = harvestId;
    }

    public HarvestDTO getHarvestRecord() {
        return harvestRecord;
    }

    public void setHarvestRecord(HarvestDTO harvestRecord) {
        this.harvestRecord = harvestRecord;
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

    public ProductStageDTO getFirststage() {
        return firststage;
    }

    public void setFirststage(ProductStageDTO firststage) {
        this.firststage = firststage;
    }

    public String getSelectedStageId() {
        return selectedStageId;
    }

    public void setSelectedStageId(String selectedStageId) {
        this.selectedStageId = selectedStageId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
}
