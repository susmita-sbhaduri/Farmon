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
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.TaskPlanDTO;

/**
 *
 * @author sb
 */
@Named(value = "taskCopy")
@ViewScoped
public class TaskCopy implements Serializable {
    private String selectedTask;    
    private Date taskDt;
    TaskPlanDTO taskRecord;
    /**
     * Creates a new instance of TaskCopy
     */
    public TaskCopy() {
    }
    public void fillValues() throws ParseException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        taskRecord = new TaskPlanDTO();
        taskRecord.setTaskId(selectedTask);
        farmondto.setTaskplanrec(taskRecord);        
        farmondto = clientService.callTaskplanIdService(farmondto);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        taskRecord = farmondto.getTaskplanrec();
        taskDt = sdf.parse(taskRecord.getTaskDt());
    }
    
    public String saveTask() {        
        String redirectUrl = "/secured/taskplan/openschedule?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        TaskPlanDTO taskplanRec = new TaskPlanDTO();
        farmondto.setTaskplanrec(taskplanRec);
        farmondto = clientService.callMaxTaskplanIdService(farmondto);
        
        int taskid = Integer.parseInt(farmondto.getTaskplanrec().getTaskId());
        if (taskid == 0) {
            taskplanRec.setTaskId(String.valueOf("1"));
        } else {
            taskplanRec.setTaskId(String.valueOf(taskid + 1));
        }
        taskplanRec.setTaskName(taskRecord.getTaskName());
        taskplanRec.setHarvestId(taskRecord.getHarvestId());
        if (taskRecord.getTaskType().equals("RES")) {
            taskplanRec.setTaskType("RES");
            taskplanRec.setResourceId(taskRecord.getResourceId());
            taskplanRec.setAppliedAmount(taskRecord.getAppliedAmount());
            taskplanRec.setAppliedAmtCost(null);
            taskplanRec.setComments(null);
        }
        if (taskRecord.getTaskType().equals("LAB")) {
            taskplanRec.setTaskType("LAB");
            taskplanRec.setResourceId(null);
            taskplanRec.setAppliedAmount(null);
            taskplanRec.setAppliedAmtCost(null);
            taskplanRec.setComments(taskRecord.getComments());
        }
        if (taskRecord.getTaskType().equals("LABHRVST")) {
            taskplanRec.setTaskType("LABHRVST");
            taskplanRec.setResourceId(null);
            taskplanRec.setAppliedAmount(null);
            taskplanRec.setAppliedAmtCost(taskRecord.getAppliedAmtCost());
            taskplanRec.setComments(taskRecord.getComments());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        taskplanRec.setTaskDt(sdf.format(taskDt));
        taskplanRec.setAppliedFlag(null);
        
        farmondto.setTaskplanrec(taskplanRec);
        farmondto = clientService.callAddTaskplanService(farmondto);
        int response = farmondto.getResponses().getFarmon_ADD_RES();
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

        }
        return redirectUrl;
    }
    public String getSelectedTask() {
        return selectedTask;
    }

    public void setSelectedTask(String selectedTask) {
        this.selectedTask = selectedTask;
    }

    public Date getTaskDt() {
        return taskDt;
    }

    public void setTaskDt(Date taskDt) {
        this.taskDt = taskDt;
    }

    public TaskPlanDTO getTaskRecord() {
        return taskRecord;
    }

    public void setTaskRecord(TaskPlanDTO taskRecord) {
        this.taskRecord = taskRecord;
    }
    
    
}
