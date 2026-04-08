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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;

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
    private Date edate = new Date();
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
        
        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(selectedCrop);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodLstCropidService(farmondto);
        cropproducts = farmondto.getCropprodlist();
    }

    public String deleteCrop() {
        
        String redirectUrl = "/secured/crop/maintaincrop?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        if (edate==null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "End date is mandatory.",
                    "End date is mandatory.");
            f.addMessage("edate", message);
            return redirectUrl;
        }        
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        croprec = new CropDTO();
        croprec.setCropId(selectedCrop);
        farmondto.setCroprec(croprec);
        farmondto = clientService.callCropRecService(farmondto);
        farmondto.getCroprec().setEndDate(sdf.format(edate));
        farmondto = clientService.callEditCropService(farmondto);
        
        int response = farmondto.getResponses().getFarmon_EDIT_RES();
        if (response == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Crop is deleted successfully");
            f.addMessage(null, message);
           
        } else {
            if (response == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Crop does not exist.");
                f.addMessage(null, message);
            }
            if (response == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Failure on deleting crop");
                f.addMessage(null, message);
            }

        }
        return redirectUrl;
        
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

    public Date getEdate() {
        return edate;
    }

    public void setEdate(Date edate) {
        this.edate = edate;
    }
    
}
