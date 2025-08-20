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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.CropDTO;
import org.bhaduri.machh.DTO.EmployeeDTO;
import org.bhaduri.machh.DTO.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_DUPLICATE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.DTO.SiteDTO;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "harvestAdd")
@ViewScoped
public class HarvestAdd implements Serializable {
    
    private List<SiteDTO> existingsites;
    private List<CropDTO> existingcrops;
    private int selectedIndexSite;
    private int selectedIndexCrop;    
    private Date sdate = new Date();
    private Date hdate;
    private String desc;
    public HarvestAdd() {
    }
    public String fillValues() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        existingsites = masterDataService.getSites();
        existingcrops = masterDataService.getCropListForSite(existingsites.get(selectedIndexSite).getSiteID());
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(existingsites==null||existingcrops==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No site or crop records.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    public void onSiteSelect() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        existingcrops = masterDataService.getCropListForSite(existingsites.get(selectedIndexSite).getSiteID());
    }
    public String saveHarvest() throws NamingException {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/harvest/harvestadd?faces-redirect=true";
        
        MasterDataServices masterDataService = new MasterDataServices();
        HarvestDTO harvestToAdd = new HarvestDTO();
        int maxid = masterDataService.getMaxHarvestId();
        if (maxid == 0) {
            harvestToAdd.setHarvestid("1");
        } else {
            harvestToAdd.setHarvestid(String.valueOf(maxid + 1));
        }
        harvestToAdd.setSiteid(existingsites.get(selectedIndexSite).getSiteID());
        harvestToAdd.setCropid(existingcrops.get(selectedIndexCrop).getCropId());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        harvestToAdd.setSowingDate(sdf.format(sdate));
        if(hdate == null) {
           harvestToAdd.setHarvestDate(null); 
        }else{
           harvestToAdd.setHarvestDate(sdf.format(hdate));
        }
        harvestToAdd.setDesc(desc);
        int hrvstaddres = masterDataService.addHarvestRecord(harvestToAdd);
        if (hrvstaddres == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Harvest added successfully");
            f.addMessage(null, message);
            return redirectUrl;
        } else {  
            if (hrvstaddres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_DUPLICATE));
                f.addMessage(null, message);
            } 
            if (hrvstaddres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
            return "/secured/userhome?faces-redirect=true";
        }
        
    }

    public List<SiteDTO> getExistingsites() {
        return existingsites;
    }

    public void setExistingsites(List<SiteDTO> existingsites) {
        this.existingsites = existingsites;
    }

    public List<CropDTO> getExistingcrops() {
        return existingcrops;
    }

    public void setExistingcrops(List<CropDTO> existingcrops) {
        this.existingcrops = existingcrops;
    }

    public int getSelectedIndexSite() {
        return selectedIndexSite;
    }

    public void setSelectedIndexSite(int selectedIndexSite) {
        this.selectedIndexSite = selectedIndexSite;
    }

    public int getSelectedIndexCrop() {
        return selectedIndexCrop;
    }

    public void setSelectedIndexCrop(int selectedIndexCrop) {
        this.selectedIndexCrop = selectedIndexCrop;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
}
