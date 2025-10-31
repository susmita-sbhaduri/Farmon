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
import java.util.Comparator;
import java.util.List;
import org.farmon.farmondto.ExpenseDTO;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "paymentDetails")
@ViewScoped
public class PaymentDetails implements Serializable {
    List<ExpenseDTO> empexps;
    private String startDt;
    private String endDt;
    
    public PaymentDetails() {
    }
    public String fillValues() {
        String redirectUrl = "/secured/reports/harvestrpts?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto.setReportstartdt(startDt);
        farmondto.setReportenddt(endDt);
        farmondto = clientService.callEmpExpPerDtService(farmondto);
        empexps = farmondto.getExpenselist();
        if (empexps.isEmpty() || empexps == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "No applied resource found.");
            f.addMessage(null, message);
            return redirectUrl;
        } else {
//      sorted First descending order of date then for the same date alphabetical order 
//      of empname stored in ExpenseRefId.
            empexps.sort(Comparator.comparing(ExpenseDTO::getDate).reversed()
            .thenComparing(ExpenseDTO::getExpenseRefId, String.CASE_INSENSITIVE_ORDER)
                        );
            return null;
        }
            
        
    }

    public List<ExpenseDTO> getEmpexps() {
        return empexps;
    }

    public void setEmpexps(List<ExpenseDTO> empexps) {
        this.empexps = empexps;
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
