/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.buyer;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.BuyerDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.SalesDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainBuyer")
@ViewScoped
public class MaintainBuyer implements Serializable {
    private BuyerDTO selectedBuyer;
    private List<BuyerDTO> buyerlist;
    private Map<String, Boolean> buyerEditable = new HashMap<>();
    public MaintainBuyer() {
    }
    
    public void fillShopValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callBuyerListService(farmondto);
        buyerlist = farmondto.getBuyerlist();     
        
        farmondto = clientService.callDisBuyerPerSalesService(farmondto);
        List<SalesDTO> salesbuyerlist = farmondto.getSaleslist();
        
//      Buyers which are ALREADY in ShopResource table cannot be deleted
        
        for (BuyerDTO buyer : buyerlist) {
            boolean deletable = true;
            for (SalesDTO sales : salesbuyerlist) {
                if (sales.getBuyerId().equals(buyer.getBuyerId())) {
                     deletable =false;
                     break;
                }
            }            
            buyerEditable.put(buyer.getBuyerId(), deletable);
        }
    }
    public BuyerDTO getSelectedBuyer() {
        return selectedBuyer;
    }

    public void setSelectedBuyer(BuyerDTO selectedBuyer) {
        this.selectedBuyer = selectedBuyer;
    }

    public List<BuyerDTO> getBuyerlist() {
        return buyerlist;
    }

    public void setBuyerlist(List<BuyerDTO> buyerlist) {
        this.buyerlist = buyerlist;
    }

    public Map<String, Boolean> getBuyerEditable() {
        return buyerEditable;
    }

    public void setBuyerEditable(Map<String, Boolean> buyerEditable) {
        this.buyerEditable = buyerEditable;
    }
    
}
