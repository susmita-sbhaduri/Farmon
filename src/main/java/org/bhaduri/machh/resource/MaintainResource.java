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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.ResAcquireDTO;

/**
 *
 * @author sb
 */
@Named(value = "maintainResource")
@ViewScoped
public class MaintainResource implements Serializable {
    private FarmresourceDTO selectedRes;
    List<FarmresourceDTO> existingresources;
    private Map<String, Boolean> resEditable = new HashMap<>();
    
    public MaintainResource() {
    }
    public void fillResourceValues() {
//        String redirectUrl = "/secured/resource/addinventory?faces-redirect=true";
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callFarmresListService(farmondto);
        existingresources = farmondto.getFarmresourcelist();      
        
        farmondto = clientService.callDisResacqPerResService(farmondto);
        List<ResAcquireDTO> acqreslist = farmondto.getResacqreclist();
        
//      Resources which are ALREADY in acqreslist cannot be deleted
        
        for (FarmresourceDTO res : existingresources) {
            boolean deletable = true;
            for (ResAcquireDTO resacq : acqreslist) {
                if (resacq.getResoureId().equals(res.getResourceId())) {
                     deletable =false;
                     break;
                }
            }            
            resEditable.put(res.getResourceId(), deletable);
        }
        
        // Remove from existingresources all resources that are ALREADY in acqreslist
//             
//        existingresources.removeIf(res -> {
//            for (ResAcquireDTO resacq : acqreslist) {  // nested loop check
//                if (resacq.getResoureId().equals(res.getResourceId())) {
//                    return true;  // remove this resource
//                }
//            }
//            return false;
//        });
        
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
    
//    public String deleteRes() throws NamingException {
//        String redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
//        FacesMessage message = null;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true);
//        MasterDataServices masterDataService = new MasterDataServices();
//        int shopres = masterDataService.deleteShopResForResid(selectedRes.getResourceId());
//        int res = masterDataService.delResource(selectedRes);
//        
//        if (res == SUCCESS && shopres == SUCCESS) {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Inventory deleted", Integer.toString(SUCCESS));
////            redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
//        } else {
//            if (res == DB_NON_EXISTING || shopres == DB_NON_EXISTING) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Record does not exist", Integer.toString(DB_NON_EXISTING));
//            }
//            if (res == DB_SEVERE || shopres == DB_SEVERE) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", Integer.toString(DB_SEVERE));
//            }
//        }
//        f.addMessage(null, message);
//        return redirectUrl;
//    }
    
//    public String goToShopForRes() {        
//        String redirectUrl = "/secured/shop/reshoplist?faces-redirect=true&resourceId=" + selectedRes.getResourceId()+ "&resourceName=" + selectedRes.getResourceName();
//        return redirectUrl;
////        return "/secured/userhome";
//    }
    
    public String acquireRes() {        
        String redirectUrl = "/secured/resource/acquireresource?faces-redirect=true&selectedRes="+ selectedRes.getResourceId();
        return redirectUrl;
//        return "/secured/userhome";
    }
    
    public String deleteRes() {  
//        FarmonDTO farmondto= new FarmonDTO();
//        FarmonClient clientService = new FarmonClient();
//        FarmresourceDTO farmresrec = new FarmresourceDTO();
//        farmresrec.setResourceId(selectedRes.getResourceId());
//        farmondto.setFarmresourcerec(farmresrec);
//        farmondto = clientService.callResnameForIdService(farmondto);
//        String amount = farmondto.getFarmresourcerec().getAvailableAmt();
//        
//        String redirectUrl;
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true); 
//        
//        if (amount.equals("0.00")) {
//            redirectUrl = "/secured/resource/deleteresource?faces-redirect=true&selectedRes="+ selectedRes.getResourceId();
//            return redirectUrl;
//        }
//        else {
//            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
//                    "Resource with non-zero stock cannot be deleted.");
//            f.addMessage(null, message);
//            redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
//            return redirectUrl;
//            
//        }
        String redirectUrl = "/secured/resource/deleteresource?faces-redirect=true&selectedRes="+ selectedRes.getResourceId();
        return redirectUrl;
    }
    
    public String editRes() {
        String redirectUrl = "/secured/resource/editresource?faces-redirect=true&selectedRes=" + selectedRes.getResourceId();
        return redirectUrl;

    }
    public List<FarmresourceDTO> getExistingresources() {
        return existingresources;
    }

    public void setExistingresources(List<FarmresourceDTO> existingresources) {
        this.existingresources = existingresources;
    }

    public FarmresourceDTO getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(FarmresourceDTO selectedRes) {
        this.selectedRes = selectedRes;
    }

    public Map<String, Boolean> getResEditable() {
        return resEditable;
    }

    public void setResEditable(Map<String, Boolean> resEditable) {
        this.resEditable = resEditable;
    }
    
    
}
