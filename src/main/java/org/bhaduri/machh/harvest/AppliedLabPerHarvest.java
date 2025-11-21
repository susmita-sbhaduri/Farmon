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
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.LabourCropDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "appliedLabPerHarvest")
@ViewScoped
public class AppliedLabPerHarvest implements Serializable {
    private List<LabourCropDTO> appliedlabours;
    private String appliedHarvest;   
    private String sitename;
    private String cropcat;
    private String cropname;
    private LabourCropDTO appliedLabour;
    /**
     * Creates a new instance of AppliedLabPerHarvest
     */
    public AppliedLabPerHarvest() {
    }
    public String fillResourceValues() {
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
        
        farmondto = clientService.callLabCropListService(farmondto);
        appliedlabours = farmondto.getLabcroplist();        
//        String redirectUrl = "/secured/harvest/laborapply?faces-redirect=true&selectedHarvest=" + appliedHarvest;
        String redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if(appliedlabours==null||appliedlabours.isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No labor is applied for this harvest.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
//    <!--                    <p:column>
//                        <p:commandButton styleClass="blue-button" 
//                                         icon="fa fa-edit" 
//                                         value="Edit"
//                                         action="#{appliedLabPerHarvest.editLabour()}" >
//                            <f:setPropertyActionListener value="#{appliedlabour}" target="#{appliedLabPerHarvest.appliedLabour}" />    
//                        </p:commandButton>
//                    </p:column>
//                    <p:column>
//                        <p:commandButton styleClass="blue-button" 
//                                         icon="fa fa-edit" 
//                                         value="Delete"
//                                         action="#{appliedLabPerHarvest.deleteLabour()}" >
//                            <f:setPropertyActionListener value="#{appliedlabour}" target="#{appliedLabPerHarvest.appliedLabour}" />    
//                        </p:commandButton>
//                    </p:column>-->
//    public String editLabour() {
//
//         String redirectUrl = "/secured/harvest/labourcropedit?faces-redirect=true&selectedLabcrop=" 
//                 + appliedLabour.getApplicationId();
//        return redirectUrl;
//    }
//    
//    public String deleteLabour() throws NamingException {
//
//        String redirectUrl = "/secured/harvest/appliedlabperharvest?faces-redirect=true&appliedHarvest=" + appliedHarvest;
//        int sqlFlag = 0;
//        FacesMessage message;
//        FacesContext f = FacesContext.getCurrentInstance();
//        f.getExternalContext().getFlash().setKeepMessages(true);
//        MasterDataServices masterDataService = new MasterDataServices();
//        LabourCropDTO labourCrop = new LabourCropDTO();
//        labourCrop.setApplicationId(appliedLabour.getApplicationId());
//
//        String labourCategory = "LABHRVST";
//        ExpenseDTO expenseRecord = masterDataService
//                .getLabExpenseForHrvst(labourCrop.getApplicationId(), labourCategory);
//        
//        int dellabcrop = masterDataService.delLabourCropRecord(labourCrop);
//        
//        if (dellabcrop == SUCCESS){ 
//            sqlFlag = sqlFlag+1;
//        } else {             
//            if (dellabcrop == DB_SEVERE) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                        "laborcrop record could not be deleted");
//                f.addMessage(null, message);
//            }
//            return redirectUrl;
//        }    
//        if (sqlFlag == 1) {            
//            int delexp = masterDataService.delExpenseRecord(expenseRecord);
//            if (delexp == SUCCESS) {
//                sqlFlag = sqlFlag + 1;
//                
//            } else {
//                if (delexp == DB_SEVERE) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                        "laborcrop record could not be updated");
//                    f.addMessage(null, message);
//                }
//                int addlabour = masterDataService.addLabourCropRecord(labourCrop);
//                if (addlabour == DB_SEVERE) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
//                            "Failure on laborcrop table correction");
//                    f.addMessage(null, message);
//                }
//                return redirectUrl;
//            }
//        }
//        if (sqlFlag == 2) {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
//                    "laborcrop record deleted successfully");
//            f.addMessage(null, message);
//        }
//        return redirectUrl;
//    } 
    
    public List<LabourCropDTO> getAppliedlabours() {
        return appliedlabours;
    }
    
   
    public void setAppliedlabours(List<LabourCropDTO> appliedlabours) {
        this.appliedlabours = appliedlabours;
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

    public LabourCropDTO getAppliedLabour() {
        return appliedLabour;
    }

    public void setAppliedLabour(LabourCropDTO appliedLabour) {
        this.appliedLabour = appliedLabour;
    }
    
}
