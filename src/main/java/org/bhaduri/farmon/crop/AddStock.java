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
import java.util.Date;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;
import org.primefaces.PrimeFaces;

/**
 *
 * @author sb
 */
@Named(value = "addStock")
@ViewScoped
public class AddStock implements Serializable {

    private String selectedCrop;
    private String selectedCropName;
    private HarvestDTO selectedHarvest;
    private List<HarvestDTO> harvestForCrop;
    private String unit;
    private float quantity;
    private Date sdate;
    
    public AddStock() {
    }
    public void fillValues(){
        FarmonDTO farmondto= new FarmonDTO();
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
        
    }
    public void goToReviewRes() {
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        if (selectedShop == null || selectedShop.trim().isEmpty()) {
//            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
//                    "Select one shop.");
//            f.addMessage("shopid", message);
//            return;
//        }
//        
//        if(rate<=0){
//           message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
//                    "Provide a non-zero rate.");
//            f.addMessage("rate", message); 
//            return;
//        }
//        
//        if (amount <= 0) {
//            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
//                    "Provide non-zero purchase amount.");
//            f.addMessage("amount", message);
//            return;
//        }
//        
//        if (purchaseDt == null) {
//            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
//                    "Purchase Date is a mandatory field.");
//            f.addMessage("pdate", message);
//            return;
//        }
//        
//        float calculatedAmount = rate*amount;
//        
//        calcAmt = String.format("%.2f", calculatedAmount);
//        PrimeFaces.current().executeScript("PF('saveConfirmDlg').show();");
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

    public HarvestDTO getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(HarvestDTO selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }

    public List<HarvestDTO> getHarvestForCrop() {
        return harvestForCrop;
    }

    public void setHarvestForCrop(List<HarvestDTO> harvestForCrop) {
        this.harvestForCrop = harvestForCrop;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }
    
}
