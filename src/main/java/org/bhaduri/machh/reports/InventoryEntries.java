/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;

/**
 *
 * @author sb
 */
@Named(value = "inventoryEntries")
@ViewScoped
public class InventoryEntries implements Serializable {
    List<InventoryDTO> inventries;
    private String cropId;
    private String cropProdId;
    private String harvestId;
    private HarvestDTO harvestrec;
    private CropDTO croprec;
    private CropProductDTO cropprodrec;
    
    public InventoryEntries() {
    }
    public String fillValues(){
        String redirectUrl = "/secured/reports/invforharcropprod?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        
        harvestrec = new HarvestDTO();
        harvestrec.setHarvestid(harvestId);
        farmondto.setHarvestrecord(harvestrec);
        farmondto = clientService.callHarvestRecService(farmondto);
        harvestrec = farmondto.getHarvestrecord();
        
        croprec = new CropDTO();
        croprec.setCropId(cropId);
        farmondto.setCroprec(croprec);
        farmondto = clientService.callCropRecService(farmondto);
        croprec = farmondto.getCroprec();
        
        cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(cropId);
        cropprodrec.setProductId(cropProdId);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodForCropProdService(farmondto);
        cropprodrec = farmondto.getCropprodrec();
        
        InventoryDTO invrec = new InventoryDTO();
        invrec.setHarvestId(harvestId);
        invrec.setCropId(cropId);
        invrec.setProductId(cropProdId);
        farmondto.setInventoryrec(invrec);
        farmondto = clientService.callInvForHarCropProdService(farmondto);
        
        inventries = farmondto.getInventorylist();
        if (inventries.isEmpty() || inventries == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "No inventory records found.");
            f.addMessage(null, message);
            return redirectUrl;
        } else
            return null;        
    }

    public List<InventoryDTO> getInventries() {
        return inventries;
    }

    public void setInventries(List<InventoryDTO> inventries) {
        this.inventries = inventries;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getCropProdId() {
        return cropProdId;
    }

    public void setCropProdId(String cropProdId) {
        this.cropProdId = cropProdId;
    }

    public String getHarvestId() {
        return harvestId;
    }

    public void setHarvestId(String harvestId) {
        this.harvestId = harvestId;
    }

    public HarvestDTO getHarvestrec() {
        return harvestrec;
    }

    public void setHarvestrec(HarvestDTO harvestrec) {
        this.harvestrec = harvestrec;
    }

    public CropDTO getCroprec() {
        return croprec;
    }

    public void setCroprec(CropDTO croprec) {
        this.croprec = croprec;
    }

    public CropProductDTO getCropprodrec() {
        return cropprodrec;
    }

    public void setCropprodrec(CropProductDTO cropprodrec) {
        this.cropprodrec = cropprodrec;
    }
    
    
}
