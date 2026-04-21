/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.crop;

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

/**
 *
 * @author sb
 */
@Named(value = "maintainCrop")
@ViewScoped
public class MaintainCrop implements Serializable {
    private CropDTO selectedCrop;
    List<CropDTO> crops;
    private Map<String, Boolean> cropAddable = new HashMap<>();
    private Map<String, Boolean> cropNotUpdatable = new HashMap<>();
    private Map<String, Boolean> cropDeletable = new HashMap<>();
    private Map<String, Boolean> cropActivatable = new HashMap<>();
    /**
     * Creates a new instance of MaintainCrop
     */
    public MaintainCrop() {
    }
    
    public void fillValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callCropListService(farmondto);
        crops = farmondto.getCroplist(); 
        for (CropDTO crop : crops) {
            boolean activatable = false;
            boolean addable = false;
            boolean notupdatable = false;
            if(crop.getEndDate()!=null){
                activatable = true;                
                notupdatable = true;                
            } else {
                addable = true;                
            }
            cropActivatable.put(crop.getCropId(), activatable);
            cropNotUpdatable.put(crop.getCropId(), notupdatable);
            cropAddable.put(crop.getCropId(), addable);
        }
//      Crops which have stock in cropproduct cannot be deleted
        CropDTO cropforstock = new CropDTO();
        List<CropProductDTO> cropprodlist;
        for (CropDTO crop : crops) {
            boolean deletable = false;
            boolean notupdatable = false;
            cropforstock.setCropId(crop.getCropId());
            farmondto.setCroprec(crop);
            farmondto = clientService.callNonzeroProdForCropService(farmondto);
            cropprodlist = farmondto.getCropprodlist();
            if(cropprodlist.isEmpty()){
                deletable = true;
                notupdatable = true;
            }                     
            cropDeletable.put(crop.getCropId(), deletable);
            cropNotUpdatable.put(crop.getCropId(), notupdatable);
        }
        
       
    }
    
    public String addStock() {        
        String redirectUrl = "/secured/crop/addstock?faces-redirect=true&selectedCrop="+ selectedCrop.getCropId();
        return redirectUrl;
    }
    public String updStock() {        
        String redirectUrl = "/secured/crop/editstock?faces-redirect=true&selectedCrop="+ selectedCrop.getCropId();
        return redirectUrl;
    }
    public String deleteCrop() {        
        String redirectUrl = "/secured/crop/deletecrop?faces-redirect=true&selectedCrop="+ selectedCrop.getCropId();
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

    public Map<String, Boolean> getCropDeletable() {
        return cropDeletable;
    }

    public void setCropDeletable(Map<String, Boolean> cropDeletable) {
        this.cropDeletable = cropDeletable;
    }

    public Map<String, Boolean> getCropActivatable() {
        return cropActivatable;
    }

    public void setCropActivatable(Map<String, Boolean> cropActivatable) {
        this.cropActivatable = cropActivatable;
    }

    public Map<String, Boolean> getCropAddable() {
        return cropAddable;
    }

    public void setCropAddable(Map<String, Boolean> cropAddable) {
        this.cropAddable = cropAddable;
    }

    public Map<String, Boolean> getCropNotUpdatable() {
        return cropNotUpdatable;
    }

    public void setCropNotUpdatable(Map<String, Boolean> cropNotUpdatable) {
        this.cropNotUpdatable = cropNotUpdatable;
    }   
    
}
