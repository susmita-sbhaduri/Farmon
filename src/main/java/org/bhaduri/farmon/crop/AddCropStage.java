/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.crop;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.CropDTO;
import org.farmon.farmondto.CropProductDTO;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.HarvestDTO;
import org.farmon.farmondto.ProductStageDTO;

/**
 *
 * @author sb
 */
@Named(value = "addCropStage")
@ViewScoped
public class AddCropStage implements Serializable {
    private String cropId;
    private String cropname;
    private String cropProdId;
    private String cropprodname;    
    private List<String> stageEntries;
    public AddCropStage() {
    }
    public String fillValues() throws NamingException {
        FarmonDTO farmondto = new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        CropDTO croprec = new CropDTO();
        croprec.setCropId(cropId);
        farmondto.setCroprec(croprec);
        farmondto = clientService.callCropRecService(farmondto);
        cropname = farmondto.getCroprec().getCropName();
        
        CropProductDTO cropprodrec = new CropProductDTO();
        cropprodrec.setCropId(cropId);
        cropprodrec.setProductId(cropProdId);
        farmondto.setCropprodrec(cropprodrec);
        farmondto = clientService.callCropprodForCropProdService(farmondto);
        cropprodname = farmondto.getCropprodrec().getProductName();
        
        stageEntries = new ArrayList<>();
        ProductStageDTO prodstagerec = new ProductStageDTO();
        prodstagerec.setCropId(cropId);
        prodstagerec.setProductId(cropProdId);
        farmondto.setProdstagerec(prodstagerec);
        farmondto = clientService.callStagesPerCropProdService(farmondto);
        List<ProductStageDTO> prodstagelist = farmondto.getProdstagelist();
        for (ProductStageDTO prodstage : prodstagelist) {            
            stageEntries.add(prodstage.getProdStageName());
        }
        // Add the initial empty pair of textboxes
        entries.add(new CropProductDTO());
       
        String redirectUrl = "/secured/userhome?faces-redirect=true";
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
            
        if(activeHarvests==null){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                    "No active harvests.");
            f.addMessage(null, message);
            return redirectUrl;
        } else 
            return null;        
    }
}
