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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.ExpenseDTO;
import org.farmon.farmondto.HarvestDTO;
import org.bhaduri.machh.DTO.LabourCropDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_DUPLICATE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "laborApply")
@ViewScoped
public class LaborApply implements Serializable {

    private String selectedHarvest;
    private String site;
    private String cropcat;
    private String cropname;
//    private String labCategory;
    private float amountPaid;
    private Date applyDt = new Date();
    private String comments;
    
    public LaborApply() {
    }
    public void fillValues() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        HarvestDTO harvestRecord = masterDataService.getHarvestRecForId(selectedHarvest);
        site = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
    }
    
    public String goToSaveLabor() throws NamingException {
        int sqlFlag = 0;
        String redirectUrl = "/secured/harvest/laborapply?faces-redirect=true&selectedHarvest=" + selectedHarvest;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        if (amountPaid == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure.",
                    "Amount paid for labour is mandatory.");
            f.addMessage("amtpaid", message);
            return redirectUrl;
        }
        
        MasterDataServices masterDataService = new MasterDataServices();
        
//        contruction of labouracquire record
        LabourCropDTO labCropRec = new LabourCropDTO();        
        int applicationid = masterDataService.getMaxIdForLabCrop();
        if (applicationid == 0) {
            labCropRec.setApplicationId("1");
        } else {
            labCropRec.setApplicationId(String.valueOf(applicationid + 1));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        labCropRec.setHarvestId(selectedHarvest); 
//        labCropRec.setLabourCategory(labCategory);
        labCropRec.setApplicationDate(sdf.format(applyDt));   
//        contruction of expense record
        ExpenseDTO expenseRec = new ExpenseDTO();
        int expenseid = masterDataService.getNextIdForExpense();
        if (expenseid == 0) {
            expenseRec.setExpenseId("1");
        } else {
            expenseRec.setExpenseId(String.valueOf(expenseid + 1));
        }
        expenseRec.setDate(sdf.format(applyDt));
        expenseRec.setExpenseType("LABHRVST");
        expenseRec.setExpenseRefId(labCropRec.getApplicationId()); //######labourcrop app id
        expenseRec.setExpenditure(String.format("%.2f", amountPaid));
        expenseRec.setCommString(comments);


        int labourapply = masterDataService.addLabourCropRecord(labCropRec);
        if (labourapply == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (labourapply == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "Duplicate record error for labourapply");
                f.addMessage(null, message);
            }
            if (labourapply == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        "Failure on insert in labourapply");
                f.addMessage(null, message);
            }           
//            return redirectUrl;
        }

        if (sqlFlag == 1) {
            int expres = masterDataService.addExpenseRecord(expenseRec);
            if (expres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (expres == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Failure", "Duplicate record error for expense table");
                    f.addMessage(null, message);
                }
                if (expres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Failure", "Duplicate record error for expense table");
                    f.addMessage(null, message);
                }
                int delacq = masterDataService.delLabourCropRecord(labCropRec);
                if (delacq == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure"
                            , "labourcrop record could not be deleted");
                    f.addMessage(null, message);
                }
//                redirectUrl = "/secured/resource/acquireresource?faces-redirect=true&selectedRes="
//                        + selectedShopRes.getResourceId();
//                return redirectUrl;
            }
        }        
                
        if (sqlFlag==2) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Labour applied successfully");
            f.addMessage(null, message);
        }
        return redirectUrl;
    }

    public String getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(String selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
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

    

    public float getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(float amountPaid) {
        this.amountPaid = amountPaid;
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

    
    
}
