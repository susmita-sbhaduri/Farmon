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
            for (CropProductDTO product : cropproducts) {
                invdetailsrec = new InvDetails();
                invdetailsrec.setCropId(selectedCrop);
                invdetailsrec.setHarvestId(harvestForCrop.getHarvestid());
                invdetailsrec.setProductId(product.getProductId());
                farmondto.setInvdetailsrec(invdetailsrec);
                farmondto = clientService.callLatestInvForCropService(farmondto);
                invdetailsrec = farmondto.getInvdetailsrec();
                inventories.add(invdetailsrec);
            }
        }
    }
    
    public String goToEditStock() {
        
        String redirectUrl = "/secured/crop/maintaincrop?faces-redirect=true";
        int sqlFlag = 0;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        InventoryDTO inventoryrec;   
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (InvDetails inventory : inventories) {
            inventoryrec = new InventoryDTO();
            inventoryrec.setInventoryId(inventory.getInventoryId());
            inventoryrec.setCropId(inventory.getCropId());
            inventoryrec.setProductId(inventory.getProductId());
            inventoryrec.setHarvestId(inventory.getHarvestId());
            inventoryrec.setCurrentQty(inventory.getCurrentQty());
            inventoryrec.setLastupdatedate(sdf.format(inventory.getLastupdatedate()));
            farmondto.setInventoryrec(inventoryrec);
            farmondto = clientService.callLatestInvForCropService(farmondto);
            invdetailsrec = farmondto.getInvdetailsrec();
            inventories.add(invdetailsrec);
        }
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        InventoryDTO inventoryrec = new InventoryDTO();
        farmondto = clientService.callMaxInvIdService(farmondto);
        int invid = Integer.parseInt(farmondto.getInventoryrec().getInventoryId());
        if (invid == 0) {
            invid = 1;
        } else {
            invid = invid + 1;
        }
        inventoryrec.setInventoryId(String.valueOf(invid));
        inventoryrec.setCropId(selectedCrop);
        inventoryrec.setProductId(selectedProduct.getProductId());
        inventoryrec.setHarvestId(selectedHarvest);
        inventoryrec.setCurrentQty(selectedProduct.getTotalstock());
        inventoryrec.setLastupdatedate(sdf.format(sdate));
        
        farmondto.setInventoryrec(inventoryrec);
        farmondto = clientService.callAddInvService(farmondto);
        int invaddres = farmondto.getResponses().getFarmon_ADD_RES();
        if (invaddres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (invaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "The product is already added, product name =" + selectedProduct.getProductName());
                f.addMessage(null, message);
            }
            if (invaddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "Failure on adding stock");
                f.addMessage(null, message);
            }
            return redirectUrl;
        }
        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(selectedCrop);
        cropprodrec.setProductId(selectedProduct.getProductId());
        
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodForCropProdService(farmondto);
        cropprodrec = farmondto.getCropprodrec();
        
        float appliedQuantity = Float.parseFloat(cropprodrec.getTotalstock());
        appliedQuantity = appliedQuantity+Float.parseFloat(selectedProduct.getTotalstock());
        cropprodrec.setTotalstock(String.format("%.2f", appliedQuantity));
        farmondto.setCropprodrec(cropprodrec);        
        farmondto = clientService.callEditCropProdService(farmondto);
        
        int response = farmondto.getResponses().getFarmon_EDIT_RES();
        if (response == SUCCESS) {
            sqlFlag = sqlFlag + 1;            
           
        } else {
            if (response == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Cropproduct does not exist.");
                f.addMessage(null, message);
            }
            if (response == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Failure on editing cropproduct");
                f.addMessage(null, message);
            }
            farmondto.setInventoryrec(inventoryrec); 
            farmondto = clientService.callDelInventoryRecService(farmondto);
            int delinv = farmondto.getResponses().getFarmon_DEL_RES();
            if (delinv == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Inventory record could not be deleted");
                f.addMessage(null, message);
            }
            return redirectUrl;
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
    
    
}
