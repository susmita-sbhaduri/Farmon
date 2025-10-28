/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.taskplan;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.naming.NamingException;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.farmon.farmondto.TaskPlanDTO;
import org.bhaduri.machh.services.MasterDataServices;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "taskDelete")
@ViewScoped
public class TaskDelete implements Serializable {
private String selectedTask;
    
    private String taskName;
    private String taskType;
    private String site;
    private String cropcat;
    private String cropname;
    private String resname;
    private String amount;
    private String unit;
    private String amtapplied;
    private String taskDt;
    private String rescat;
    private String cropwt;
    private String cropwtunit;
    private String appliedcost;
    private String comments;
    public TaskDelete() {
    }
    public void fillValues() throws NamingException {
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        TaskPlanDTO taskplanRec = new TaskPlanDTO();
        taskplanRec.setTaskId(selectedTask);
        farmondto.setTaskplanrec(taskplanRec);        
        farmondto = clientService.callTaskplanIdService(farmondto);
        
        taskplanRec = farmondto.getTaskplanrec();
        taskName = taskplanRec.getTaskName();
        
        HarvestDTO harvestRecord = new HarvestDTO();
        harvestRecord.setHarvestid(taskplanRec.getHarvestId());
        farmondto.setHarvestrecord(harvestRecord);
        farmondto = clientService.callHarvestRecService(farmondto);        
        harvestRecord = farmondto.getHarvestrecord();
        
//        HarvestDTO harvestRecord = masterDataService.getHarvestRecForId(taskplanRec.getHarvestId());
        site = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
        if (taskplanRec.getTaskType().equals("RES")) {
            taskType = "Resource";
            FarmresourceDTO resourceRec = new FarmresourceDTO();
            resourceRec.setResourceId(taskplanRec.getResourceId());
            farmondto.setFarmresourcerec(resourceRec);
            farmondto = clientService.callResnameForIdService(farmondto);
            
            resourceRec = farmondto.getFarmresourcerec();
            resname = resourceRec.getResourceName();
            amount = resourceRec.getAvailableAmt();
            unit = resourceRec.getUnit();
            amtapplied = taskplanRec.getAppliedAmount();
            appliedcost = "";
            comments = "";
//            appliedcost = "NA";
//            comments = "NA";
            if (resourceRec.getCropwtunit() != null) {
                rescat = "Crop";
                cropwt = resourceRec.getCropweight();
                cropwtunit = resourceRec.getCropwtunit();
            } else {
                rescat = "Other";
                cropwt = "";
                cropwtunit = "";
            }
        }
        if (taskplanRec.getTaskType().equals("LABHRVST")) {
            taskType = "Labour(to be paid)";
            appliedcost = taskplanRec.getAppliedAmtCost();
            comments = taskplanRec.getComments();
            resname = "";
            amount = "";
            unit = "Rs.";
            amtapplied = "";
            rescat = "";
            cropwt = "";
            cropwtunit = "";
        }
        if (taskplanRec.getTaskType().equals("LAB")) {
            taskType = "Labour";
            appliedcost = "";
            comments = taskplanRec.getComments();
            resname = "";
            amount = "";
            unit = "";
            amtapplied = "";
            rescat = "";
            cropwt = "";
            cropwtunit = "";
        }
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate date = LocalDate.parse(taskplanRec.getTaskDt(), inputFormat);
        taskDt = date.format(outputFormat);
    }
    
    public String deleteTask() throws NamingException {
        
        String redirectUrl = "/secured/taskplan/openschedule?faces-redirect=true";
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

    public String getSelectedTask() {
        return selectedTask;
    }

    public void setSelectedTask(String selectedTask) {
        this.selectedTask = selectedTask;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public String getResname() {
        return resname;
    }

    public void setResname(String resname) {
        this.resname = resname;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAmtapplied() {
        return amtapplied;
    }

    public void setAmtapplied(String amtapplied) {
        this.amtapplied = amtapplied;
    }

    public String getTaskDt() {
        return taskDt;
    }

    public void setTaskDt(String taskDt) {
        this.taskDt = taskDt;
    }

    public String getRescat() {
        return rescat;
    }

    public void setRescat(String rescat) {
        this.rescat = rescat;
    }

    public String getCropwt() {
        return cropwt;
    }

    public void setCropwt(String cropwt) {
        this.cropwt = cropwt;
    }

    public String getCropwtunit() {
        return cropwtunit;
    }

    public void setCropwtunit(String cropwtunit) {
        this.cropwtunit = cropwtunit;
    }

    public String getAppliedcost() {
        return appliedcost;
    }

    public void setAppliedcost(String appliedcost) {
        this.appliedcost = appliedcost;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    
}
