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
import org.farmon.farmondto.EmpExpDTO;
import org.farmon.farmondto.EmployeeDTO;
import org.farmon.farmondto.FarmonDTO;

/**
 *
 * @author sb
 */
@Named(value = "empPaidLoans")
@ViewScoped
public class EmpPaidLoans implements Serializable {
    List<EmpExpDTO> loanrecords = new ArrayList<>();

    /**
     * Creates a new instance of EmpPaidLoans
     */
    public EmpPaidLoans() {
    }
    public void fillValues() {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callEmpPaidLoanService(farmondto);
        List<EmpExpDTO> loanList = farmondto.getEmpexplist();
        EmpExpDTO record = new EmpExpDTO();
        EmployeeDTO emprecord = new EmployeeDTO();
        String empName;
        for (int i = 0; i < loanList.size(); i++) {
           emprecord.setId(loanList.get(i).getEmpid());
           farmondto.setEmprec(emprecord);
           farmondto = clientService.callEmpNameforIdService(farmondto);
           empName = farmondto.getEmprec().getName();
           record.setEmpid(empName);
           record.setId("LOAN");
           record.setTotal(loanList.get(i).getTotal());
//           record.setOutstanding(loanList.get(i).getOutstanding());
           record.setSdate(loanList.get(i).getSdate());
           record.setEdate(loanList.get(i).getEdate());
           loanrecords.add(record);
           
           farmondto.setEmpexprec(loanList.get(i));
           farmondto = clientService.callEmpPaybkLoanService(farmondto);
           
           List<EmpExpDTO> paybackList = farmondto.getEmpexplist();
           for (int j = 0; j < paybackList.size(); j++) {
               record = new EmpExpDTO();
               record.setEmpid("");
               record.setId("REPAYMENT");
               record.setTotal(paybackList.get(j).getTotal());
//               record.setOutstanding("");
               record.setSdate(paybackList.get(j).getSdate());
               record.setEdate("--");
               loanrecords.add(record);               
           }
           record = new EmpExpDTO();
        }
        
    }

    public List<EmpExpDTO> getLoanrecords() {
        return loanrecords;
    }

    public void setLoanrecords(List<EmpExpDTO> loanrecords) {
        this.loanrecords = loanrecords;
    }
    
}
