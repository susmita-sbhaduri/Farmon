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
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.HarvestSalesDTO;
import org.farmon.farmondto.SalesDTO;

/**
 *
 * @author sb
 */
@Named(value = "harvestWiseCropSales")
@ViewScoped
public class HarvestWiseCropSales implements Serializable {
    List<HarvestSalesDTO> harvestsales;
    private String startDt;
    private String endDt;
    /**
     * Creates a new instance of HarvestWiseCropSales
     */
    public HarvestWiseCropSales() {
    }
    public void fillValues() {
        harvestsales = new ArrayList<>();
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        
        
        farmondto = clientService.callDistinctHarInvService(farmondto);
        List<HarvestDTO> activeharvestlst = farmondto.getHarvestlist();
        farmondto = clientService.callActiveCropLstService(farmondto);
        List<CropDTO> crops = farmondto.getCroplist();
        HarvestSalesDTO rec = new HarvestSalesDTO();
        for (int i = 0; i < activeharvestlst.size(); i++) {            
            CropDTO cropforstock = new CropDTO();
            List<CropProductDTO> cropprodlist;
            for (int ii = 0; ii < crops.size(); ii++) {
                cropforstock.setCropId(crops.get(ii).getCropId());
                farmondto.setCroprec(crops.get(ii));
                farmondto = clientService.callNonzeroProdForCropService(farmondto);
                cropprodlist = farmondto.getCropprodlist();
                for (int i3 = 0; i3 < cropprodlist.size(); i3++) {
                    farmondto.setReportstartdt(startDt);
                    farmondto.setReportenddt(endDt);
                    
                    SalesDTO salesrec = new SalesDTO();
                    salesrec.setCropId(crops.get(ii).getCropId());
                    salesrec.setProdId(cropprodlist.get(i3).getProductId());
                    salesrec.setHarvestId(activeharvestlst.get(i).getHarvestid());                    
                    farmondto.setSalesrec(salesrec);
                    farmondto = clientService.callSalesSumHarCropProdService(farmondto);
                    SalesDTO salessum = farmondto.getSalesrec();
                    if(!salessum.getQuantitySold().equals("0.00") &&
                            !salessum.getPriceperUnit().equals("0.00")){
                        rec.setHarvestid(activeharvestlst.get(i).getHarvestid());
                        rec.setHarvestName(activeharvestlst.get(i).getHarvestName());
                        rec.setSiteid(activeharvestlst.get(i).getSiteid());
                        rec.setSiteName(activeharvestlst.get(i).getSiteName());
                        rec.setCropId(crops.get(ii).getCropId());
                        rec.setCropName(crops.get(ii).getCropName());
                        rec.setProdId(cropprodlist.get(i3).getProductId());
                        rec.setProductName(cropprodlist.get(i3).getProductName());
                        rec.setUnit(cropprodlist.get(i3).getUnit());
                        
                        rec.setSalesQty(salessum.getQuantitySold());
                        rec.setSalesAmtRs(salessum.getPriceperUnit());
                        
                        
                        harvestsales.add(rec);
                        rec = new HarvestSalesDTO();
                    }
                }
            }
        }
        
//        if (harveststocks != null) {
//            // Sort by Harvest Name, THEN Crop Name, THEN Product Name
//            harveststocks.sort(Comparator.comparing(HarvestStockDTO::getSiteName)
//                    .thenComparing(HarvestStockDTO::getHarvestName)
//                    .thenComparing(HarvestStockDTO::getCropName)
//                    .thenComparing(HarvestStockDTO::getProductName));
//        }
    }

    public List<HarvestSalesDTO> getHarvestsales() {
        return harvestsales;
    }

    public void setHarvestsales(List<HarvestSalesDTO> harvestsales) {
        this.harvestsales = harvestsales;
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
