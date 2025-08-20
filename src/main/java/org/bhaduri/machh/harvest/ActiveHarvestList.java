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
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "activeHarvestList")
@ViewScoped
public class ActiveHarvestList implements Serializable {

    private HarvestDTO selectedHarvest;
    List<HarvestDTO> harvests;
    
    public ActiveHarvestList() {
    }

    public String fillHarvestValues() throws NamingException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        
        farmondto = clientService.callActiveHarvestListService(farmondto);
        harvests = farmondto.getHarvestlist();
//        MasterDataServices masterDataService = new MasterDataServices();
//        harvests = masterDataService.getActiveHarvestList();
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(harvests==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active harvests.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    
    public String goApplyResource(){
        String redirectUrl = "/secured/harvest/resourceapply?faces-redirect=true&selectedHarvest=" + selectedHarvest.getHarvestid();
        return redirectUrl;
    }
    
//    public String goHarvestDetails(){
//        String redirectUrl = "/secured/harvest/harvestdetails?faces-redirect=true&selectedHarvest=" + selectedHarvest.getHarvestid();
//        return redirectUrl;
//    }
    
    public String goReviewResource(){
        String redirectUrl = "/secured/harvest/appliedresperharvest?faces-redirect=true&appliedHarvest=" + selectedHarvest.getHarvestid();
        return redirectUrl;
    }
    
    public String goApplyLabour(){
        String redirectUrl = "/secured/harvest/laborapply?faces-redirect=true&selectedHarvest=" + selectedHarvest.getHarvestid();
        return redirectUrl;
    }
    
    public String goReviewLabour(){
        String redirectUrl = "/secured/harvest/appliedlabperharvest?faces-redirect=true&appliedHarvest=" + selectedHarvest.getHarvestid();
        return redirectUrl;
    }

    public HarvestDTO getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(HarvestDTO selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }   

    public List<HarvestDTO> getHarvests() {
        return harvests;
    }

    public void setHarvests(List<HarvestDTO> harvests) {
        this.harvests = harvests;
    }
    
    
}
