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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_DUPLICATE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.DTO.TaskPlanDTO;
import org.bhaduri.machh.services.MasterDataServices;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "taskAdd")
@ViewScoped
public class TaskAdd implements Serializable {
    private String selectedDate;
    private Date taskDt;    
    private String taskName;
    private String selectedTaskType = "res";
    private List<FarmresourceDTO> availableresources;
    private int selectedIndexRes;
    private List<HarvestDTO> activeharvests;
    private int selectedIndexHarvest;
    private String amount;
    private String unit;
    private float amtapplied;
    private float appliedcost;
    private String comments;
    private boolean resReadonly = false; // default not readonly
    private boolean costReadonly = false;
    private boolean commReadonly = false;    
//    private String rescat;
//    private String cropwt;
//    private String cropwtunit;
    /**
     * Creates a new instance of TaskAdd
     */
    public TaskAdd() {
    }
    public void fillValues() throws NamingException, ParseException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        MasterDataServices masterDataService = new MasterDataServices();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        taskDt = sdf.parse(selectedDate);
        farmondto = clientService.callActiveHarvestListService(farmondto);
        activeharvests = farmondto.getHarvestlist();
        farmondto = clientService.callNonzeroresListService(farmondto);
        availableresources = farmondto.getFarmresourcelist();
        
        FarmresourceDTO farmresrec = new FarmresourceDTO();
        
        farmresrec.setResourceId(availableresources.get(selectedIndexRes).getResourceId());
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        
        unit = farmondto.getFarmresourcerec().getUnit();
        amount = farmondto.getFarmresourcerec().getAvailableAmt();

    }
    
    public void onTaskTypeSelect() throws NamingException{
//        System.out.println("No crop categories are found." + selectedShop);
        if (selectedTaskType.equals("labhar")) {
            resReadonly = true;
            costReadonly = false;
            commReadonly = false;
            amount = "NA";
            unit = "Rs.";
//            rescat = "NA";
//            cropwt = "NA";
//            cropwtunit = "NA";
        }
        if (selectedTaskType.equals("lab")) {
            resReadonly = true;
            costReadonly = true;
            commReadonly = false;
            amount = "NA";
            unit = "NA";
//            rescat = "NA";
//            cropwt = "NA";
//            cropwtunit = "NA";
        }
        if (selectedTaskType.equals("res")) {
            resReadonly = false;
            costReadonly = true;
            commReadonly = true;
        }
    }
    
    public void onResourceSelect() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        unit = masterDataService.getResourceNameForId(Integer.parseInt(availableresources.
                get(selectedIndexRes).getResourceId())).getUnit();
        amount = masterDataService.getResourceNameForId(Integer.parseInt(availableresources.
                get(selectedIndexRes).getResourceId())).getAvailableAmt();
