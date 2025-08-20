/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.harvest;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import org.bhaduri.machh.DTO.LabourCropDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.DTO.ResourceCropDTO;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "harvestDetails")
@ViewScoped
public class HarvestDetails implements Serializable {
    
    private String selectedHarvest;
    private String site;
    private String cropcat;
    private String cropname;
    private Date sdate;
    private Date hdate;
    private String desc;
    private HarvestDTO harvestRecord;
    /**
     * Creates a new instance of HarvestDetails
     */
    public HarvestDetails() {
    }
    public void fillValues() throws NamingException, ParseException {
        MasterDataServices masterDataService = new MasterDataServices();
//        appliedreslist = masterDataService.getResCropForHarvest(selectedHarvest);        
//        ResourceCropDTO record = new ResourceCropDTO();
//        for (int i = 0; i < appliedreslist.size(); i++) {
//            FarmresourceDTO farmres = masterDataService.getResourceNameForId(Integer.parseInt(
//                    appliedreslist.get(i).getResourceId()));
//            if(farmres.getCropwtunit()!=null){
//                record.setResourceName(farmres.getResourceName());
//                record.setAppliedAmount(appliedreslist.get(i).getAppliedAmount());
//                record.setApplicationDt(appliedreslist.get(i).getApplicationDt());
//                appliedcroplist.add(record);                
//            } else  {
//                applresourcelist.add(record);
//            }
//            record = new ResourceCropDTO();
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        harvestRecord = masterDataService.getHarvestRecForId(selectedHarvest);
        site = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
        
        sdate = sdf.parse(harvestRecord.getSowingDate());
        if (harvestRecord.getHarvestDate() != null) {
            hdate = sdf.parse(harvestRecord.getHarvestDate());
        } else {
            hdate = null;
        }
        
        desc = harvestRecord.getDesc();
    }
    
    public String saveDesc() throws NamingException {
        
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
        harvestRecord.setDesc(desc);
        
        MasterDataServices masterDataService = new MasterDataServices();
        int empeditres = masterDataService.editHarvestRecord(harvestRecord);
        if (empeditres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Harvest description updated successfully");
            f.addMessage(null, message);
//            return "/secured/userhome?faces-redirect=true";
            return redirectUrl;
        } else {  
            if (empeditres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_NON_EXISTING));
                f.addMessage(null, message);
            } 
            if (empeditres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
//            return "/secured/userhome?faces-redirect=true";
        }
        return redirectUrl;
    }
    
    public String getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(String selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCropcat() {
        return cropcat;
    }

    public void setCropcat(String cropcat) {
        this.cropcat = cropcat;
    }

    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }

    public Date getHdate() {
        return hdate;
    }

    public void setHdate(Date hdate) {
        this.hdate = hdate;
    }

    public HarvestDTO getHarvestRecord() {
        return harvestRecord;
    }

    public void setHarvestRecord(HarvestDTO harvestRecord) {
        this.harvestRecord = harvestRecord;
    }

    
    
    
}
