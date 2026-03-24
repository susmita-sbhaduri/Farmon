/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.taskplan;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.TaskPlanDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "taskView")
@ViewScoped
public class TaskView implements Serializable {
    private String selectedTask;
    
    private String taskName;
    private String taskType;
    private String site;
    private String harvestName;
    private String resname;
    private String amount;
    private String unit;
    private String amtapplied;
    private String taskDt;
    private String appliedcost;
    private String comments;
    private boolean resVisible = true; 
    private boolean labCostVisible = true;
    private boolean labCommVisible = true;
    private boolean amountVisible = true;
    public TaskView() {
    }
    public void fillValues(){
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
        harvestName = harvestRecord.getHarvestName();
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
            resVisible = true;
            amountVisible = true;            
            labCostVisible = false;
            labCommVisible = false;

        }
        if (taskplanRec.getTaskType().equals("LABHRVST")) {
            taskType = "Labour(to be paid)";
            appliedcost = taskplanRec.getAppliedAmtCost();
            comments = taskplanRec.getComments();
            unit = "Rs.";
            resVisible = false;
            amountVisible = false;            
            labCostVisible = true;
            labCommVisible = true;
        }
        if (taskplanRec.getTaskType().equals("LAB")) {
            taskType = "Labour";
            comments = taskplanRec.getComments();
            resVisible = false;
            amountVisible = false;            
            labCostVisible = false;
            labCommVisible = true;
        }
        
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate date = LocalDate.parse(taskplanRec.getTaskDt(), inputFormat);
        taskDt = date.format(outputFormat);
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

    public String getHarvestName() {
        return harvestName;
    }

    public void setHarvestName(String harvestName) {
        this.harvestName = harvestName;
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

    public boolean isResVisible() {
        return resVisible;
    }

    public void setResVisible(boolean resVisible) {
        this.resVisible = resVisible;
    }

    public boolean isLabCostVisible() {
        return labCostVisible;
    }

    public void setLabCostVisible(boolean labCostVisible) {
        this.labCostVisible = labCostVisible;
    }

    public boolean isLabCommVisible() {
        return labCommVisible;
    }

    public void setLabCommVisible(boolean labCommVisible) {
        this.labCommVisible = labCommVisible;
    }

    public boolean isAmountVisible() {
        return amountVisible;
    }

    public void setAmountVisible(boolean amountVisible) {
        this.amountVisible = amountVisible;
    }
    
}
