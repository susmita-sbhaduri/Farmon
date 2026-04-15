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
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.farmon.farmondto.InventoryDTO;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sb
 */
@Named(value = "addStock")
@ViewScoped
public class AddStock implements Serializable {

    private String selectedCrop;
    private String selectedCropName;
    private String selectedHarvest;
    private List<HarvestDTO> harvestForCrop;
    private List<CropProductDTO> cropproducts;
    private CropProductDTO selectedProduct;    
    private String stock;
    private Date sdate = new Date();
    
    public AddStock() {
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
//        unit = croprec.getUnit();

        InventoryDTO invrec = new InventoryDTO();
        invrec.setCropId(selectedCrop);
        farmondto.setInventoryrec(invrec);
        farmondto = clientService.callInvHarForCropService(farmondto);
        harvestForCrop = farmondto.getHarvestlist();

        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(selectedCrop);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodLstCropidService(farmondto);
        cropproducts = farmondto.getCropprodlist();
        if (cropproducts != null) {
            for (CropProductDTO product : cropproducts) {
                product.setTotalstock(""); // Note: Use "" if totalstock is still a String
            }
        }
    }
    
    public void onRowSelect(SelectEvent<CropProductDTO> event) {
        CropProductDTO newlySelected = event.getObject();

        // Loop through the entire list of products
        for (CropProductDTO product : cropproducts) {
            // If the product in the loop is NOT the one they just clicked...
            if (!product.getId().equals(newlySelected.getId())) {
                // Clear out any amount they might have typed previously
                product.setTotalstock(""); // Use "" if prodAmount is a String instead of Integer/Double
            }
        }
    }
    
    
    public String goToAddStock() {
        
        String redirectUrl = "/secured/crop/maintaincrop?faces-redirect=true";
        int sqlFlag = 0;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
             
        if (selectedHarvest == null||selectedHarvest.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Select one site and harvest.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        if (selectedProduct == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Select one Product.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        if (selectedProduct.getTotalstock() == null|| selectedProduct.getTotalstock().trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide a valid Amount.");
            f.addMessage(null, message);
            return redirectUrl;
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
    
    public String goToAddAgainStock() {        
        String redirectUrl = "/secured/crop/addstock?faces-redirect=true&selectedCrop="+ selectedCrop;
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

    

    public List<HarvestDTO> getHarvestForCrop() {
        return harvestForCrop;
    }

    public void setHarvestForCrop(List<HarvestDTO> harvestForCrop) {
        this.harvestForCrop = harvestForCrop;
    }

    public List<CropProductDTO> getCropproducts() {
        return cropproducts;
    }

    public void setCropproducts(List<CropProductDTO> cropproducts) {
        this.cropproducts = cropproducts;
    }

    public CropProductDTO getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(CropProductDTO selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }    

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }
    
}
