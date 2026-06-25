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
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;
import org.farmon.farmondto.ProductStageDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainGrowthStage")
@ViewScoped
public class MaintainGrowthStage implements Serializable {

    private String selectedHarvest;
    private String selectedCrop;
    private String selectedCropProd;
    List<HarvestDTO> harvests;    
    List<CropDTO> crops;
    List<CropProductDTO> cropprods;
    
    public MaintainGrowthStage() {
    }

    public String fillHarvestValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callActiveHarvestListService(farmondto);
        harvests = farmondto.getHarvestlist();

        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);

        if (harvests == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active harvests.");
            f.addMessage(null, message);
            return redirectUrl;
        } else {
            return null;
        }
    }
    
    public void onHarvestChange() {
        if (selectedHarvest != null && !selectedHarvest.isEmpty()) {
            // Fetch associated crops using the String ID
            FarmonDTO farmondto = new FarmonDTO();
            FarmonClient clientService = new FarmonClient();
            farmondto = clientService.callCropListService(farmondto);
            crops = farmondto.getCroplist();
            for (CropDTO crop : crops) {
                if (crop.getEndDate() != null) {
                    crops.remove(crop);
                }
            }
        }
    }
    public void onCropChange() {
        if (selectedCrop != null && !selectedCrop.isEmpty()) {
            // Fetch products associated with selected crop
            FarmonDTO farmondto= new FarmonDTO();
            FarmonClient clientService = new FarmonClient();
            CropDTO cropforstage = new CropDTO();
            cropforstage.setCropId(selectedCrop);
            farmondto.setCroprec(cropforstage);
            farmondto = clientService.callNonzeroProdForCropService(farmondto);
            cropprods = farmondto.getCropprodlist();
        }
    }
    
    public String goToAddStage() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(selectedHarvest == null || selectedHarvest.trim().isEmpty()
                || selectedCrop == null || selectedCrop.trim().isEmpty()
                || selectedCropProd == null || selectedCropProd.trim().isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/crop/maintaingrowthstage?faces-redirect=true";
        }
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        ProductStageDTO stagerec = new ProductStageDTO();
        stagerec.setCropId(selectedCrop);
        stagerec.setProductId(selectedCropProd);
        farmondto.setProdstagerec(stagerec);
        farmondto = clientService.callStagesPerCropProdService(farmondto);
        List<ProductStageDTO> stagelist = farmondto.getProdstagelist();
        if (stagelist.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "No Growth stages exist for this crop product");
            f.addMessage(null, message);
            return "/secured/crop/maintaingrowthstage?faces-redirect=true";
        }
        
        String redirectUrl = "/secured/crop/addgrowthstage?faces-redirect=true&harvestId="
                + selectedHarvest + "&cropId=" + selectedCrop 
                + "&cropProdId=" + selectedCropProd;
        return redirectUrl; 
    }

    public String getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(String selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }

    public String getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(String selectedCrop) {
        this.selectedCrop = selectedCrop;
    }

    public String getSelectedCropProd() {
        return selectedCropProd;
    }

    public void setSelectedCropProd(String selectedCropProd) {
        this.selectedCropProd = selectedCropProd;
    }

    public List<HarvestDTO> getHarvests() {
        return harvests;
    }

    public void setHarvests(List<HarvestDTO> harvests) {
        this.harvests = harvests;
    }

    public List<CropDTO> getCrops() {
        return crops;
    }

    public void setCrops(List<CropDTO> crops) {
        this.crops = crops;
    }

    public List<CropProductDTO> getCropprods() {
        return cropprods;
    }

    public void setCropprods(List<CropProductDTO> cropprods) {
        this.cropprods = cropprods;
    }
    
    
    
}
