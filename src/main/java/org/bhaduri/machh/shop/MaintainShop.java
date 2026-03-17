/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.shop;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainShop")
@ViewScoped
public class MaintainShop implements Serializable {
    private ShopDTO selectedShop;
    private List<ShopDTO> shoplist;
    private Map<String, Boolean> shopEditable = new HashMap<>();
    /**
     * Creates a new instance of MaintainShop
     */
    public MaintainShop() {
    }
    public void fillShopValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callShopListService(farmondto);
        shoplist = farmondto.getShoplist();     
        
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
    
    public String deleteShop() { 
        String redirectUrl = "/secured/resource/deleteresource?faces-redirect=true&selectedRes="+ selectedShop.getShopId();
        return redirectUrl;
    }
    
    public String editShop() {
        String redirectUrl = "/secured/shop/editshop?faces-redirect=true&selectedShop=" + selectedShop.getShopId();
        return redirectUrl;

    }
    public List<ShopDTO> getShoplist() {
        return shoplist;
    }

    public void setShoplist(List<ShopDTO> shoplist) {
        this.shoplist = shoplist;
    }

    public Map<String, Boolean> getShopEditable() {
        return shopEditable;
    }

    public void setShopEditable(Map<String, Boolean> shopEditable) {
        this.shopEditable = shopEditable;
    }

    public ShopDTO getSelectedShop() {
        return selectedShop;
    }

    public void setSelectedShop(ShopDTO selectedShop) {
        this.selectedShop = selectedShop;
    }
    
}
