/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

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
import org.bhaduri.machh.DTO.ResourceCropDTO;
import org.bhaduri.machh.DTO.TaskPlanDTO;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "taskListDetails")
@ViewScoped
public class TaskListDetails implements Serializable {
    List<TaskPlanDTO> tasks;
    private String startDt;
    private String endDt;
    public TaskListDetails() {
    }
    public String fillValues() throws NamingException, ParseException {
        String redirectUrl = "/secured/reports/taskdetails?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        Date startDate;
        Date endDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        startDate = formatter.parse(startDt);
        endDate = formatter.parse(endDt);
        MasterDataServices masterDataService = new MasterDataServices();
        tasks = masterDataService.getTaskdDetailsBetweenDates(startDate, endDate);
        if (tasks.isEmpty() || tasks == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "No applied resource found for this date range.");
            f.addMessage(null, message);
            return redirectUrl;
        } else
            return null;        
    }

    public List<TaskPlanDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskPlanDTO> tasks) {
        this.tasks = tasks;
    }

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }
    
}


