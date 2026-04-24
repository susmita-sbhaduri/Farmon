/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.sales;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.ExpenseDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
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
                salesrec.setCurrentInventoryQty("");
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
    
    public void onSiteHarSelect() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        for (SalesDTO record : salesrecords) {
            InventoryDTO inventoryrec = new InventoryDTO();
            inventoryrec.setCropId(selectedCrop);
            inventoryrec.setProductId(record.getProdId());
            inventoryrec.setHarvestId(this.selectedHarvest);
            farmondto.setInventoryrec(inventoryrec);
            farmondto = clientService.callSumFortHarCropProdService(farmondto);
            record.setCurrentInventoryQty(farmondto.getInventoryrec().getCurrentQty());
        }
    }
    
    public String goToAddSales() {
        
        String redirectUrl = "/secured/sales/maintainsales?faces-redirect=true";
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
        
        if (selectedProduct.getQuantitySold() == null|| selectedProduct.getQuantitySold().trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide a quantity sold for the product selected.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        BigDecimal qtySold = new BigDecimal(selectedProduct.getQuantitySold());
        BigDecimal currentStock = new BigDecimal(selectedProduct.getCurrentInventoryQty());
        if (qtySold.compareTo(currentStock) > 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Quantity sold cannot be more than the current inventory quantity.");
            f.addMessage(null, message);
            return redirectUrl;
        }
        
        if (selectedProduct.getPriceperUnit() == null|| selectedProduct.getPriceperUnit().trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide the price per unit for the product selected.");
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
        inventoryrec.setProductId(selectedProduct.getProdId());
        inventoryrec.setHarvestId(selectedHarvest);
        inventoryrec.setCurrentQty("-"+selectedProduct.getQuantitySold());
        inventoryrec.setLastupdatedate(sdf.format(sdate));
        
        farmondto.setInventoryrec(inventoryrec);
        farmondto = clientService.callAddInvService(farmondto);
        int invaddres = farmondto.getResponses().getFarmon_ADD_RES();
        if (invaddres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (invaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "The product is already added, product name =" + selectedProduct.getProductname());
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
        cropprodrec.setProductId(selectedProduct.getProdId());
        
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodForCropProdService(farmondto);
        cropprodrec = farmondto.getCropprodrec();
        
        float appliedQuantity = Float.parseFloat(cropprodrec.getTotalstock());
        appliedQuantity = appliedQuantity+Float.parseFloat("-"+selectedProduct.getQuantitySold());
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
        SalesDTO salesrecord = new SalesDTO();
        farmondto = clientService.callMaxSalesIdService(farmondto);
        int salesid = Integer.parseInt(farmondto.getSalesrec().getSalesId());
        if (salesid == 0) {
            salesrecord.setSalesId("1");
        } else {
            salesrecord.setSalesId(String.valueOf(salesid + 1));
        }
        salesrecord.setCropId(selectedCrop);
        salesrecord.setProdId(selectedProduct.getProdId());
        salesrecord.setHarvestId(selectedHarvest);
        salesrecord.setQuantitySold(selectedProduct.getQuantitySold());
        salesrecord.setPriceperUnit(selectedProduct.getPriceperUnit());
        salesrecord.setSalesDate(sdf.format(sdate));
        
        farmondto.setSalesrec(salesrecord);
        farmondto = clientService.callAddSalesService(farmondto);
        int invaddres = farmondto.getResponses().getFarmon_ADD_RES();
        if (invaddres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (invaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "The product is already added, product name =" + selectedProduct.getProductname());
                f.addMessage(null, message);
            }
            if (invaddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "Failure on adding stock");
                f.addMessage(null, message);
            }
            return redirectUrl;
        }
        //        contruction of expense record
        ExpenseDTO expenseRec = new ExpenseDTO();
        farmondto = clientService.callMaxExpIdService(farmondto);
        int expenseid = Integer.parseInt(farmondto.getExpenserec().getExpenseId());
        if (expenseid == 0) {
            expenseRec.setExpenseId("1");
        } else {
            expenseRec.setExpenseId(String.valueOf(expenseid + 1));
        }
        expenseRec.setDate(sdf.format(sdate));
        expenseRec.setExpenseType("SALE");
        float rate = Float.parseFloat("-"+selectedProduct.getPriceperUnit());
        appliedQuantity = Float.parseFloat(cropprodrec.getTotalstock());
        expenseRec.setExpenseRefId(resAcquireRec.getAcquireId()); //######resourcecrop acq id
        float totalSalesAmt = rate * appliedQuantity;
        expenseRec.setExpenditure(String.format("%.2f", totalSalesAmt));
        expenseRec.setCommString("");
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
