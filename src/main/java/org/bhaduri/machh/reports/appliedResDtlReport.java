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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.ResourceCropDTO;
import org.bhaduri.machh.services.MasterDataServices;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "appliedResDtlReport")
@ViewScoped
public class appliedResDtlReport implements Serializable {
    List<ResourceCropDTO> resapps;
    private String startDt;
    private String endDt;
    private String resourceId;
    /**
     * Creates a new instance of appliedResDtlReport
     */
    public appliedResDtlReport() {
    }
    public String fillValues() throws NamingException, ParseException {
        String redirectUrl = "/secured/reports/appliedresdetails?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto.setReportstartdt(startDt);
        farmondto.setReportenddt(endDt);
        ResourceCropDTO rescroprec = new ResourceCropDTO();
        rescroprec.setResourceId(resourceId);
        farmondto.setResourceCropDTO(rescroprec);
        
        farmondto = clientService.callResCropPerResDtService(farmondto);
        resapps = farmondto.getRescroplist();
        if (resapps.isEmpty() || resapps == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "No applied resource found for this date range.");
            f.addMessage(null, message);
            return redirectUrl;
        } else
            return null;        
    }

    public List<ResourceCropDTO> getResapps() {
        return resapps;
    }

    public void setResapps(List<ResourceCropDTO> resapps) {
        this.resapps = resapps;
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
}
