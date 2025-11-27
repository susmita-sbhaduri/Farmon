/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.resource;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.ShopDTO;
import org.farmon.farmondto.ShopResDTO;
import org.farmon.farmondto.TaskPlanDTO;

/**
 *
 * @author sb
 */
@Named(value = "deleteResource")
@ViewScoped
public class DeleteResource implements Serializable {
    private String selectedRes;
    private String resname;
    private String unit;
    private List<ShopResDTO> shopreslist;
    
    
    /**
     * Creates a new instance of DeleteResource
     */
    public DeleteResource() {
    }
    
    public void fillExistingDetails() throws NamingException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        FarmresourceDTO farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        resname = farmondto.getFarmresourcerec().getResourceName();
        unit = farmondto.getFarmresourcerec().getUnit();
        
        ShopResDTO shopresrec = new ShopResDTO();
        shopresrec.setResourceId(selectedRes);
        farmondto.setShopresrec(shopresrec);
        farmondto = clientService.callShopResForResidService(farmondto);        
        shopreslist = farmondto.getShopreslist();
        
    }
    
    public String deleteRes() {
        
        String redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        TaskPlanDTO taskplanRec = new TaskPlanDTO();
        taskplanRec.setTaskId(selectedTask);
        farmondto.setTaskplanrec(taskplanRec);        
        farmondto = clientService.callDeleteTaskplanService(farmondto);
        
        int response = farmondto.getResponses().getFarmon_DEL_RES();
        if (response == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Task is deleted successfully");
            f.addMessage(null, message);
        } else {
            if (response == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Task does not exist.");
                f.addMessage(null, message);
            }
            if (response == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Failure on adding task");
                f.addMessage(null, message);
            }

        }
        return redirectUrl;
        
    }

    public String getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    public String getResname() {
        return resname;
    }

    public void setResname(String resname) {
        this.resname = resname;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<ShopResDTO> getShopreslist() {
        return shopreslist;
    }

    public void setShopreslist(List<ShopResDTO> shopreslist) {
        this.shopreslist = shopreslist;
    }    
    
}
