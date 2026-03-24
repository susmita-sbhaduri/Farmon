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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainHarvest")
@ViewScoped
public class MaintainHarvest implements Serializable {
    private HarvestDTO selectedHarvest;
    List<HarvestDTO> harvests;
    private Map<String, Boolean> shopEditable = new HashMap<>();
    /**
     * Creates a new instance of MaintainHarvest
     */
    public MaintainHarvest() {
    }
    public String fillValues() throws NamingException {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callActiveHarvestListService(farmondto);
        
        harvests = farmondto.getHarvestlist();
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
    public String goHarvestDetails(){
        String redirectUrl = "/secured/harvest/harvestdetails?faces-redirect=true&selectedHarvest=" + selectedHarvest.getHarvestid();
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
