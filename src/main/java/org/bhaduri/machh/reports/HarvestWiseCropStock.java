/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.reports;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.HarvestStockDTO;
import org.farmon.farmondto.InventoryDTO;

/**
 *
 * @author sb
 */
@Named(value = "harvestWiseCropStock")
@ViewScoped
public class HarvestWiseCropStock implements Serializable {
    List<HarvestStockDTO> harveststocks;
    /**
     * Creates a new instance of HarvestWiseCropStock
     */
    public HarvestWiseCropStock() {
    }
    public void fillValues() {
        harveststocks = new ArrayList<>();
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        farmondto = clientService.callDistinctHarInvService(farmondto);
        List<HarvestDTO> activeharvestlst = farmondto.getHarvestlist();
        farmondto = clientService.callActiveCropLstService(farmondto);
        List<CropDTO> crops = farmondto.getCroplist();
        HarvestStockDTO rec = new HarvestStockDTO();
        for (int i = 0; i < activeharvestlst.size(); i++) {            
            CropDTO cropforstock = new CropDTO();
            List<CropProductDTO> cropprodlist;
            for (int ii = 0; ii < crops.size(); ii++) {
                cropforstock.setCropId(crops.get(ii).getCropId());
                farmondto.setCroprec(crops.get(ii));
                farmondto = clientService.callNonzeroProdForCropService(farmondto);
                cropprodlist = farmondto.getCropprodlist();
                for (int i3 = 0; i3 < cropprodlist.size(); i3++) {
                    InventoryDTO inventoryrec = new InventoryDTO();
                    inventoryrec.setCropId(crops.get(ii).getCropId());
                    inventoryrec.setProductId(cropprodlist.get(i3).getProductId());
                    inventoryrec.setHarvestId(activeharvestlst.get(i).getHarvestid());
                    farmondto.setInventoryrec(inventoryrec);
                    farmondto = clientService.callInvForHarCropProdService(farmondto);
                    List<InventoryDTO> recordList = farmondto.getInventorylist();
                    if(!recordList.isEmpty()){
                        rec.setHarvestid(activeharvestlst.get(i).getHarvestid());
                        rec.setHarvestName(activeharvestlst.get(i).getHarvestName());
                        rec.setCropId(crops.get(ii).getCropId());
                        rec.setCropName(crops.get(ii).getCropName());
                        rec.setProdId(cropprodlist.get(i3).getProductId());
                        rec.setProductName(cropprodlist.get(i3).getProductName());
                        rec.setSiteid(activeharvestlst.get(i).getSiteid());
                        rec.setSiteName(activeharvestlst.get(i).getSiteName());
                        
                        farmondto.setInventoryrec(inventoryrec);
                        farmondto = clientService.callSumForHarCropProdService(farmondto);
                        String qtyString = farmondto.getInventoryrec().getCurrentQty();
                        
                        rec.setStock(qtyString);
                        rec.setUnit(cropprodlist.get(i3).getUnit());
                        harveststocks.add(rec);
                        rec = new HarvestStockDTO();
                    }
                }
            }
        }
        
        if (harveststocks != null) {
            // Sort by Harvest Name, THEN Crop Name, THEN Product Name
            harveststocks.sort(Comparator.comparing(HarvestStockDTO::getSiteName)
                    .thenComparing(HarvestStockDTO::getHarvestName)
                    .thenComparing(HarvestStockDTO::getCropName)
                    .thenComparing(HarvestStockDTO::getProductName));
        }
    }

    public List<HarvestStockDTO> getHarveststocks() {
        return harveststocks;
    }

    public void setHarveststocks(List<HarvestStockDTO> harveststocks) {
        this.harveststocks = harveststocks;
    }
    
}
