/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.sales;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.SalesDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainSales")
@ViewScoped
public class MaintainSales implements Serializable {
    private CropDTO selectedCrop;
    List<CropDTO> crops;
    private Map<String, Boolean> salesAddable = new HashMap<>();
    private Map<String, Boolean> salesUpdatable = new HashMap<>();
    public MaintainSales() {
    }
    public void fillValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callActiveCropLstService(farmondto);
        crops = farmondto.getCroplist(); 
        
//      Active crops which have stock can be sold
        CropDTO cropforstock = new CropDTO();
        SalesDTO salesrecord = new SalesDTO();
        List<CropProductDTO> cropprodlist;
        List<SalesDTO> saleslist;
        for (CropDTO crop : crops) {
            boolean addable = false;      
            boolean updatable = false;
            cropforstock.setCropId(crop.getCropId());
            farmondto.setCroprec(crop);
            farmondto = clientService.callNonzeroProdForCropService(farmondto);
            cropprodlist = farmondto.getCropprodlist();
            if(!cropprodlist.isEmpty()){
                addable = true;                
            }                     
            salesAddable.put(crop.getCropId(), addable);
            salesrecord.setCropId(crop.getCropId());
            farmondto.setSalesrec(salesrecord);
            farmondto = clientService.callNonzeroSalesForCropService(farmondto);
            saleslist = farmondto.getSaleslist();
            if(!saleslist.isEmpty()){
                updatable = true;                
            } 
            salesUpdatable.put(crop.getCropId(), updatable);
        }
    }
    public String addSales() {        
        String redirectUrl = "/secured/sales/addsales?faces-redirect=true&selectedCrop="+ selectedCrop.getCropId();
        return redirectUrl;
    }
    public String updSales() {        
        String redirectUrl = "/secured/sales/editsales?faces-redirect=true&selectedCrop="+ selectedCrop.getCropId();
        return redirectUrl;
    }
    public CropDTO getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(CropDTO selectedCrop) {
        this.selectedCrop = selectedCrop;
    }

    public List<CropDTO> getCrops() {
        return crops;
    }

    public void setCrops(List<CropDTO> crops) {
        this.crops = crops;
    }

    public Map<String, Boolean> getSalesAddable() {
        return salesAddable;
    }

    public void setSalesAddable(Map<String, Boolean> salesAddable) {
        this.salesAddable = salesAddable;
    }

    public Map<String, Boolean> getSalesUpdatable() {
        return salesUpdatable;
    }

    public void setSalesUpdatable(Map<String, Boolean> salesUpdatable) {
        this.salesUpdatable = salesUpdatable;
    }
    
    
}
