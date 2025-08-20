/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.machh.DTO;

import org.farmon.farmondto.HarvestDTO;
import java.io.Serializable;

/**
 *
 * @author sb
 */
public class ResourceCropDTO implements Serializable {
    private String applicationId;
    private String resourceId;
    private String resourceName;
    private String resUnit;
    private String harvestId;
    private HarvestDTO harvestDto;
    private String applicationDt;
    private String appliedAmount;
    private String appliedAmtCost;

    public HarvestDTO getHarvestDto() {
        return harvestDto;
    }

    public void setHarvestDto(HarvestDTO harvestDto) {
        this.harvestDto = harvestDto;
    }

    
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public String getHarvestId() {
        return harvestId;
    }

    public void setHarvestId(String harvestId) {
        this.harvestId = harvestId;
    }

    public String getApplicationDt() {
        return applicationDt;
    }

    public void setApplicationDt(String applicationDt) {
        this.applicationDt = applicationDt;
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

    public String getResUnit() {
        return resUnit;
    }

    public void setResUnit(String resUnit) {
        this.resUnit = resUnit;
    }

    
    
}
