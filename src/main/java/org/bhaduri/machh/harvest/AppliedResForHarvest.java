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
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.ResourceCropDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "appliedResForHarvest")
@ViewScoped
public class AppliedResForHarvest implements Serializable {
    private List<ResourceCropDTO> appliedresources;
    private String appliedHarvest;   
    private String sitename;
    private String cropcat;
    private String cropname;
    private ResourceCropDTO appliedRes;
    /**
     * Creates a new instance of AppliedResForHarvest
     */
    public AppliedResForHarvest() {
    }
    
    public String fillResourceValues() throws NamingException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        HarvestDTO harvestRecord = new HarvestDTO();
        harvestRecord.setHarvestid(appliedHarvest);
        farmondto.setHarvestrecord(harvestRecord);
        farmondto = clientService.callHarvestRecService(farmondto);
        harvestRecord = farmondto.getHarvestrecord();
        
//        MasterDataServices masterDataService = new MasterDataServices();
//        HarvestDTO harvestRecord = masterDataService.getHarvestRecForId(appliedHarvest);
        sitename = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
        
        
        farmondto = clientService.callResCropListService(farmondto);
        appliedresources = farmondto.getRescroplist();        
        String redirectUrl = "/secured/harvest/resourceapply?faces-redirect=true&selectedHarvest=" + appliedHarvest;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(appliedresources==null||appliedresources.isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No resource is applied for this harvest.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
    
//    public String goToEdit(){
//        String redirectUrl = "/secured/harvest/resourcecropedit?faces-redirect=true&selectedRescrop=" + 
//                appliedresources.get(appliedresources.size()-1).getApplicationId();
////        String redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
//        return redirectUrl;
//    }
//    
//    public String deleteResource() throws NamingException{
//        String redirectUrl = "/secured/harvest/appliedresperharvest?faces-redirect=true&appliedHarvest=" + appliedHarvest;
//        int sqlFlag = 0;
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true);
//        
//        
//        MasterDataServices masterDataService = new MasterDataServices();
//        ResourceCropDTO resourceCrop = new ResourceCropDTO();
//        resourceCrop.setApplicationId(appliedRes.getApplicationId());
//
//        FarmresourceDTO resourceRecord = masterDataService.
//                getResourceNameForId(Integer.parseInt(appliedRes.getResourceId()));
//        float farmResourceAmt = Float.parseFloat(resourceRecord.getAvailableAmt())
//                + Float.parseFloat(appliedRes.getAppliedAmount());
//        resourceRecord.setAvailableAmt(String.format("%.2f", farmResourceAmt));
//        
//        int delres = masterDataService.delResCropRecord(resourceCrop);
//
//                
//
//        if (delres == SUCCESS) {
//            sqlFlag = sqlFlag + 1;
//        } else {
//            if (delres == DB_SEVERE) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                        "resourcecrop record could not be deleted");
//                f.addMessage(null, message);
//            }
//            return redirectUrl;
//        }
//        if (sqlFlag == 1) {
//            int updRes = masterDataService.editResource(resourceRecord);
//            if (updRes == SUCCESS) {
//                sqlFlag = sqlFlag + 1;
//            } else {
//                if (updRes == DB_SEVERE) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                            "resourcecrop record could not be updated");
//                    f.addMessage(null, message);
//                    
//                }
//                int addres = masterDataService.addResCropRecord(resourceCrop);
//                if (addres == DB_SEVERE) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
//                            "Failure on resourcecrop table correction");
//                    f.addMessage(null, message);
//                }
//                return redirectUrl;
//            }
//        }
//        if (sqlFlag == 2) {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
//                    "resourcecrop record deleted successfully");
//            f.addMessage(null, message);
//        }
//        return redirectUrl;
//    }
    public List<ResourceCropDTO> getAppliedresources() {
        return appliedresources;
    }

    public void setAppliedresources(List<ResourceCropDTO> appliedresources) {
        this.appliedresources = appliedresources;
    }

    

    public String getAppliedHarvest() {
        return appliedHarvest;
    }

    public void setAppliedHarvest(String appliedHarvest) {
        this.appliedHarvest = appliedHarvest;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getCropcat() {
        return cropcat;
    }

    public void setCropcat(String cropcat) {
        this.cropcat = cropcat;
    }

    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public ResourceCropDTO getAppliedRes() {
        return appliedRes;
    }

    public void setAppliedRes(ResourceCropDTO appliedRes) {
        this.appliedRes = appliedRes;
    }
    
}
