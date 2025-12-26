/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.EmpLeaveDTO;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "empLeaveReport")
@ViewScoped
public class EmpLeaveReport implements Serializable {
    List<EmpLeaveDTO> leaverecords = new ArrayList<>();
    /**
     * Creates a new instance of EmpLeaveReport
     */
    public EmpLeaveReport() {
    }
    public void fillValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callGetEmpLeavesService(farmondto);
        List<EmpLeaveDTO> leaves = farmondto.getEmpleavelist();
        EmpLeaveDTO record = new EmpLeaveDTO();
        int flag = 0;
        if (!leaves.isEmpty()) {
            for (int i = 0; i < leaves.size() - 1; i++) {
                if (flag == 0) {
                    record.setEmpname(leaves.get(i).getEmpname());
                    record.setLeavedate(leaves.get(i).getLeavedate());
                    record.setComments(leaves.get(i).getComments());
                } else {
                    record.setEmpname("--");
                    record.setLeavedate(leaves.get(i).getLeavedate());
                    record.setComments(leaves.get(i).getComments());
                }
                if (leaves.get(i).getEmpid().equals(leaves.get(i + 1).getEmpid())) {
                    flag = 1;
                } else {
                    flag = 0;
                }
                leaverecords.add(record);
                record = new EmpLeaveDTO();
            }
            if (flag == 0) {
                record.setEmpname(leaves.get(leaves.size() - 1).getEmpname());
                record.setLeavedate(leaves.get(leaves.size() - 1).getLeavedate());
                record.setComments(leaves.get(leaves.size() - 1).getComments());
            } else {
                record.setEmpname("--");
                record.setLeavedate(leaves.get(leaves.size() - 1).getLeavedate());
                record.setComments(leaves.get(leaves.size() - 1).getComments());
            }
            leaverecords.add(record);
        }
        
        
    }

    public List<EmpLeaveDTO> getLeaverecords() {
        return leaverecords;
    }

    public void setLeaverecords(List<EmpLeaveDTO> leaverecords) {
        this.leaverecords = leaverecords;
    }
    
    
}
