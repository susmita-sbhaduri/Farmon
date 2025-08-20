/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.harvest;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_DUPLICATE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.DTO.ResourceCropDTO;
import org.bhaduri.machh.DTO.ShopResCropDTO;
import org.bhaduri.machh.DTO.ShopResDTO;
import org.bhaduri.machh.services.MasterDataServices;

/**
 *
 * @author sb
 */
@Named(value = "resourceApply")
@ViewScoped
public class ResourceApply implements Serializable {
    private String selectedRes;
    private String selectedHarvest;
    private String site;
    private String cropcat;
    private String cropname;
    private List<FarmresourceDTO> existingresources;
    private int selectedIndex;
    private String amount;
    private String unit;
    private float amtapplied;
    private Date applyDt = new Date();
    private String rescat;
    private String cropwt;
    private String cropwtunit;
    private float resCropAppliedCost=0;
    
    public ResourceApply() {
    }
    public void fillValues() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        HarvestDTO harvestRecord = masterDataService.getHarvestRecForId(selectedHarvest);
//        SiteDTO siteRecord = masterDataService.getSiteNameForId(harvestRecord.getSiteName());
//        CropDTO cropRecord = masterDataService.getCropPerPk(harvestRecord.getCropName());
        site = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
        existingresources = masterDataService.getNonzeroResList();
    }
    
    public void onResSelect() throws NamingException {        
        MasterDataServices masterDataService = new MasterDataServices();
        selectedRes = existingresources.get(selectedIndex).getResourceId();
        amount = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
                .getAvailableAmt();
        unit = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
                .getUnit();
        if(masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
                .getCropwtunit()!=null){
            rescat = "Crop";
            cropwt = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
                    .getCropweight();
            cropwtunit = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
                    .getCropwtunit();
            
        } else{ 
            rescat = "Other";
            cropwt = "";
            cropwtunit = "";
        }
    }
    
//    public String goToSubmitRes() {
//        String redirectUrl = "/secured/harvest/appliedresperharvest?faces-redirect=true&appliedHarvest=" + selectedHarvest;
//        return redirectUrl; 
//    }
    
    public String goToApplyRes() throws NamingException {
        String redirectUrl = "/secured/harvest/resourceapply?faces-redirect=true&selectedHarvest=" + selectedHarvest;
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        if (selectedRes == null || selectedRes.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "Select one resource.");
            f.addMessage("resid", message);
            return redirectUrl;
        }
        if (amtapplied == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                    "Apply non-zero amount of resource.");
            f.addMessage("amtapplied", message);
            return redirectUrl;
        } else {
            float remainingAmt = Float.parseFloat(amount) - amtapplied;
            if (remainingAmt == 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure.",
                        "Strored resource would be finished after this application.");
                f.addMessage("amtapplied", message);
//                return null;
            }
            if (remainingAmt < 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure.",
                        "Stored resource is less than applied resource.");
                f.addMessage("amtapplied", message);
                return redirectUrl;
