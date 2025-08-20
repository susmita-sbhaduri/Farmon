/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.HarvestDTO;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "hrvstReport")
@ViewScoped
public class HrvstReport implements Serializable {
    private String selectedHrvst;
    private int selectedIndexHarvest;
    List<HarvestDTO> activeharvests;
    private Date startDt;
    private Date endDt;
    /**
     * Creates a new instance of HrvstReport
     */
    public HrvstReport() {
    }
    public String fillHarvestValues() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        activeharvests = masterDataService.getActiveHarvestList();
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(activeharvests==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active harvests.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    public String summaryAll() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        selectedHrvst = activeharvests.get(selectedIndexHarvest).getHarvestid();
        if(startDt==null||endDt==null||selectedHrvst == null || selectedHrvst.trim().isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/reports/harvestrpts?faces-redirect=true";
        }
        String startDate = sdf.format(startDt);
        String endDate = sdf.format(endDt);
        String redirectUrl = "/secured/reports/hrvstrestotal?faces-redirect=true&harvestId=" + selectedHrvst 
                + "&startDt=" + startDate + "&endDt=" + endDate;
        return redirectUrl; 
    }
    
    public String resourceDetails() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        selectedHrvst = activeharvests.get(selectedIndexHarvest).getHarvestid();
        if(startDt==null||endDt==null||selectedHrvst == null || selectedHrvst.trim().isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/reports/harvestrpts?faces-redirect=true";
        }
        String startDate = sdf.format(startDt);
        String endDate = sdf.format(endDt);
        String redirectUrl = "/secured/reports/hrvstdetails?faces-redirect=true&harvestId=" + selectedHrvst 
                + "&startDt=" + startDate + "&endDt=" + endDate;
        return redirectUrl; 
    }
    
    public String labourDetails() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        selectedHrvst = activeharvests.get(selectedIndexHarvest).getHarvestid();
        if(startDt==null||endDt==null||selectedHrvst == null || selectedHrvst.trim().isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/reports/harvestrpts?faces-redirect=true";
        }
        String startDate = sdf.format(startDt);
        String endDate = sdf.format(endDt);
        String redirectUrl = "/secured/reports/hrvstlabdetails?faces-redirect=true&harvestId=" + selectedHrvst 
                + "&startDt=" + startDate + "&endDt=" + endDate;
        return redirectUrl; 
    }
    
    public String getSelectedHrvst() {
        return selectedHrvst;
    }

    public void setSelectedHrvst(String selectedHrvst) {
        this.selectedHrvst = selectedHrvst;
    }

    public List<HarvestDTO> getActiveharvests() {
        return activeharvests;
    }

    public void setActiveharvests(List<HarvestDTO> activeharvests) {
        this.activeharvests = activeharvests;
    }

    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    public int getSelectedIndexHarvest() {
        return selectedIndexHarvest;
    }

    public void setSelectedIndexHarvest(int selectedIndexHarvest) {
        this.selectedIndexHarvest = selectedIndexHarvest;
    }
    
    
}
