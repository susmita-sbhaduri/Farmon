/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.crop;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "mntnProdStage")
@ViewScoped
public class MntnProdStage implements Serializable {
    private String selectedCrop;
    private String selectedCropProd;
    List<CropDTO> crops;
    List<CropProductDTO> cropprods;
    public MntnProdStage() {
    }
    public void fillValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callCropListService(farmondto);
        crops = farmondto.getCroplist(); 
        for (CropDTO crop : crops) {
            boolean activatable = false;
            boolean addable = false;
//            boolean notupdatable = false;
            if(crop.getEndDate()!=null){
                activatable = true;                
//                notupdatable = true;                
            } else {
                addable = true;                
            }
//            cropActivatable.put(crop.getCropId(), activatable);
//            cropAddable.put(crop.getCropId(), addable);
        }
//      Crops which have stock in cropproduct cannot be deleted
        CropDTO cropforstage = new CropDTO();
        List<CropProductDTO> cropprodlist;
        for (CropDTO crop : crops) {
            boolean deletable = false;
            boolean notupdatable = false;
            cropforstage.setCropId(crop.getCropId());
            farmondto.setCroprec(crop);
            farmondto = clientService.callNonzeroProdForCropService(farmondto);
            cropprodlist = farmondto.getCropprodlist();
            if(cropprodlist.isEmpty() && crop.getEndDate()==null){
                deletable = true;
            }   
            if(cropprodlist.isEmpty()){
                notupdatable = true;
            } 
//            cropDeletable.put(crop.getCropId(), deletable);
//            cropNotUpdatable.put(crop.getCropId(), notupdatable);
        }
        
       
    }
}
