/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.resource;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import static java.util.Collections.list;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "editResource")
@ViewScoped
public class EditResource implements Serializable {
    private String selectedRes;
    private String resname;
    private String unit;
    private int selectedIndex;
    private List<ShopResDTO> shopreslist;
    private FarmresourceDTO farmresrec;
    private List<ShopDTO> shoplist;
    /**
     * Creates a new instance of EditResource
     */
    public EditResource() {
    }
    public void fillExistingDetails() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        resname = farmondto.getFarmresourcerec().getResourceName();
        unit = farmondto.getFarmresourcerec().getUnit();
        
        ShopResDTO shopresrec = new ShopResDTO();
        shopresrec.setResourceId(selectedRes);
        farmondto.setShopresrec(shopresrec);
        farmondto = clientService.callDistictShopResService(farmondto);        
        shopreslist = farmondto.getShopreslist();
        
        farmondto = clientService.callShopListService(farmondto);
        shoplist = farmondto.getShoplist();
        // Remove from shoplist all shops that are ALREADY in shopreslist
        shoplist.removeIf(shop -> {
            for (ShopResDTO res : shopreslist) {  // nested loop check
                if (res.getShopId().equals(shop.getShopId())) {
                    return true;  // remove this shop
                }
            }
            return false;
        });
        // Add "none" option at index 0
        ShopDTO noneOption = new ShopDTO();
        noneOption.setShopName("none");
        noneOption.setShopId("");  // Empty ID for "none"    
        shoplist.add(0, noneOption);  // Now index 0 = "none"
    }

    public String getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    public String getResname() {
        return resname;
    }

    public void setResname(String resname) {
        this.resname = resname;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<ShopResDTO> getShopreslist() {
        return shopreslist;
    }

    public void setShopreslist(List<ShopResDTO> shopreslist) {
        this.shopreslist = shopreslist;
    }

    public FarmresourceDTO getFarmresrec() {
        return farmresrec;
    }

    public void setFarmresrec(FarmresourceDTO farmresrec) {
        this.farmresrec = farmresrec;
    }

    public List<ShopDTO> getShoplist() {
        return shoplist;
    }

    public void setShoplist(List<ShopDTO> shoplist) {
        this.shoplist = shoplist;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    
}
