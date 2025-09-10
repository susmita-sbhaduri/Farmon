/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.taskplan;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import javax.naming.NamingException;
import org.farmon.farmondto.TaskPlanDTO;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sb
 */
@Named(value = "openSchedule")
@ViewScoped
public class OpenSchedule implements Serializable {

    private ScheduleModel taskModel = new DefaultScheduleModel();
    private List<ScheduleEvent<?>> tasksForSelectedDate;
    private LocalDateTime selectedDateTime;
    private String selectedDate;

    /**
     * Creates a new instance of OpenSchedule
     *
     * @throws javax.naming.NamingException
     */
    public OpenSchedule() throws NamingException {
//        eventModel = 
        // On initial load, fill with events for the current month
        fillTasksForMonth();
    }

    private void fillTasksForMonth() throws NamingException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        taskModel.clear();
        farmondto = clientService.callTaskplanListService(farmondto);
        
//        MasterDataServices masterDataService = new MasterDataServices();
        List<TaskPlanDTO> entries = farmondto.getTaskplanlist();

        for (TaskPlanDTO entry : entries) {
            LocalDate localDate = LocalDate.parse(entry.getTaskDt());
            LocalDateTime startDateTime = localDate.atStartOfDay();
            LocalDateTime endDateTime = localDate.plusDays(1).atStartOfDay();  // <=== next day midnight

            String styleClass = "applied-flag-no";
            if ("Y".equals(entry.getAppliedFlag())) {
                styleClass = "applied-flag-yes";
            }

            ScheduleEvent<?> evt = DefaultScheduleEvent.builder()
                    .title(entry.getTaskName())
                    .startDate(startDateTime)
                    .endDate(endDateTime)
                    .description(entry.getTaskType())
                    .id(entry.getTaskId())
                    .data(entry.getAppliedFlag())
                    .allDay(true)
                    .styleClass(styleClass) // <-- use your custom class!
                    .textColor("#000000") // <--- Add this line, text colour black in the schedule
                    .build();
            taskModel.addEvent(evt);
        }
    }

    public void onDateSelect(SelectEvent<LocalDateTime> event) {
        LocalDateTime localDateTime = event.getObject();
        selectedDateTime = localDateTime; // add a LocalDateTime field to your bean

        // Use DateTimeFormatter to strictly format date part
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        selectedDate = selectedDateTime.toLocalDate().format(formatter); // add a Date field to your bean if necessary                
        
        
    }
    public String onDateConfirm() {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//        String selectedDateString = sdf.format(selectedDate);
        String redirectUrl = "/secured/taskplan/taskadd?faces-redirect=true&selectedDate=" + selectedDate;
        return redirectUrl;
        
//        System.out.println("Confirmed: " + selectedDate);
    }
//    public void onViewChange() throws NamingException {
//       
//            fillTasksForMonth();
//    }

    public String getFormattedStartDate(ScheduleEvent<?> event) {
        LocalDateTime ldt = event.getStartDate();
        if (ldt == null) {
            return "";
        }
        // Use DateTimeFormatter to strictly format date part
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        return ldt.toLocalDate().format(formatter);
    }

//    public Date getEndDateAsDate(ScheduleEvent<?> event) {
//        LocalDateTime ldt = event.getEndDate();
//        if (ldt == null) {
//            return null;
//        }
//        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
//    }

    public void onTaskSelect(SelectEvent<ScheduleEvent<?>> selectTask) {
        ScheduleEvent<?> selectedEvent = selectTask.getObject();
// Extract LocalDate from selected event
        LocalDate selectedTaskDate = selectedEvent.getStartDate().toLocalDate();

        // Find all events that occur on this date
        tasksForSelectedDate = taskModel.getEvents().stream()
                .filter(evt -> evt.getStartDate().toLocalDate().isEqual(selectedTaskDate))
                .collect(Collectors.toList());
    }

    public String submitTask(ScheduleEvent<?> event) {
        String taskid = event.getId();
        System.out.printf("Taskid: %s", taskid);
        String redirectUrl = "/secured/taskplan/taskapply?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
//        for (ScheduleEvent<?> event : eventsForSelectedDate) {
//            String taskid = event.getId();
//            // process as needed: e.g., save to DB, or just print
//            System.out.printf("Taskid: %s", taskid);
//        }
    }
    
    public String editTask(ScheduleEvent<?> event) {
        String taskid = event.getId();
        System.out.printf("Taskid: %s", taskid);
        String redirectUrl = "/secured/taskplan/taskedit?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
//        for (ScheduleEvent<?> event : eventsForSelectedDate) {
//            String taskid = event.getId();
//            // process as needed: e.g., save to DB, or just print
//            System.out.printf("Taskid: %s", taskid);
//        }
    }

    public String viewTask(ScheduleEvent<?> event) {
        String taskid = event.getId();
        System.out.printf("Taskid: %s", taskid);
        String redirectUrl = "/secured/taskplan/taskview?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
    }
    
    public String deleteTask(ScheduleEvent<?> event) {
        String taskid = event.getId();
        System.out.printf("Taskid: %s", taskid);
        String redirectUrl = "/secured/taskplan/taskdelete?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
    }

    // Utility to get first/last date of a month
//    private Date[] getMonthRange(Date refDate) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(refDate);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        Date start = cal.getTime();
//
//        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//        cal.set(Calendar.MILLISECOND, 999);
//        Date end = cal.getTime();
//        return new Date[]{start, end};
//    }

    public ScheduleModel getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(ScheduleModel taskModel) {
        this.taskModel = taskModel;
    }

    public List<ScheduleEvent<?>> getTasksForSelectedDate() {
        return tasksForSelectedDate;
    }

    public void setTasksForSelectedDate(List<ScheduleEvent<?>> tasksForSelectedDate) {
        this.tasksForSelectedDate = tasksForSelectedDate;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    

    public LocalDateTime getSelectedDateTime() {
        return selectedDateTime;
    }

    public void setSelectedDateTime(LocalDateTime selectedDateTime) {
        this.selectedDateTime = selectedDateTime;
    }
    
    
}
