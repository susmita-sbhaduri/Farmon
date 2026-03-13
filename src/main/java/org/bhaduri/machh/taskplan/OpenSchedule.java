/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.taskplan;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import java.util.List;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import javax.naming.NamingException;
import org.farmon.farmondto.TaskPlanDTO;
import org.primefaces.model.ScheduleModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyScheduleModel;

/**
 *
 * @author sb
 */
@Named(value = "openSchedule")
@ViewScoped
public class OpenSchedule implements Serializable {

    private ScheduleModel taskModel;
    private List<ScheduleEvent<?>> tasksForSelectedDate;
    private LocalDateTime selectedDateTime;
    private String selectedDate;
    

    /**
     * Creates a new instance of OpenSchedule
     *
     */
    public OpenSchedule(){
//
//        fillTasksForMonth();
    }
    
    @PostConstruct
    public void init(){        
        taskModel = new LazyScheduleModel() {
            @Override
            public void loadEvents(LocalDateTime start, LocalDateTime end) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String startDt = start.format(formatter);
                    String endDt = end.format(formatter);
                    
                    // Call your working database method
                    fillTasksForMonth(startDt, endDt);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not load tasks."));
                }
            }
        };
    }
    

    private void fillTasksForMonth(String startDate, String endDate) 
            throws NamingException {        
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
//        taskModel.clear();
        
        farmondto.setReportstartdt(startDate);
        farmondto.setReportenddt(endDate);
        
        farmondto = clientService.callTaskLstBtnDatesService(farmondto);
//        farmondto = clientService.callTaskplanListService(farmondto);
        List<TaskPlanDTO> entries = farmondto.getTaskplanlist();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        for (TaskPlanDTO entry : entries) {
//            LocalDate localDate = LocalDate.parse(entry.getTaskDt());
            LocalDate localDate = LocalDate.parse(entry.getTaskDt(), formatter);
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

    }

    public String viewTask(ScheduleEvent<?> event) {
        String taskid = event.getId();
        System.out.printf("Taskid: %s", taskid);
        String redirectUrl = "/secured/taskplan/taskview?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
    }
    
    public String copyTask(ScheduleEvent<?> event) {        
        String redirectUrl = "/secured/taskplan/taskcopy?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
    }
    
    public String deleteTask(ScheduleEvent<?> event) {
        String taskid = event.getId();
        System.out.printf("Taskid: %s", taskid);
        String redirectUrl = "/secured/taskplan/taskdelete?faces-redirect=true&selectedTask=" + event.getId();
        return redirectUrl;
    }

   

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
