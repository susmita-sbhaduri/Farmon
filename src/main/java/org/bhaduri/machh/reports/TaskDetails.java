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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author sb
 */
@Named(value = "taskDetails")
@ViewScoped
public class TaskDetails implements Serializable {
    private Date startDt;
    private Date endDt = new Date();
    public TaskDetails() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1); // subtract 1 month
        Date oneMonthAgo = cal.getTime();
        startDt = oneMonthAgo;
    }
    public String goToTaskListDetails() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(startDt==null||endDt==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "All the fields are mandatory");
            f.addMessage(null, message);
            return "/secured/reports/appliedresdetails?faces-redirect=true";
        }
        String startDate = sdf.format(startDt);
        String endDate = sdf.format(endDt);
        String redirectUrl = "/secured/reports/tasklistdetails?faces-redirect=true&startDt=" + startDate + "&endDt=" + endDate;
        return redirectUrl; 
    }
    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }
    
}
