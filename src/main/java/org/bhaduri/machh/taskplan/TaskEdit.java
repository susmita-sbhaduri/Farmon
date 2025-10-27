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
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.farmon.farmondto.TaskPlanDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "taskEdit")
@ViewScoped
public class TaskEdit implements Serializable {
    private String selectedTask;
    private String taskName;
    private String taskType;
    private Date taskDt;
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
    private String rescat;
    private String cropwt;
    private String cropwtunit;
    /**
     * Creates a new instance of TaskEdit
     */
    public TaskEdit() {
    }
    public void fillValues() throws NamingException, ParseException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        TaskPlanDTO taskRecord = new TaskPlanDTO();
        taskRecord.setTaskId(selectedTask);
        farmondto.setTaskplanrec(taskRecord);        
        farmondto = clientService.callTaskplanIdService(farmondto);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        taskRecord = farmondto.getTaskplanrec();
        taskDt = sdf.parse(taskRecord.getTaskDt());
        taskType = taskRecord.getTaskType();
        taskName = taskRecord.getTaskName();
                
        farmondto = clientService.callActiveHarvestListService(farmondto);
        activeharvests = farmondto.getHarvestlist();
        
        for (int i = 0; i < activeharvests.size(); i++) {            
            if (activeharvests.get(i).getHarvestid().equals(taskRecord.getHarvestId())) {
                selectedIndexHarvest = i;
                break;
            }
        }
        
//        farmondto = clientService.callNonzeroresListService(farmondto);
//        availableresources = farmondto.getFarmresourcelist();
//        for (int i = 0; i < availableresources.size(); i++) {            
//            if (availableresources.get(i).getResourceId().equals(taskRecord.getResourceId())) {
//                selectedIndexRes = i;
//                break;
//            }
//        }
        
        if (taskType.equals("LABHRVST")) {
            resReadonly = true;
            costReadonly = false;
            commReadonly = false;
            taskType = "Labour(to be paid)";
            unit = "Rs.";
            amount = "NA";
            rescat = "NA";
            cropwt = "NA";
            cropwtunit = "NA";
            comments = taskRecord.getComments();
        }
        if (taskType.equals("LAB")) {
            resReadonly = true;
            costReadonly = true;
            commReadonly = false;
            taskType = "Labour";
            unit = "NA";
            amount = "NA";
            rescat = "NA";
            cropwt = "NA";
            cropwtunit = "NA";
            comments = taskRecord.getComments();
        }
        if (taskType.equals("RES")) {
            resReadonly = false;
            costReadonly = true;
            commReadonly = true;
            taskType = "Resource";
            
            farmondto = clientService.callNonzeroresListService(farmondto);
            availableresources = farmondto.getFarmresourcelist();
            for (int i = 0; i < availableresources.size(); i++) {
                if (availableresources.get(i).getResourceId().equals(taskRecord.getResourceId())) {
                    selectedIndexRes = i;
                    break;
                }
            }
                        
            FarmresourceDTO farmresrec = new FarmresourceDTO();
            farmresrec.setResourceId(availableresources.get(selectedIndexRes)
                    .getResourceId());
            farmondto.setFarmresourcerec(farmresrec);
            farmondto = clientService.callResnameForIdService(farmondto);
            
            unit = farmondto.getFarmresourcerec().getUnit();
            amount = farmondto.getFarmresourcerec().getAvailableAmt();
            if (farmondto.getFarmresourcerec().getCropwtunit() != null) {
                rescat = "Crop";
                cropwt = farmondto.getFarmresourcerec().getCropweight();
                cropwtunit = farmondto.getFarmresourcerec().getCropwtunit();
            } else {
                rescat = "Other";
                cropwt = "";
                cropwtunit = "";
            }
        }
        if (taskRecord.getAppliedAmount()==null)
            amtapplied = 0;
        else
            amtapplied = Float.parseFloat(taskRecord.getAppliedAmount());
        
