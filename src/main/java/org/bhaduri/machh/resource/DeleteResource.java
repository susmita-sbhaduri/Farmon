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
import org.farmon.farmondto.ShopResDTO;

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
    private FarmresourceDTO farmresrec;
    
    /**
     * Creates a new instance of DeleteResource
     */
    public DeleteResource() {
    }
    
    public void fillExistingDetails() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmresrec = new FarmresourceDTO();
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
        int sqlFlag = 0;
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        FarmresourceDTO resourceRec = new FarmresourceDTO();
        resourceRec.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(resourceRec);
        farmondto = clientService.callDelFarmresService(farmondto);
        
        int response = farmondto.getResponses().getFarmon_DEL_RES();
        if (response == SUCCESS) {
            sqlFlag = sqlFlag + 1;
           
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
        
        if (sqlFlag == 1) {
            ShopResDTO shopresrec = new ShopResDTO();
            shopresrec.setResourceId(selectedRes);
            farmondto.setShopresrec(shopresrec);
            farmondto = clientService.callDelShopresService(farmondto);            
            int resres = farmondto.getResponses().getFarmon_DEL_RES();
            if (resres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (resres == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Shop Resource record does not exist");
                    f.addMessage(null, message);
                }
                if (resres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "Failure on Shop Resource update");
                    f.addMessage(null, message);
                }
                farmondto.setFarmresourcerec(farmresrec);
                farmondto = clientService.callAddFarmresService(farmondto);
                int delres = farmondto.getResponses().getFarmon_ADD_RES();
                if (delres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                            "Farm resource record could not be deleted");
                    f.addMessage(null, message);
                }
                return redirectUrl;
            }
        }
        if (sqlFlag == 2) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Resource is deleted successfully");
            f.addMessage(null, message);
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
