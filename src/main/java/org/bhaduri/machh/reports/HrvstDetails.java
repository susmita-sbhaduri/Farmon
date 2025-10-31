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
import java.util.List;
import org.farmon.farmondto.ResourceCropDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "hrvstDetails")
@ViewScoped
public class HrvstDetails implements Serializable {
    List<ResourceCropDTO> rescrops;
    private String startDt;
    private String endDt;
    private String harvestId;
    /**
     * Creates a new instance of HrvstDetails
     */
    public HrvstDetails() {
        System.out.println("No resourcecrop record is found for this harvest.");
    }
    public String fillValues(){
        String redirectUrl = "/secured/reports/harvestrpts?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto.setReportstartdt(startDt);
        farmondto.setReportenddt(endDt);
        ResourceCropDTO rescroprec = new ResourceCropDTO();
        rescroprec.setHarvestId(harvestId);
        farmondto.setResourceCropDTO(rescroprec);
        farmondto = clientService.callResCropPerHarDtService(farmondto);
        
        rescrops = farmondto.getRescroplist();
        if (rescrops.isEmpty() || rescrops == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "No applied resource found.");
            f.addMessage(null, message);
            return redirectUrl;
        } else
            return null;        
    }

    public List<ResourceCropDTO> getRescrops() {
        return rescrops;
    }

    public void setRescrops(List<ResourceCropDTO> rescrops) {
        this.rescrops = rescrops;
    }

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    public String getHarvestId() {
        return harvestId;
    }

    public void setHarvestId(String harvestId) {
        this.harvestId = harvestId;
    }
    
    
}
