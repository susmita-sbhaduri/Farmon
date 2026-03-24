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
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainCrop")
@ViewScoped
public class MaintainCrop implements Serializable {
    private CropDTO selectedCrop;
    List<CropDTO> crops;
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
        
        farmondto = clientService.callDisShopPerResService(farmondto);
        List<ShopResDTO> shopreslist = farmondto.getShopreslist();
        
//      Shops which are ALREADY in shopres cannot be deleted
        
        for (ShopDTO shop : shoplist) {
            boolean deletable = true;
            for (ShopResDTO shopres : shopreslist) {
                if (shopres.getShopId().equals(shop.getShopId())) {
                     deletable =false;
                     break;
                }
            }            
            shopEditable.put(shop.getShopId(), deletable);
        }
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
    
}
