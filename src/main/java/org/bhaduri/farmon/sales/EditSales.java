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
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.ExpenseDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
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
    private List<String> existUpdDate;
    
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

        SalesDTO salesrecord = new SalesDTO();
        salesrecord.setCropId(selectedCrop);
        farmondto.setSalesrec(salesrecord);
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
            existUpdDate = new ArrayList<>();
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
                existUpdDate.add(salesrec.getSalesDate());
            }
        }
    }
    
    public String goToEditSales() {
        String redirectUrl = "/secured/sales/maintainsales?faces-redirect=true";       
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();

        InventoryDTO invrec;
        CropProductDTO cropprodrec;
        ExpenseDTO exprec;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int inveditres;
        int saleseditres;
        int cropprodres;
        int expeditres;
        int reccount = 0;
        String oldAmount;
//        String oldPricePerUnit;
        String oldUpdDate;
        for (int i = 0; i < salesrecords.size(); i++) {
            SalesDTO salesrec = salesrecords.get(i);
            oldAmount = existingAmounts.get(i);
//            oldPricePerUnit = existPriceperUnit.get(i);
            oldUpdDate = existUpdDate.get(i);
            if (salesrec.getQuantitySold() == null || salesrec.getQuantitySold().trim().isEmpty()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Quantity sold cannot be zero.");
                f.addMessage(null, message);
                return redirectUrl;
            }

            if (salesrec.getPriceperUnit() == null || salesrec.getPriceperUnit().trim().isEmpty()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Price per unit cannot be zero.");
                f.addMessage(null, message);
                return redirectUrl;
            }

            invrec = new InventoryDTO();
            invrec.setCropId(salesrec.getCropId());
            invrec.setHarvestId(salesrec.getHarvestId());
            invrec.setProductId(salesrec.getProdId());
            invrec.setLastupdatedate(oldUpdDate);
            farmondto.setInventoryrec(invrec);
            farmondto = clientService.callLastInvForSalesService(farmondto);
            invrec = farmondto.getInventoryrec();
            float invcurrqty = Float.parseFloat(salesrec.getQuantitySold()) * (-1);
            invrec.setCurrentQty(String.format("%.2f", invcurrqty));
            invrec.setLastupdatedate(salesrec.getSalesDate());
            farmondto.setInventoryrec(invrec);
            farmondto = clientService.callEditInvService(farmondto);
            inveditres = farmondto.getResponses().getFarmon_EDIT_RES();
            if (inveditres == SUCCESS) {
                farmondto.setSalesrec(salesrec);
                farmondto = clientService.callEditSalesRecService(farmondto);
                saleseditres = farmondto.getResponses().getFarmon_EDIT_RES();
                if (saleseditres == SUCCESS) {
                    cropprodrec = new CropProductDTO();
                    cropprodrec.setCropId(salesrec.getCropId());
                    cropprodrec.setProductId(salesrec.getProdId());
                    farmondto.setCropprodrec(cropprodrec);
                    farmondto = clientService.callCropprodForCropProdService(farmondto);
                    cropprodrec = farmondto.getCropprodrec();
                    float quantity = Float.parseFloat(cropprodrec.getTotalstock());
                    quantity = quantity + Float.parseFloat(oldAmount) - Float.parseFloat(salesrec.getQuantitySold());
                    cropprodrec.setTotalstock(String.format("%.2f", quantity));
                    farmondto.setCropprodrec(cropprodrec);
                    farmondto = clientService.callEditCropProdService(farmondto);
                    cropprodres = farmondto.getResponses().getFarmon_EDIT_RES();
                    if (cropprodres == SUCCESS) {
                        exprec = new ExpenseDTO();
                        exprec.setExpenseRefId(salesrec.getSalesId());
                        exprec.setExpenseType("SALE");
                        farmondto = clientService.callGetExpRecService(farmondto);
                        exprec = farmondto.getExpenserec();
                        float rate = Float.parseFloat("-" + salesrec.getPriceperUnit());
                        float appliedQuantity = Float.parseFloat(salesrec.getQuantitySold());
                        float totalSalesAmt = rate * appliedQuantity;
                        exprec.setExpenditure(String.format("%.2f", totalSalesAmt));
                        exprec.setDate(salesrec.getSalesDate());
                        farmondto = clientService.callEditExpService(farmondto);
                        expeditres = farmondto.getResponses().getFarmon_EDIT_RES();
                        if (expeditres == SUCCESS) {
                            reccount = reccount + 1;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (reccount == salesrecords.size()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Last Sale is updated in inventory successfully.");
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
