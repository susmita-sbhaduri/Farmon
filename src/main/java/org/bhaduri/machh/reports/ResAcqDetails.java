/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.ResAcqReportDTO;
import org.bhaduri.machh.services.MasterDataServices;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "resAcqDetails")
@ViewScoped
public class ResAcqDetails implements Serializable {
    List<ResAcqReportDTO> resacqs;
    
    /**
     * Creates a new instance of ResAcqDetails
     */
    public ResAcqDetails() {
    }
    public void fillValues() throws NamingException, ParseException {
//        Date startDate;
//        Date endDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
//        startDate = formatter.parse(startDt);
//        endDate = formatter.parse(endDt);
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callRecAcqRptService(farmondto);
        
        resacqs = farmondto.getResacqreport();
        
//        System.out.println("No resourcecrop record is found for this harvest.");
    }

    public List<ResAcqReportDTO> getResacqs() {
        return resacqs;
    }

    public void setResacqs(List<ResAcqReportDTO> resacqs) {
        this.resacqs = resacqs;
    }
    
}
