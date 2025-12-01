/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.FarmresourceDTO;

/**
 *
 * @author sb
 */
@Named(value = "resourceStock")
@ViewScoped
public class ResourceStock implements Serializable {
    List<FarmresourceDTO> existingresources;
    /**
     * Creates a new instance of ResourceStock
     */
    public ResourceStock() {
    }
    public void fillResourceValues() {
//        String redirectUrl = "/secured/resource/addinventory?faces-redirect=true";
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callFarmresListService(farmondto);
        existingresources = farmondto.getFarmresourcelist();  
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true);
//        if(existingresources.isEmpty()){            
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                    "Add one resource.");
//            f.addMessage(null, message);
//            return redirectUrl;            
//        } else {
//            return null;    
//        }
    }

    public List<FarmresourceDTO> getExistingresources() {
        return existingresources;
    }

    public void setExistingresources(List<FarmresourceDTO> existingresources) {
        this.existingresources = existingresources;
    }
    
    
}