        if (taskRecord.getAppliedAmtCost()==null)
            appliedcost = 0;
        else
            appliedcost = Float.parseFloat(taskRecord.getAppliedAmtCost());
    }
    public void onResourceChange() throws NamingException {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        FarmresourceDTO farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(availableresources.get(selectedIndexRes)
                .getResourceId());
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        unit = farmondto.getFarmresourcerec().getUnit();
        amount = farmondto.getFarmresourcerec().getAvailableAmt();
        amtapplied = 0;
        if(farmondto.getFarmresourcerec().getCropwtunit()!=null){
            rescat = "Crop";
            cropwt = farmondto.getFarmresourcerec().getCropweight();
            cropwtunit = farmondto.getFarmresourcerec().getCropwtunit();
            
        } else{ 
            rescat = "Other";
            cropwt = "";
            cropwtunit = "";
        }
    }
    
    public String saveTask() throws NamingException {
        
        String redirectUrl = "/secured/taskplan/taskedit?faces-redirect=true&selectedTask=" + selectedTask;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
//        Date today = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(today);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//
//        Date todayAtMidnight = cal.getTime();
//        if (taskDt.before(todayAtMidnight)) {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                    "Task cannot be added in a past date.");
//            f.addMessage(null, message);
//            return redirectUrl;
//        }
        if (taskName.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Task name is mandatory.");
            f.addMessage("taskname", message);
            return redirectUrl;
        }
        if (taskType.equals("Resource") && amtapplied == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide non-zero resource amount.");
            f.addMessage("amtapplied", message);
            return redirectUrl;
        }
        if (taskType.equals("Labour(to be paid)") && appliedcost == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide non-zero cost.");
            f.addMessage("amtapplied", message);
            return redirectUrl;
        }
//        int sqlFlag = 0;
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        TaskPlanDTO taskplanRec = new TaskPlanDTO();
        taskplanRec.setTaskId(selectedTask);
        farmondto.setTaskplanrec(taskplanRec);        
        farmondto = clientService.callTaskplanIdService(farmondto);
        
        taskplanRec = farmondto.getTaskplanrec();
        //#######editted taskplan record construction ########
        taskplanRec.setTaskId(selectedTask);
        taskplanRec.setHarvestId(activeharvests.get(selectedIndexHarvest).getHarvestid());
        taskplanRec.setTaskName(taskName);
        
        if (taskplanRec.getTaskType().equals("RES")) {
            taskplanRec.setResourceId(availableresources.get(selectedIndexRes).getResourceId());
            taskplanRec.setAppliedAmount(String.format("%.2f", amtapplied));
            taskplanRec.setAppliedAmtCost(null);
            taskplanRec.setComments(null);
        }
        if (taskplanRec.getTaskType().equals("LABHRVST")) {
            taskplanRec.setResourceId(null);
            taskplanRec.setAppliedAmount(null);
            taskplanRec.setAppliedAmtCost(String.format("%.2f", appliedcost));
            taskplanRec.setComments(comments);
        }
        if (taskplanRec.getTaskType().equals("LAB")) {
            taskplanRec.setResourceId(null);
            taskplanRec.setAppliedAmount(null);
            taskplanRec.setAppliedAmtCost(null);
            taskplanRec.setComments(comments);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        taskplanRec.setTaskDt(sdf.format(taskDt));
        taskplanRec.setAppliedFlag(null);
        
        farmondto.setTaskplanrec(taskplanRec);
        farmondto = clientService.callEditTaskplanService(farmondto);
        int response = farmondto.getResponses().getFarmon_EDIT_RES();
//        int response = masterDataService.editTaskplanRecord(taskplanRec);
        redirectUrl = "/secured/taskplan/openschedule?faces-redirect=true";
        if (response == SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Task is updated successfully");
            f.addMessage(null, message);
        } else {
            if (response == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Task does not exist.");
                f.addMessage(null, message);
            }
            if (response == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                         "Failure on updating task");
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

    public Date getTaskDt() {
        return taskDt;
    }

    public void setTaskDt(Date taskDt) {
        this.taskDt = taskDt;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isCommReadonly() {
        return commReadonly;
    }

    public void setCommReadonly(boolean commReadonly) {
        this.commReadonly = commReadonly;
    }
    
    
}
