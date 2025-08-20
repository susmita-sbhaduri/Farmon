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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.ExpenseDTO;
import org.bhaduri.machh.DTO.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import org.bhaduri.machh.DTO.LabourCropDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "labourCropEdit")
@ViewScoped
public class LabourCropEdit implements Serializable {

    private String selectedLabcrop;
    private String site;
    private String cropcat;
    private String cropname;
    private float amountpaid;
    private Date applyDt = new Date();
    private String comments;
    private ExpenseDTO expensePrev;
    private LabourCropDTO labcropPrev;
    /**
     * Creates a new instance of LabourCropEdit
     */
    public LabourCropEdit() {
    }
    public void fillValues() throws NamingException, ParseException {
        MasterDataServices masterDataService = new MasterDataServices();
        labcropPrev = masterDataService.getLabCropForId(selectedLabcrop);
        HarvestDTO harvestRecord = masterDataService
                .getHarvestRecForId(labcropPrev.getHarvestId());
        site = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
       
        // for autofill        
        this.amountpaid = Float.parseFloat(labcropPrev.getAppliedAmount());
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        this.applyDt = formatter.parse(labcropPrev.getApplicationDate());
        this.comments = labcropPrev.getExpenseComments();
    }

    public String goToSave() throws NamingException {
        
        String redirectUrl = "/secured/harvest/appliedlabperharvest?faces-redirect=true&appliedHarvest=" 
                + labcropPrev.getHarvestId();
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        if (amountpaid == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure.",
                    "Labour payment cannot be zero.");
            f.addMessage("amountpaid", message);
            return redirectUrl;
        }
        
        int sqlFlag = 0;
        MasterDataServices masterDataService = new MasterDataServices();
        LabourCropDTO labourrRecord = masterDataService.getLabCropForId(selectedLabcrop);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // for labourcrop table
        labourrRecord.setApplicationDate(sdf.format(applyDt));
        
        String labourCategory = "LABHRVST";
        ExpenseDTO expenseRec = masterDataService.getLabExpenseForHrvst(selectedLabcrop, labourCategory);
        expenseRec.setCommString(comments);
        expenseRec.setExpenditure(String.format("%.2f", amountpaid));
        
        int labcropres = masterDataService.editLabCropRecord(labourrRecord);
        
        if (labcropres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (labcropres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure" 
                                                , "Labourcrop record doesn't exist");
                f.addMessage(null, message);
            }
            if (labcropres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            }
            return redirectUrl;
        }
        
        
        if (sqlFlag == 1) {
            int expres = masterDataService.editExpenseRecord(expenseRec);
            if (expres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (expres == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", "Labour record does not exist");
                    f.addMessage(null, message);
                }
                if (expres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", "Failure on labor record update");
                    f.addMessage(null, message);
                }
//              revert the changes in amount for labourcrop table
                labcropres = masterDataService.editLabCropRecord(labcropPrev);                
                if (labcropres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "Failure on laborcrop table update");
                    f.addMessage(null, message);
                }
//                redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
                return redirectUrl;
            }
        }
        if (sqlFlag == 2) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Labour expense updated successfully with application ID=" + selectedLabcrop);
            f.addMessage(null, message);
        }
        return redirectUrl;        
    }
    
    public String goToAppliedLab() {
        String redirectUrl = "/secured/harvest/appliedlabperharvest?faces-redirect=true&appliedHarvest=" 
                + labcropPrev.getHarvestId();
        return redirectUrl; 
    }
    public String getSelectedLabcrop() {
        return selectedLabcrop;
    }

    public void setSelectedLabcrop(String selectedLabcrop) {
        this.selectedLabcrop = selectedLabcrop;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
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

    public float getAmountpaid() {
        return amountpaid;
    }

    public void setAmountpaid(float amountpaid) {
        this.amountpaid = amountpaid;
    }

    

    public Date getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(Date applyDt) {
        this.applyDt = applyDt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ExpenseDTO getExpensePrev() {
        return expensePrev;
    }

    public void setExpensePrev(ExpenseDTO expensePrev) {
        this.expensePrev = expensePrev;
    }

    public LabourCropDTO getLabcropPrev() {
        return labcropPrev;
    }

    public void setLabcropPrev(LabourCropDTO labcropPrev) {
        this.labcropPrev = labcropPrev;
    }
    
}