//                return null;
            }
        }
        int sqlFlag = 0;
        MasterDataServices masterDataService = new MasterDataServices();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        
        //resourcecrop record construction
        ResourceCropDTO resourceCrop = new ResourceCropDTO();        
        int applicationid = masterDataService.getMaxIdForResCrop();
        if (applicationid == 0) {
            resourceCrop.setApplicationId("1");
        } else {
            resourceCrop.setApplicationId(String.valueOf(applicationid + 1));
        }
        resourceCrop.setResourceId(selectedRes);
        resourceCrop.setHarvestId(selectedHarvest);
        resourceCrop.setAppliedAmount(String.format("%.2f", amtapplied)); 
        //shopresource record construction and update
        String shopResupdate = calcShopResAmt(amtapplied, resourceCrop.getApplicationId());
        //shopresource record construction and update
        if(shopResupdate.equals("OK")){
            resourceCrop.setAppliedAmtCost((String.format("%.2f", resCropAppliedCost)));
        } else resourceCrop.setAppliedAmtCost("0.00");
        resourceCrop.setApplicationDt(sdf.format(applyDt));
        
        
        
        
        //farmresource record construction
        FarmresourceDTO resourceRec = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes));
        float remainingAmt = Float.parseFloat(amount) - amtapplied;
        resourceRec.setAvailableAmt(String.format("%.2f", remainingAmt));
        
        int rescropres = masterDataService.addResCropRecord(resourceCrop);
        
        if (rescropres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (rescropres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure."
                        , "Resource already applied with this application ID=" + resourceCrop.getApplicationId());
                f.addMessage(null, message);
            }
            if (rescropres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure."
                        , "Failure on applying resource");
                f.addMessage(null, message);
            } 
            redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
            return redirectUrl;
        }
        
        
        if (sqlFlag == 1) {
            int resres = masterDataService.editResource(resourceRec);
            if (resres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (resres == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure"
                            , "Resource record does not exist");
                    f.addMessage(null, message);
                }
                if (resres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure"
                            , "Failure on resource update");
                    f.addMessage(null, message);
                }
                int delres = masterDataService.delResCropRecord(resourceCrop);
                if (delres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure"
                            , "resourcecrop record could not be deleted");
                    f.addMessage(null, message);
                }
                redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
                return redirectUrl;
            }
        }
        if (sqlFlag == 2) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Resource applied successfully with application ID=" + resourceCrop.getApplicationId());
            f.addMessage(null, message);
        }
        return redirectUrl;
        
    }
    
    public String calcShopResAmt(float quantityApplied, String applId) throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
        List<ShopResDTO> shopResListResid = masterDataService.getShopResForResid(selectedRes);

        ShopResDTO shopResRec = new ShopResDTO();
        float appliedQuantity = quantityApplied;
        int breaki = 0;
        for (int i = 0; i < shopResListResid.size(); i++) {
            shopResRec.setId(shopResListResid.get(i).getId());
            shopResRec.setRate(shopResListResid.get(i).getRate());
            shopResRec.setResRateDate(shopResListResid.get(i).getResRateDate());
            shopResRec.setResourceId(shopResListResid.get(i).getResourceId());
            shopResRec.setShopId(shopResListResid.get(i).getShopId());
            shopResRec.setResAppId(applId);
            ///
            ShopResCropDTO shoprescrop = new ShopResCropDTO();
            int maxid = masterDataService.getMaxIdForShopResCrop();
            if (maxid == 0) {
                shoprescrop.setId("1");
            } else {
                shoprescrop.setId(String.valueOf(maxid + 1));
            }
            shoprescrop.setResCropId(applId);
            shoprescrop.setShopResId(shopResListResid.get(i).getId());
            ///
            float shopResStock = Float.parseFloat(shopResListResid.get(i).getStockPerRate());
            float shopResRate = Float.parseFloat(shopResListResid.get(i).getRate());
            if ((appliedQuantity - shopResStock) <= 0) {
                //in this case the applied resource is deducted from the stock(consumed
                //from the stock completely and stock is not more than 0, hence break from this loop.
                shopResStock = shopResStock - appliedQuantity;                
                resCropAppliedCost = resCropAppliedCost + (appliedQuantity * shopResRate);                
                shopResRec.setStockPerRate(String.format("%.2f", shopResStock));
                shopResRec.setAmtApplied(String.format("%.2f", appliedQuantity));
                
                ///                
                shoprescrop.setAmtApplied(String.format("%.2f", appliedQuantity));
                ///
                appliedQuantity = 0;
                
                int shopres = masterDataService.editShopForRes(shopResRec);
                if (shopres != SUCCESS) {
                    resCropAppliedCost = 0;
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "shopresource record could not be updated");
                    f.addMessage(null, message);
                    return redirectUrl;
                }
                
                ///
                int shoprescropres = masterDataService.addShopResCrop(shoprescrop);
                if (shoprescropres != SUCCESS) {
                    resCropAppliedCost = 0;
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "shoprescrop record could not be inserted");
                    f.addMessage(null, message);
                    return redirectUrl;
                }
                ///
                breaki = i;
                break;
            }
            if ((appliedQuantity - shopResStock) > 0) {
                resCropAppliedCost = resCropAppliedCost + (shopResStock * shopResRate);
                appliedQuantity = appliedQuantity - shopResStock;
                shopResRec.setAmtApplied(String.format("%.2f", shopResStock));
                ///                
                shoprescrop.setAmtApplied(String.format("%.2f", shopResStock));
                ///
                shopResStock = 0;
                shopResRec.setStockPerRate(String.format("%.2f", shopResStock));
            }
            int shopres = masterDataService.editShopForRes(shopResRec);
            if (shopres != SUCCESS) {
                resCropAppliedCost = 0;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "shopresource record could not be updated");
                f.addMessage(null, message);
                return redirectUrl;
            }
            ///
            int shoprescropres = masterDataService.addShopResCrop(shoprescrop);
            if (shoprescropres != SUCCESS) {
                resCropAppliedCost = 0;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "shoprescrop record could not be inserted");
                f.addMessage(null, message);
                return redirectUrl;
            }
                ///
        }
