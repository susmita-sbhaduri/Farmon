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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.InventoryDTO;

/**
 *
 * @author sb
 */
@Named(value = "addCrop")
@ViewScoped
public class AddCrop implements Serializable {
    private List<Integer> selectedHarvestIds;
    List<HarvestDTO> activeHarvests;
    private List<CropProductDTO> entries;
    private String cropname;    
    private Date sdate = new Date();
    /**
     * Creates a new instance of AddCrop
     */
    public AddCrop() {
    }
    public String fillValues() throws NamingException {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callActiveHarvestListService(farmondto);
        
        activeHarvests = farmondto.getHarvestlist();
        entries = new ArrayList<>();
        // Add the initial empty pair of textboxes
        entries.add(new CropProductDTO());
       
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
//        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Both products and corresponding units are mandatory.",
//                    "Both products and corresponding units are mandatory."); 
//        f.addMessage(null, message);
            
        if(activeHarvests==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active harvests.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    // Ajax listener fired when an input value changes
    public void onInputChanged(int rowIndex) {       
            
        if (rowIndex == entries.size() - 1) {
            CropProductDTO lastEntry = entries.get(rowIndex);
            
            // If the user typed something in either box, add a new blank row
            boolean isProdFilled = lastEntry.getProductName() != null && !lastEntry.getProductName().trim().isEmpty();
            boolean isUnitFilled = lastEntry.getUnit() != null && !lastEntry.getUnit().trim().isEmpty();
            
            if (isProdFilled || isUnitFilled) {
                entries.add(new CropProductDTO());
            } 
        }
    }
    public String goToSaveCrop() {
        String redirectUrl = "/secured/crop/addcrop?faces-redirect=true";
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient(); 
        int sqlFlag = 0;
        FacesMessage message = null;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        if (selectedHarvestIds == null || selectedHarvestIds.isEmpty()) {            
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select at least one harvest.",
                    "Select at least one harvest.");
            f.addMessage("harvests", message);
            return redirectUrl;
        }

        if (cropname.isBlank()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cropname is mandatory.",
                    "Cropname is mandatory.");
            f.addMessage("cropname", message);
            return redirectUrl;
        }
        for (int i = 0; i < entries.size(); i++) {
            CropProductDTO entry = entries.get(i);
            boolean isProdFilled = entry.getProductName() != null && !entry.getProductName().trim().isEmpty();
            boolean isUnitFilled = entry.getUnit() != null && !entry.getUnit().trim().isEmpty();

            // If it's a half-filled row, block the save and show a message
            if ((isProdFilled && !isUnitFilled) || (!isProdFilled && isUnitFilled)) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "Both products and corresponding units are mandatory.");
                f.addMessage(null, message);
                return redirectUrl;
            }
        }
        // Filter out any rows where BOTH fields are empty
        List<CropProductDTO> validEntries = entries.stream()
                .filter(e -> (e.getProductName() != null && !e.getProductName().trim().isEmpty()) 
                          || (e.getUnit() != null && !e.getUnit().trim().isEmpty()))
                .collect(Collectors.toList());
        
        CropDTO croprec = new CropDTO();
        farmondto = clientService.callMaxCropIdService(farmondto);
        int cropid = Integer.parseInt(farmondto.getCroprec().getCropId());
        if (cropid == 0) {
            cropid = 1;
        } else {
            cropid = cropid + 1;
        }
        croprec.setCropId(String.valueOf(cropid));
        croprec.setCropName(cropname);
        croprec.setTotalStock("0.00");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        croprec.setStartDate(sdf.format(sdate));
        farmondto.setCroprec(croprec);
        farmondto = clientService.callAddCropService(farmondto);
        int cropaddres = farmondto.getResponses().getFarmon_ADD_RES();
        
        if (cropaddres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (cropaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "The crop is already added, crop name =" + cropname);
                f.addMessage(null, message);
            }
            if (cropaddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "Failure on adding crop");
                f.addMessage(null, message);
            }
            return redirectUrl;
        }
        
        int cropprodflag = 0;
        int productId =0;
        CropProductDTO cropprodrec = new CropProductDTO();
        farmondto = clientService.callMaxCropprodIdService(farmondto);
        int cropprodid = Integer.parseInt(farmondto.getCropprodrec().getId());
        if (cropprodid == 0) {
            cropprodid = 1;
        } else {
            cropprodid = cropprodid + 1;
        }
        int cropprodaddres;
        for (int i = 0; i < validEntries.size(); i++) {
            CropProductDTO entry = validEntries.get(i);
            cropprodrec.setId(String.valueOf(cropprodid));
            cropprodrec.setCropId(String.valueOf(cropid));
            productId = productId +1;
            cropprodrec.setProductId(String.valueOf(productId));
            cropprodrec.setProductName(entry.getProductName());
            cropprodrec.setTotalstock("0.00");
            cropprodrec.setUnit(entry.getUnit());
            farmondto.setCropprodrec(cropprodrec);
            farmondto = clientService.callAddCropProdService(farmondto);
            cropprodaddres = farmondto.getResponses().getFarmon_ADD_RES();
            if (cropprodaddres == SUCCESS) {
                cropprodflag = cropprodflag+1;
                cropprodid = cropprodid + 1;
                cropprodrec = new CropProductDTO();
            } else {
                break;
            }
        }
        
        if (validEntries.size() == cropprodflag) {
            sqlFlag = sqlFlag + 1;
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "Failure on adding product");
            f.addMessage(null, message);
            farmondto.setCroprec(croprec);
            farmondto = clientService.callDelCropService(farmondto);
            int delres = farmondto.getResponses().getFarmon_DEL_RES();
            if (delres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "crop record could not be deleted");
                f.addMessage(null, message);
            }
            return redirectUrl;
        }
        
        int invflag = 0;
        InventoryDTO inventoryrec = new InventoryDTO();
        farmondto = clientService.callMaxInvIdService(farmondto);
        int invid = Integer.parseInt(farmondto.getInventoryrec().getInventoryId());
        if (invid == 0) {
            invid = 1;
        } else {
            invid = invid + 1;
        }
        int invaddres;
        int cropprodcount = 0;
        productId = 0;
        for (Integer id : selectedHarvestIds) {
            for (int i = 0; i < validEntries.size(); i++) {
                inventoryrec.setInventoryId(String.valueOf(invid));
                inventoryrec.setCropId(String.valueOf(cropid));
                productId = productId+1;
                inventoryrec.setProductId(String.valueOf(productId));
                inventoryrec.setHarvestId(String.valueOf(id));
                inventoryrec.setCurrentQty("0.00");
                inventoryrec.setLastupdatedate(sdf.format(sdate));
                farmondto.setInventoryrec(inventoryrec);
                farmondto = clientService.callAddInvService(farmondto);
                invaddres = farmondto.getResponses().getFarmon_ADD_RES();
                if (invaddres == SUCCESS) {
                    invflag = invflag + 1;
                    invid = invid + 1;
                    inventoryrec = new InventoryDTO();
                    cropprodcount = cropprodcount+1;
                } else {
                    break;
                }                
            }
            if(cropprodcount < validEntries.size()){
                break;
            }
        }
        if ((selectedHarvestIds.size()*validEntries.size()) == invflag) {
            sqlFlag = sqlFlag + 1;
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "Failure on adding product in the inventory");
            f.addMessage(null, message);
            farmondto.setCroprec(croprec);
            farmondto = clientService.callDelCropService(farmondto);
            int delres = farmondto.getResponses().getFarmon_DEL_RES();
            if (delres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "crop record could not be deleted");
                f.addMessage(null, message);
            }
            
            return redirectUrl;
        }
        if (sqlFlag == 3) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Crop added to inventory successfully.");
            f.addMessage(null, message);
        }

        return redirectUrl;
    }
    public List<Integer> getSelectedHarvestIds() {
        return selectedHarvestIds;
    }

    public void setSelectedHarvestIds(List<Integer> selectedHarvestIds) {
        this.selectedHarvestIds = selectedHarvestIds;
    }

    public List<HarvestDTO> getActiveHarvests() {
        return activeHarvests;
    }

    public void setActiveHarvests(List<HarvestDTO> activeHarvests) {
        this.activeHarvests = activeHarvests;
    }
    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }

    public List<CropProductDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<CropProductDTO> entries) {
        this.entries = entries;
    }
    
}
