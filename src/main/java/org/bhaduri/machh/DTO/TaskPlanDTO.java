/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.machh.DTO;

import org.farmon.farmondto.HarvestDTO;

/**
 *
 * @author sb
 */
public class TaskPlanDTO {
    private String taskId;
    private String taskType;
    private String taskName;
    private String resourceId;
    private String resourceName;
    private String resUnit;
    private String harvestId;
    private HarvestDTO harvestDto;
    private String taskDt;
    private String appliedAmount;
    private String appliedAmtCost;
    private String appliedFlag;
    private String comments;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResUnit() {
        return resUnit;
    }

    public void setResUnit(String resUnit) {
        this.resUnit = resUnit;
    }

    public String getHarvestId() {
        return harvestId;
    }

    public void setHarvestId(String harvestId) {
        this.harvestId = harvestId;
    }

    public HarvestDTO getHarvestDto() {
        return harvestDto;
    }

    public void setHarvestDto(HarvestDTO harvestDto) {
        this.harvestDto = harvestDto;
    }

    public String getTaskDt() {
        return taskDt;
    }

    public void setTaskDt(String taskDt) {
        this.taskDt = taskDt;
    }

    public String getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(String appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    public String getAppliedAmtCost() {
        return appliedAmtCost;
    }

    public void setAppliedAmtCost(String appliedAmtCost) {
        this.appliedAmtCost = appliedAmtCost;
    }

    public String getAppliedFlag() {
        return appliedFlag;
    }

    public void setAppliedFlag(String appliedFlag) {
        this.appliedFlag = appliedFlag;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }    
    
}
