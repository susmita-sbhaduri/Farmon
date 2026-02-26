/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.SensorDbDTO;

/**
 *
 * @author sb
 */
@Named(value = "sensorDataReport")
@ViewScoped
public class SensorDataReport implements Serializable {
    List<SensorDbDTO> sensordatalist;
    /**
     * Creates a new instance of SensorDataReport
     */
    public SensorDataReport() {
    }
    public void fillSensorValues() {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();        
        farmondto = clientService.callSensorDataService(farmondto);
        sensordatalist = farmondto.getSensordatalist(); 
    }

    public List<SensorDbDTO> getSensordatalist() {
        return sensordatalist;
    }

    public void setSensordatalist(List<SensorDbDTO> sensordatalist) {
        this.sensordatalist = sensordatalist;
    }
    
    
}
