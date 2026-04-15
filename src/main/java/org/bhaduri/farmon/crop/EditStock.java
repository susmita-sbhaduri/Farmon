/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.crop;

import jakarta.inject.Named;
import jakarta.enterprise.context.Dependent;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InvDetails;
import org.farmon.farmondto.InventoryDTO;

/**
 *
 * @author sb
 */
@Named(value = "editStock")
@Dependent
public class EditStock {

    private String selectedCrop;
    private String selectedCropName;
    private String selectedHarvest;
    private HarvestDTO harvestForCrop;
    private List<CropProductDTO> cropproducts;
    
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
            InvDetails invdetailsrec = new InvDetails();
            
            for (CropProductDTO product : cropproducts) {
                invdetailsrec.setCropId(selectedCrop);
                invdetailsrec.setHarvestId(harvestForCrop.getHarvestid());
                invdetailsrec.setProductId(product.getProductId());
                farmondto.setInvdetailsrec(invdetailsrec);
                farmondto = clientService.callLatestInvForCropService(farmondto);
                
            }
        }
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
    
    
}
