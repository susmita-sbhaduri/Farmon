/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.sales;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InvDetails;
import org.farmon.farmondto.InventoryDTO;
import org.farmon.farmondto.SalesDTO;

/**
 *
 * @author sb
 */
@Named(value = "editSales")
@ViewScoped
public class EditSales implements Serializable {

    private String selectedCrop;
    private String selectedCropName;
    private String selectedHarvest;
    private HarvestDTO harvestForCrop;
    private List<CropProductDTO> cropproducts;
    private List<SalesDTO> salesrecords;
    private List<String> existingAmounts;
    private List<String> existPriceperUnit;
    
    public EditSales() {
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

        SalesDTO salesrec = new SalesDTO();
        salesrec.setCropId(selectedCrop);
        farmondto.setSalesrec(salesrec);
        farmondto = clientService.callLastSalesHarForCropService(farmondto);
        harvestForCrop = farmondto.getHarvestrecord();

        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(selectedCrop);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodLstCropidService(farmondto);
        cropproducts = farmondto.getCropprodlist();
        salesrecords = new ArrayList<>();
        if (cropproducts != null) {
            SalesDTO salesrec;
            existingAmounts = new ArrayList<>();
            existPriceperUnit = new ArrayList<>();
            for (CropProductDTO product : cropproducts) {
                InventoryDTO inventoryrec = new InventoryDTO();
                inventoryrec.setCropId(selectedCrop);
                inventoryrec.setProductId(product.getProductId());
                inventoryrec.setHarvestId(harvestForCrop.getHarvestid());
                farmondto.setInventoryrec(inventoryrec);
                farmondto = clientService.callSumForHarCropProdService(farmondto);
                String qtyString = farmondto.getInventoryrec().getCurrentQty();

                salesrec = new SalesDTO();
                salesrec.setCropId(selectedCrop);
                salesrec.setProdId(product.getProductId());
                salesrec.setHarvestId(harvestForCrop.getHarvestid());
                farmondto.setSalesrec(salesrec);
                farmondto = clientService.callLatestSalesForCropService(farmondto);                
                salesrec = farmondto.getSalesrec();
                salesrec.setCurrentInventoryQty(qtyString);
                salesrecords.add(salesrec);
                existingAmounts.add(salesrec.getQuantitySold());
                existPriceperUnit.add(salesrec.getPriceperUnit());
                
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

    public List<CropProductDTO> getCropproducts() {
        return cropproducts;
    }

    public void setCropproducts(List<CropProductDTO> cropproducts) {
        this.cropproducts = cropproducts;
    }

    public List<SalesDTO> getSalesrecords() {
        return salesrecords;
    }

    public void setSalesrecords(List<SalesDTO> salesrecords) {
        this.salesrecords = salesrecords;
    }

    public List<String> getExistingAmounts() {
        return existingAmounts;
    }

    public void setExistingAmounts(List<String> existingAmounts) {
        this.existingAmounts = existingAmounts;
    }

    public List<String> getExistPriceperUnit() {
        return existPriceperUnit;
    }

    public void setExistPriceperUnit(List<String> existPriceperUnit) {
        this.existPriceperUnit = existPriceperUnit;
    }
    
}