//        if (breaki < shopResListResid.size() - 1) {
//            for (int ii = breaki+1; ii < shopResListResid.size(); ii++) {
//                shopResRec.setId(shopResListResid.get(ii).getId());
//                shopResRec.setRate(shopResListResid.get(ii).getRate());
//                shopResRec.setResRateDate(shopResListResid.get(ii).getResRateDate());
//                shopResRec.setResourceId(shopResListResid.get(ii).getResourceId());
//                shopResRec.setStockPerRate(shopResListResid.get(ii).getStockPerRate());
//                shopResRec.setShopId(shopResListResid.get(ii).getShopId());
//                shopResRec.setResAppId("0");
//                shopResRec.setAmtApplied(String.format("%.2f", 0.0));
//                int shopres = masterDataService.editShopForRes(shopResRec);
//                if (shopres != SUCCESS) {
//                    resCropAppliedCost = 0;
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
//                            "shopresource record could not be updated");
//                    f.addMessage(null, message);
//                    return redirectUrl;
//                }
//            }
//        }
        return "OK";
    }
    public String getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    public String getSelectedHarvest() {
        return selectedHarvest;
    }

    public void setSelectedHarvest(String selectedHarvest) {
        this.selectedHarvest = selectedHarvest;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCropcat() {
        return cropcat;
    }

    public void setCropcat(String cropcat) {
        this.cropcat = cropcat;
    }

    public String getCropname() {
        return cropname;
    }

    public void setCropname(String cropname) {
        this.cropname = cropname;
    }

    public List<FarmresourceDTO> getExistingresources() {
        return existingresources;
    }

    public void setExistingresources(List<FarmresourceDTO> existingresources) {
        this.existingresources = existingresources;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getAmtapplied() {
        return amtapplied;
    }

    public void setAmtapplied(float amtapplied) {
        this.amtapplied = amtapplied;
    }

    public Date getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(Date applyDt) {
        this.applyDt = applyDt;
    }

    public float getResCropAppliedCost() {
        return resCropAppliedCost;
    }

    public void setResCropAppliedCost(float resCropAppliedCost) {
        this.resCropAppliedCost = resCropAppliedCost;
    }

    public String getRescat() {
        return rescat;
    }

    public void setRescat(String rescat) {
        this.rescat = rescat;
    }

    public String getCropwt() {
        return cropwt;
    }

    public void setCropwt(String cropwt) {
        this.cropwt = cropwt;
    }

    public String getCropwtunit() {
        return cropwtunit;
    }

    public void setCropwtunit(String cropwtunit) {
        this.cropwtunit = cropwtunit;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
        
}
