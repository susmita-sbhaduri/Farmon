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
import javax.naming.NamingException;
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
@Named(value = "invForHarCropProd")
@ViewScoped
public class InvForHarCropProd implements Serializable {
    private String selectedHarvest;
    private String selectedCrop;
    private String selectedCropProd;
    

    List<HarvestDTO> harvests;
    
    List<CropDTO> crops;
    List<CropProductDTO> cropprods;
    public InvForHarCropProd() {
    }

     public String fillHarvestValues(){
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callDistinctHarInvService(farmondto);
        harvests = farmondto.getHarvestlist();
        
        
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(harvests==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No harvests in the inventory.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
     
    public void onHarvestChange() {
        if (selectedHarvest != null && !selectedHarvest.isEmpty()) {
            // Fetch associated crops using the String ID
            FarmonDTO farmondto= new FarmonDTO();
            FarmonClient clientService = new FarmonClient();
            InventoryDTO invrec = new InventoryDTO();
            invrec.setHarvestId(selectedHarvest);
            farmondto.setInventoryrec(invrec);
            farmondto = clientService.callCropsForHarInvService(farmondto);
            crops = farmondto.getCroplist();
        }
    }
    public void onCropChange() {
        if (selectedCrop != null && !selectedCrop.isEmpty()) {
            // Fetch products associated with selected crop
            FarmonDTO farmondto= new FarmonDTO();
            FarmonClient clientService = new FarmonClient();
            InventoryDTO invrec = new InventoryDTO();
            invrec.setHarvestId(selectedHarvest);
            invrec.setCropId(selectedCrop);
            farmondto.setInventoryrec(invrec);
            farmondto = clientService.callCropprodForHarCropService(farmondto);
            cropprods = farmondto.getCropprodlist();
        }
    }
    
    public List<HarvestDTO> getHarvests() {
        return harvests;
    }

    public void setHarvests(List<HarvestDTO> harvests) {
        this.harvests = harvests;
    }

    public String getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(String selectedCrop) {
        this.selectedCrop = selectedCrop;
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

    public String getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(String selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }

    public String getSelectedCropProd() {
        return selectedCropProd;
    }

    public void setSelectedCropProd(String selectedCropProd) {
        this.selectedCropProd = selectedCropProd;
    }
    
}
