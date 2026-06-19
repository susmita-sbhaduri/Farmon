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

/**
 *
 * @author sb
 */
@Named(value = "mntnProdStage")
@ViewScoped
public class MntnProdStage implements Serializable {
    private String selectedCrop;
    private String selectedCropProd;
    List<CropDTO> crops;
    List<CropProductDTO> cropprods;
    public MntnProdStage() {
    }
    public void fillValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callCropListService(farmondto);
        crops = farmondto.getCroplist(); 
        for (CropDTO crop : crops) {            
            if(crop.getEndDate()!=null){
                crops.remove(crop);
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
        
        if(selectedCrop == null || selectedCrop.trim().isEmpty()
                || selectedCropProd == null || selectedCropProd.trim().isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/crop/mntnprodstage?faces-redirect=true";
        }
        
        String redirectUrl = "/secured/crop/addcropstage?faces-redirect=true&cropId=" 
                + selectedCrop + "&cropProdId=" + selectedCropProd;
        return redirectUrl; 
    }
    
    public String goToEditStage() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(selectedCrop == null || selectedCrop.trim().isEmpty()
                || selectedCropProd == null || selectedCropProd.trim().isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/crop/mntnprodstage?faces-redirect=true";
        }
        
        String redirectUrl = "/secured/crop/editcropstage?faces-redirect=true&cropId=" 
                + selectedCrop + "&cropProdId=" + selectedCropProd;
        return redirectUrl; 
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