//        if (masterDataService.getResourceNameForId(Integer.parseInt(availableresources.
//                get(selectedIndexRes).getResourceId()))
//                .getCropwtunit() != null) {
//            rescat = "Crop";
//            cropwt = masterDataService.getResourceNameForId(Integer.parseInt(availableresources.
//                    get(selectedIndexRes).getResourceId()))
//                    .getCropweight();
//            cropwtunit = masterDataService.getResourceNameForId(Integer.parseInt(availableresources.
//                    get(selectedIndexRes).getResourceId()))
//                    .getCropwtunit();
//
//        } else {
//            rescat = "Other";
//            cropwt = "";
//            cropwtunit = "";
//        }
    }
    
    public String goToSaveTask() throws NamingException {
        String redirectUrl = "/secured/taskplan/openschedule?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        MasterDataServices masterDataService = new MasterDataServices();

        if (taskName.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Task name is mandatory.");
            f.addMessage("taskname", message);
            return redirectUrl;
        }
        if (selectedTaskType.equals("res") && amtapplied == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide non-zero resource amount.");
            f.addMessage("amtapplied", message);
            return redirectUrl;
        }

        if (selectedTaskType.equals("labhar") && appliedcost == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide non-zero cost.");
            f.addMessage("amtapplied", message);
            return redirectUrl;
        }

        TaskPlanDTO taskplanRec = new TaskPlanDTO();
        int taskid = masterDataService.getMaxTaskplanId();
        if (taskid == 0) {
            taskplanRec.setTaskId(String.valueOf("1"));
        } else {
            taskplanRec.setTaskId(String.valueOf(taskid + 1));
        }
        taskplanRec.setTaskName(taskName);
        taskplanRec.setHarvestId(activeharvests.get(selectedIndexHarvest).getHarvestid());
        if (selectedTaskType.equals("res")) {
            taskplanRec.setTaskType("RES");
            taskplanRec.setResourceId(availableresources.get(selectedIndexRes).getResourceId());
            taskplanRec.setAppliedAmount(String.format("%.2f", amtapplied));
            taskplanRec.setAppliedAmtCost(null);
            taskplanRec.setComments(null);
        }
        if (selectedTaskType.equals("lab")) {
            taskplanRec.setTaskType("LAB");
            taskplanRec.setResourceId(null);
            taskplanRec.setAppliedAmount(null);
            taskplanRec.setAppliedAmtCost(null);
            taskplanRec.setComments(comments);
        }
        if (selectedTaskType.equals("labhar")) {
            taskplanRec.setTaskType("LABHRVST");
            taskplanRec.setResourceId(null);
            taskplanRec.setAppliedAmount(null);
            taskplanRec.setAppliedAmtCost(String.format("%.2f", appliedcost));
            taskplanRec.setComments(comments);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        taskplanRec.setTaskDt(sdf.format(taskDt));
        taskplanRec.setAppliedFlag(null);
        
        int response = masterDataService.addTaskplanRecord(taskplanRec);
        if (response == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Task is added to the date successfully");
            f.addMessage(null, message);
        } else {
            if (response == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Task exists already.");
                f.addMessage(null, message);
            }
            if (response == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Failure on adding task");
                f.addMessage(null, message);
            }
//            redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";

        }
        return redirectUrl;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Date getTaskDt() {
        return taskDt;
    }

    public void setTaskDt(Date taskDt) {
        this.taskDt = taskDt;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSelectedTaskType() {
        return selectedTaskType;
    }

    public void setSelectedTaskType(String selectedTaskType) {
        this.selectedTaskType = selectedTaskType;
    }

    public List<FarmresourceDTO> getAvailableresources() {
        return availableresources;
    }

    public void setAvailableresources(List<FarmresourceDTO> availableresources) {
        this.availableresources = availableresources;
    }

    public int getSelectedIndexRes() {
        return selectedIndexRes;
    }

    public void setSelectedIndexRes(int selectedIndexRes) {
        this.selectedIndexRes = selectedIndexRes;
    }

    public List<HarvestDTO> getActiveharvests() {
        return activeharvests;
    }

    public void setActiveharvests(List<HarvestDTO> activeharvests) {
        this.activeharvests = activeharvests;
    }

    public int getSelectedIndexHarvest() {
        return selectedIndexHarvest;
    }

    public void setSelectedIndexHarvest(int selectedIndexHarvest) {
        this.selectedIndexHarvest = selectedIndexHarvest;
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

    public float getAmtapplied() {
        return amtapplied;
    }

    public void setAmtapplied(float amtapplied) {
        this.amtapplied = amtapplied;
    }

    public float getAppliedcost() {
        return appliedcost;
    }

    public void setAppliedcost(float appliedcost) {
        this.appliedcost = appliedcost;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isResReadonly() {
        return resReadonly;
    }

    public void setResReadonly(boolean resReadonly) {
        this.resReadonly = resReadonly;
    }

    public boolean isCostReadonly() {
        return costReadonly;
    }

    public void setCostReadonly(boolean costReadonly) {
        this.costReadonly = costReadonly;
    }

    public boolean isCommReadonly() {
        return commReadonly;
    }

    public void setCommReadonly(boolean commReadonly) {
        this.commReadonly = commReadonly;
    }

}
