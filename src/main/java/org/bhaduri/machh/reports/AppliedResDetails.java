/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.ResourceCropDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "appliedResDetails")
@ViewScoped
public class AppliedResDetails implements Serializable {
    private Date startDt;
    private Date endDt = new Date();
    private List<FarmresourceDTO> availableresources;
    private int selectedIndexRes;
    private String unit;
//    private String rescat;
//    private String cropwt;
//    private String cropwtunit;
    private boolean messageShown = false;

    public String fillValues() throws IOException {
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callNonzeroresListService(farmondto);

        availableresources = farmondto.getFarmresourcelist();
        if (availableresources.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No available resources.");
            f.addMessage(null, message);
            return redirectUrl;
        } 
        
       
        ResourceCropDTO resourceCrop = new ResourceCropDTO();
        resourceCrop.setResourceId(availableresources.
                get(selectedIndexRes).getResourceId());
        farmondto.setResourceCropDTO(resourceCrop);
        farmondto = clientService.callResCropPerResService(farmondto);
        List<ResourceCropDTO> listforRes = farmondto.getRescroplist();
        if (listforRes.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "This resource is not applied so far.");
            f.addMessage(null, message);
            messageShown = true; // prevent repeated messages
            return null; // Return null will return to the current loading page
        } else {
            return null;
        }
        
    }
    
    public AppliedResDetails() {
        // Inside your bean's constructor or init method
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1); // subtract 1 month
        Date oneMonthAgo = cal.getTime();
        startDt = oneMonthAgo;
    }
    public void onResourceSelect() throws IOException {
        //here redirection is done programmatically using ExternalContext.redirect()
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String redirectUrl = contextPath + "/faces/secured/reports/appliedresdetails.xhtml";

        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        ResourceCropDTO resourceCrop = new ResourceCropDTO();
        resourceCrop.setResourceId(availableresources.
                get(selectedIndexRes).getResourceId());
        farmondto.setResourceCropDTO(resourceCrop);
        farmondto = clientService.callResCropPerResService(farmondto);
        List<ResourceCropDTO> listforRes = farmondto.getRescroplist();
        if (listforRes.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "This resource is not applied so far.");
            f.addMessage(null, message);
            // Do the redirect here:
            f.getExternalContext().redirect(redirectUrl);
            f.responseComplete();
        }
        FarmresourceDTO farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(availableresources.get(selectedIndexRes)
                .getResourceId());
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        unit = farmondto.getFarmresourcerec().getUnit();
//        if (farmondto.getFarmresourcerec().getCropwtunit() != null) {
//            rescat = "Crop";
//            cropwt = farmondto.getFarmresourcerec().getCropweight();
//            cropwtunit = farmondto.getFarmresourcerec().getCropwtunit();
//
//        } else {
//            rescat = "Other";
//            cropwt = "";
//            cropwtunit = "";
//        }
    }

    public String resourceDetails() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(startDt==null||endDt==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/reports/appliedresdetails?faces-redirect=true";
        }
        String startDate = sdf.format(startDt);
        String endDate = sdf.format(endDt);
        String redirectUrl = "/secured/reports/appliedresdtlreport?faces-redirect=true&resourceId=" + availableresources.
                get(selectedIndexRes).getResourceId() 
                + "&startDt=" + startDate + "&endDt=" + endDate;
        return redirectUrl; 
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

    public List<FarmresourceDTO> getAvailableresources() {
        return availableresources;
    }

    public void setAvailableresources(List<FarmresourceDTO> availableresources) {
        this.availableresources = availableresources;
    }

    public int getSelectedIndexRes() {
        return selectedIndexRes;
    }

    public void setSelectedIndexRes(int selectedIndexRes) {
        this.selectedIndexRes = selectedIndexRes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public boolean isMessageShown() {
        return messageShown;
    }

    public void setMessageShown(boolean messageShown) {
        this.messageShown = messageShown;
    }
    
    
}
