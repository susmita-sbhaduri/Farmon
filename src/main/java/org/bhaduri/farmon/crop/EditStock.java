/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.crop;

import jakarta.inject.Named;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InvDetails;
import org.farmon.farmondto.InventoryDTO;

/**
 *
 * @author sb
 */
@Named(value = "editStock")
@ViewScoped
public class EditStock implements Serializable {

    private String selectedCrop;
    private String selectedCropName;
    private String selectedHarvest;
    private HarvestDTO harvestForCrop;
    private List<CropProductDTO> cropproducts;
    private List<InvDetails> inventories;
    private List<String> existingAmounts;
    
    public EditStock() {
    }
    public void fillValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        CropDTO croprec = new CropDTO();
        croprec.setCropId(selectedCrop);
        farmondto.setCroprec(croprec);
        farmondto = clientService.callCropRecService(farmondto);
        croprec = farmondto.getCroprec();
        selectedCropName = croprec.getCropName();

        InventoryDTO invrec = new InventoryDTO();
        invrec.setCropId(selectedCrop);
        farmondto.setInventoryrec(invrec);
        farmondto = clientService.callLastInvHarForCropService(farmondto);
        harvestForCrop = farmondto.getHarvestrecord();

        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(selectedCrop);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodLstCropidService(farmondto);
        cropproducts = farmondto.getCropprodlist();
        if (cropproducts != null) {
            InvDetails invdetailsrec;
            inventories = new ArrayList<>();
            existingAmounts = new ArrayList<>();
            for (CropProductDTO product : cropproducts) {
                invdetailsrec = new InvDetails();
                invdetailsrec.setCropId(selectedCrop);
                invdetailsrec.setHarvestId(harvestForCrop.getHarvestid());
                invdetailsrec.setProductId(product.getProductId());
                farmondto.setInvdetailsrec(invdetailsrec);
                farmondto = clientService.callLatestInvForCropService(farmondto);
                invdetailsrec = farmondto.getInvdetailsrec();
                inventories.add(invdetailsrec);
                existingAmounts.add(invdetailsrec.getCurrentQty());
            }
        }
    }
    
    public String goToEditStock() {
        
        String redirectUrl = "/secured/crop/maintaincrop?faces-redirect=true";
        int sqlFlag = 0;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        InventoryDTO inventoryrec; 
        CropProductDTO cropprodrec;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int inveditres;
        int invrec = 0;
        String oldAmount;
        for (int i = 0; i < inventories.size(); i++) {
            InvDetails inventory = inventories.get(i);
            oldAmount = existingAmounts.get(i);
            
            inventoryrec = new InventoryDTO();
            cropprodrec = new CropProductDTO();
            inventoryrec.setInventoryId(inventory.getInventoryId());
            inventoryrec.setCropId(inventory.getCropId());
            inventoryrec.setProductId(inventory.getProductId());
            inventoryrec.setHarvestId(inventory.getHarvestId());
            inventoryrec.setCurrentQty(inventory.getCurrentQty());
            inventoryrec.setLastupdatedate(inventory.getLastupdatedate());
            farmondto.setInventoryrec(inventoryrec);
            farmondto = clientService.callEditInvService(farmondto);
            inveditres = farmondto.getResponses().getFarmon_EDIT_RES();
            if (inveditres == SUCCESS) {                
                cropprodrec.setCropId(inventory.getCropId());
                cropprodrec.setProductId(inventory.getProductId());
                farmondto.setCropprodrec(cropprodrec);
                farmondto.setCropprodrec(cropprodrec);
                farmondto = clientService.callCropprodForCropProdService(farmondto);
                cropprodrec = farmondto.getCropprodrec();

                float quantity = Float.parseFloat(cropprodrec.getTotalstock());                
                quantity = quantity - Float.parseFloat(oldAmount)+Float.parseFloat(inventory.getCurrentQty());
                cropprodrec.setTotalstock(String.format("%.2f", quantity));
                farmondto.setCropprodrec(cropprodrec);
                farmondto = clientService.callEditCropProdService(farmondto);
                int response = farmondto.getResponses().getFarmon_EDIT_RES();
                if (response == SUCCESS) {
                    invrec = invrec + 1;
                } else {
                    break;
                }
            }
        }
        
        
        if (sqlFlag == 2) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Stock added to inventory successfully.");
            f.addMessage(null, message);
        }
        return redirectUrl;
        
    }
    
    public String getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(String selectedCrop) {
        this.selectedCrop = selectedCrop;
    }

    public String getSelectedCropName() {
        return selectedCropName;
    }

    public void setSelectedCropName(String selectedCropName) {
        this.selectedCropName = selectedCropName;
    }

    public String getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(String selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }

    public HarvestDTO getHarvestForCrop() {
        return harvestForCrop;
    }

    public void setHarvestForCrop(HarvestDTO harvestForCrop) {
        this.harvestForCrop = harvestForCrop;
    }

    public List<CropProductDTO> getCropproducts() {
        return cropproducts;
    }

    public void setCropproducts(List<CropProductDTO> cropproducts) {
        this.cropproducts = cropproducts;
    }

    public List<InvDetails> getInventories() {
        return inventories;
    }

    public void setInventories(List<InvDetails> inventories) {
        this.inventories = inventories;
    }

    public List<String> getExistingAmounts() {
        return existingAmounts;
    }

    public void setExistingAmounts(List<String> existingAmounts) {
        this.existingAmounts = existingAmounts;
    }
    
    
}
