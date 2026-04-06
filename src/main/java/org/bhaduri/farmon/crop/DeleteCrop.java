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
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;
import org.farmon.farmondto.ShopResDTO;

/**
 *
 * @author sb
 */
@Named(value = "deleteCrop")
@ViewScoped
public class DeleteCrop implements Serializable {
    private String selectedCrop;
    private String cropname;
    private CropDTO croprec;
    private List<HarvestDTO> cropharvests;
    private List<CropProductDTO> cropproducts;
    private String count;
    private String sdate;
    /**
     * Creates a new instance of DeleteCrop
     */
    public DeleteCrop() {
    }
    public void fillExistingDetails() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        croprec = new CropDTO();
        croprec.setCropId(selectedCrop);
        farmondto.setCroprec(croprec);
        farmondto = clientService.callCropRecService(farmondto);
        cropname = farmondto.getCroprec().getCropName();
        count = farmondto.getCroprec().getTotalStock();
        sdate = farmondto.getCroprec().getStartDate();
        
        InventoryDTO invrec = new InventoryDTO();
        invrec.setCropId(selectedCrop);
        farmondto.setInventoryrec(invrec);
        farmondto = clientService.callInvHarForCropService(farmondto);
        cropharvests = farmondto.getHarvestlist();
        
    }

    public CropDTO getCroprec() {
        return croprec;
    }

    public void setCroprec(CropDTO croprec) {
        this.croprec = croprec;
    }
    
    public String getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(String selectedCrop) {
        this.selectedCrop = selectedCrop;
    }

    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public List<HarvestDTO> getCropharvests() {
        return cropharvests;
    }

    public void setCropharvests(List<HarvestDTO> cropharvests) {
        this.cropharvests = cropharvests;
    }

    public List<CropProductDTO> getCropproducts() {
        return cropproducts;
    }

    public void setCropproducts(List<CropProductDTO> cropproducts) {
        this.cropproducts = cropproducts;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }
    
}
