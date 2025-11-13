/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author sb
 */
@Named(value = "monthsForExpRpt")
@ViewScoped
public class MonthsForExpRpt implements Serializable {

    private String selectedMonth;
    private List<String> months;
    private Integer selectedYear;
    private List<Integer> years;
    
    public MonthsForExpRpt() {
        months = new ArrayList<>();
        for (Month month : Month.values()) {
            // Add month in "MMM" format, e.g. Jan, Feb
            months.add(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        years = new ArrayList<>();
        for (int y = 2019; y <= 2050; y++) {
            years.add(y);
        }
    }

    public String getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(String selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public Integer getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(Integer selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }
    public String goToGenerateRpt() {        
        
        String redirectUrl = "/secured/reports/monthlyexpense?faces-redirect=true&month=" 
                + selectedMonth + "&year=" + selectedYear;
        return redirectUrl; 
    }
}
