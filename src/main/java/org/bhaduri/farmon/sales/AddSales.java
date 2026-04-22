/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.sales;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;
import org.farmon.farmondto.SalesDTO;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sb
 */
@Named(value = "addSales")
@ViewScoped
public class AddSales implements Serializable {

    private String selectedCrop;
    private String selectedCropName;
    private String selectedHarvest;
    private List<HarvestDTO> harvestForCrop;
    private List<CropProductDTO> cropproducts;
    private List<SalesDTO> salesrecords;
    private SalesDTO selectedProduct;    
    private String stock;
    private Date sdate = new Date();
    
    public AddSales() {
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
        farmondto = clientService.callInvHarForCropService(farmondto);
        harvestForCrop = farmondto.getHarvestlist();

        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(selectedCrop);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodLstCropidService(farmondto);
        cropproducts = farmondto.getCropprodlist();
        if (cropproducts != null) {
            SalesDTO salesrec;
            salesrecords = new ArrayList<>();
            for (CropProductDTO product : cropproducts) {
                salesrec = new SalesDTO();
                salesrec.setCropId(selectedCrop);
                salesrec.setProdId(product.getProductId());
                salesrec.setProductname(product.getProductName());
                salesrec.setProdunit(product.getUnit());
                salesrec.setQuantitySold("");
                salesrec.setPriceperUnit("");
                salesrecords.add(salesrec);
            }
        }
    }
    public void onRowSelect(SelectEvent<SalesDTO> event) {
        SalesDTO newlySelected = event.getObject();

        // Loop through the entire list of products
        for (SalesDTO product : salesrecords) {
            // If the product in the loop is NOT the one they just clicked...
            if (!product.getProdId().equals(newlySelected.getProdId())) {
                // Clear out any amount they might have typed previously
                product.setQuantitySold(""); // Use "" if prodAmount is a String instead of Integer/Double
                product.setPriceperUnit("");
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

    public List<SalesDTO> getSalesrecords() {
        return salesrecords;
    }

    public void setSalesrecords(List<SalesDTO> salesrecords) {
        this.salesrecords = salesrecords;
    }

    public SalesDTO getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(SalesDTO selectedProduct) {
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
