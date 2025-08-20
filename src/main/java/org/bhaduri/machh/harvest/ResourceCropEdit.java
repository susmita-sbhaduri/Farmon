/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 *## this version includues the logic to switch the resource doing active harvests -->review resources
 */
package org.bhaduri.machh.harvest;


import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.bhaduri.machh.DTO.FarmresourceDTO;
import org.farmon.farmondto.HarvestDTO;
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
@Named(value = "resourceCropEdit")
@ViewScoped
public class ResourceCropEdit implements Serializable {

    private String selectedRes;
    private String resName;
    private String selectedRescrop;
    private String site;
    private String cropcat;
    private String cropname;
    private String amount;
    private String unit;
    private float amtapplied;
    private Date applyDt;
    private String rescat;
    private String cropwt;
    private String cropwtunit;
    private FarmresourceDTO farmresPrev;
    private ResourceCropDTO rescropPrev;
    private float resCropAppliedCost=0;

    public ResourceCropEdit() {
    }
    
    public void fillValues() throws NamingException, ParseException {
        MasterDataServices masterDataService = new MasterDataServices();
        rescropPrev = masterDataService.getResCropForId(selectedRescrop);
//        resCropAppliedCost = Float.parseFloat(rescropPrev.getAppliedAmtCost());
        HarvestDTO harvestRecord = masterDataService
                .getHarvestRecForId(rescropPrev.getHarvestId());
        site = harvestRecord.getSiteName();
        cropcat = harvestRecord.getCropCategory();
        cropname = harvestRecord.getCropName();
        
        resName = rescropPrev.getResourceName();        
        selectedRes = rescropPrev.getResourceId();
        
        farmresPrev = masterDataService.
                getResourceNameForId(Integer.parseInt(rescropPrev.getResourceId()));
        amount = farmresPrev.getAvailableAmt();
        unit = farmresPrev.getUnit();
        if(masterDataService.getResourceNameForId(Integer.parseInt(rescropPrev.getResourceId()))
                .getCropwtunit()!=null){
            rescat = "Crop";
            cropwt = masterDataService.getResourceNameForId(Integer.parseInt(rescropPrev.getResourceId()))
                    .getCropweight();
            cropwtunit = masterDataService.getResourceNameForId(Integer.parseInt(rescropPrev.getResourceId()))
                    .getCropwtunit();
            
        } else{ 
            rescat = "Other";
            cropwt = "";
            cropwtunit = "";
        }
        
       // for autofill        
        this.amtapplied = Float.parseFloat(rescropPrev.getAppliedAmount());
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        this.applyDt = formatter.parse(rescropPrev.getApplicationDt());
    }

//    public void onResSelect() throws NamingException {        
//        MasterDataServices masterDataService = new MasterDataServices();
//        amount = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
//                .getAvailableAmt();
//        unit = masterDataService.getResourceNameForId(Integer.parseInt(selectedRes))
//                .getUnit();
//    }
    
    public String goToSave() throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        
        String redirectUrl = "/secured/harvest/appliedresperharvest?faces-redirect=true&appliedHarvest=" 
                + rescropPrev.getHarvestId();
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        float remainingAmt =0;        
        String shopResResponse;        
        ResourceCropDTO rescropRecord = masterDataService.getResCropForId(selectedRescrop);
        
        if (amtapplied == Float.parseFloat(rescropPrev.getAppliedAmount())) {
            remainingAmt = Float.parseFloat(farmresPrev.getAvailableAmt());
            resCropAppliedCost = Float.parseFloat(rescropPrev.getAppliedAmtCost());
            rescropRecord.setAppliedAmtCost(String.format("%.2f", resCropAppliedCost));
        } else {
            remainingAmt = Float.parseFloat(farmresPrev.getAvailableAmt())
                    - amtapplied
                    + Float.parseFloat(rescropPrev.getAppliedAmount());
            shopResResponse = calcShopResAmtAdd(selectedRescrop, selectedRes);
            if(shopResResponse.equals("OKADD")){
                shopResResponse = calcShopResAmtDeduct(amtapplied, selectedRescrop, selectedRes);
                if(shopResResponse.equals("OKDEDUCT")){
                    System.out.println(" atlast done.");
                    rescropRecord.setAppliedAmtCost(String.format("%.2f", resCropAppliedCost));
                }
            }
        }    
        
        if (amtapplied == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Apply non-zero amount of resource.",
                    "Apply non-zero amount of resource.");
            f.addMessage("amtapplied", message);
            return redirectUrl;
        } else {
//            float remainingAmt = Float.parseFloat(amount) - amtapplied +amtappliedprev;
            if (remainingAmt == 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Strored resource would be finished after this application.",
                        "Stored resource would be finished after this application.");
                f.addMessage("amtapplied", message);
            }
            if (remainingAmt < 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Resource cannot be applied.",
                        "Stored resource is less than applied resource.");
                f.addMessage("amtapplied", message);
                return redirectUrl;
            }
        }
        
        int sqlFlag = 0;
//        ResourceCropDTO rescropRecord = masterDataService.getResCropForId(selectedRescrop);
        rescropRecord.setResourceId(selectedRes);// for resourcecrop table
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        rescropRecord.setAppliedAmount(String.format("%.2f", amtapplied));
        rescropRecord.setApplicationDt(sdf.format(applyDt));
        
        
        FarmresourceDTO resourceRec = masterDataService.
                getResourceNameForId(Integer.parseInt(selectedRes)); // for farmresouce table
        resourceRec.setAvailableAmt(String.format("%.2f", remainingAmt));
       
        int rescropres = masterDataService.editResCropRecord(rescropRecord);
        
        if (rescropres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (rescropres == DB_NON_EXISTING) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure" 
                                                , "Resourcecrop record doesn't exist");
                f.addMessage(null, message);
            }
            if (rescropres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        Integer.toString(DB_SEVERE));
                f.addMessage(null, message);
            } 
            return redirectUrl;
        }
        
        
        if (sqlFlag == 1) {
            int resres = masterDataService.editResource(resourceRec);
            if (resres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (resres == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Resource record does not exist", Integer.toString(DB_NON_EXISTING));
                    f.addMessage(null, message);
                }
                if (resres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure on resource update", Integer.toString(DB_SEVERE));
                    f.addMessage(null, message);
                }
                resres = masterDataService.editResCropRecord(rescropPrev);

                if (resres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure on resourcecrop table update",
                            Integer.toString(DB_SEVERE));
                    f.addMessage(null, message);
                }
                return redirectUrl;
            }
        }
        if (sqlFlag == 2) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Resource updated successfully with application ID=" + rescropRecord.getApplicationId());
            f.addMessage(null, message);
        }
        return redirectUrl;        
    }
    
    public String calcShopResAmtDeduct(float quantityApplied, String applId, String resId) throws NamingException {
        MasterDataServices masterDataService = new MasterDataServices();
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
        List<ShopResDTO> shopResListResid = masterDataService.getShopResForResid(resId);
        
        int breaki = 0;
        ShopResDTO shopResRec = new ShopResDTO();
        float appliedQuantity = quantityApplied;
        for (int i = 0; i < shopResListResid.size(); i++) {
            shopResRec.setId(shopResListResid.get(i).getId());
            shopResRec.setRate(shopResListResid.get(i).getRate());
            shopResRec.setResRateDate(shopResListResid.get(i).getResRateDate());
            shopResRec.setResourceId(shopResListResid.get(i).getResourceId());
            shopResRec.setShopId(shopResListResid.get(i).getShopId());
            ///
            int shoprescropflag = 0;
            ShopResCropDTO shoprescropadd = new ShopResCropDTO();
//            ShopResCropDTO shoprescroped = masterDataService
//                    .getRecForShopResCrop(applId, shopResListResid.get(i).getId());
            List<ShopResCropDTO> shopResCropList = masterDataService
                    .getShopResCropEmpty(applId);// all the records with zero amt applied
            ShopResCropDTO shoprescroped;
            if(shopResCropList.size()==0){
                shoprescroped = null;
            } else {
                shoprescroped = shopResCropList.get(0);//we need to take the first record as in every iteration 
// of the loop next record of the shoprescrop record would be updated. This way we are interating next of shoprescrop.
                shoprescroped.setShopResId(shopResListResid.get(i).getId());
            }
            if (shoprescroped == null) {
                shoprescropflag = 1;
                int maxid = masterDataService.getMaxIdForShopResCrop();
                if (maxid == 0) {
                    shoprescropadd.setId("1");
                } else {
                    shoprescropadd.setId(String.valueOf(maxid + 1));
                }
                shoprescropadd.setResCropId(applId);
                shoprescropadd.setShopResId(shopResListResid.get(i).getId());
            } else
                shoprescropflag = 2;
            
            ///
            float shopResStock = Float.parseFloat(shopResListResid.get(i).getStockPerRate());
            float shopResRate = Float.parseFloat(shopResListResid.get(i).getRate());
            
            
            if (appliedQuantity <= shopResStock) {
                //in this case the applied resource is deducted from the stock(consumed
                //from the stock completely and stock is not more than 0, hence break from this loop.
                shopResStock = shopResStock - appliedQuantity;
                resCropAppliedCost = resCropAppliedCost + (appliedQuantity * shopResRate);               
                shopResRec.setStockPerRate(String.format("%.2f", shopResStock));
                ///
                if(shoprescropflag ==2){
                    shoprescroped.setAmtApplied(String.format("%.2f", appliedQuantity));
                }
                if(shoprescropflag ==1){
                    shoprescropadd.setAmtApplied(String.format("%.2f", appliedQuantity));
                }
                ///
                appliedQuantity = 0;

                int shopres = masterDataService.editShopForRes(shopResRec);
                if (shopres != SUCCESS) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "shopresource record could not be updated");
                    f.addMessage(null, message);
                    return redirectUrl;
                }
                ///
                if (shoprescropflag == 2) {
                    int shoprescropresp = masterDataService.editShopResCrop(shoprescroped);
                    if (shoprescropresp != SUCCESS) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "shoprescrop record could not be updated");
                        f.addMessage(null, message);
                        return redirectUrl;
                    }
                }
                if (shoprescropflag == 1) {
                    int shoprescropresp = masterDataService.addShopResCrop(shoprescropadd);
                    if (shoprescropresp != SUCCESS) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                                "shoprescrop record could not be updated");
                        f.addMessage(null, message);
                        return redirectUrl;
                    }
                }
                ///
                breaki = i;
                break;
            }
            if (appliedQuantity > shopResStock) {
                resCropAppliedCost = resCropAppliedCost + (shopResStock * shopResRate);
                appliedQuantity = appliedQuantity - shopResStock;
                ///
                if(shoprescropflag ==2){
                    shoprescroped.setAmtApplied(String.format("%.2f", shopResStock));
                }
                if(shoprescropflag ==1){
                    shoprescropadd.setAmtApplied(String.format("%.2f", shopResStock));
                }
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
            if (shoprescropflag == 2) {
                int shoprescropresp = masterDataService.editShopResCrop(shoprescroped);
                if (shoprescropresp != SUCCESS) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "shoprescrop record could not be updated");
                    f.addMessage(null, message);
                    return redirectUrl;
                }
            }
            if (shoprescropflag == 1) {
                int shoprescropresp = masterDataService.addShopResCrop(shoprescropadd);
                if (shoprescropresp != SUCCESS) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                            "shoprescrop record could not be updated");
                    f.addMessage(null, message);
                    return redirectUrl;
                }
            }
            ///
        }
        List<ShopResCropDTO> shopResCropList = masterDataService.getShopResCropEmpty(applId);
        for (int j = 0; j < shopResCropList.size(); j++) {
            ShopResCropDTO shoprescropdel = new ShopResCropDTO();
            shoprescropdel.setId(shopResCropList.get(j).getId());
            int shoprescropresp = masterDataService.deleteShopResCrop(shoprescropdel);
            if (shoprescropresp != SUCCESS) {
                resCropAppliedCost = 0;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "shoprescrop record could not be deleted");
                f.addMessage(null, message);
                return redirectUrl;
            }
        }     
        
        return "OKDEDUCT";
    }
    
        
    public String calcShopResAmtAdd(String applyid, String resId) throws NamingException{
        MasterDataServices masterDataService = new MasterDataServices();
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        String redirectUrl = "/secured/harvest/activehrvstlst?faces-redirect=true";
//        float appliedQuantity = quantityApplied;
        List<ShopResCropDTO> listperApplyId = masterDataService.getShopResCropList(applyid);
        
        for (int i = 0; i < listperApplyId.size(); i++) {
            ShopResCropDTO recToUpd = new ShopResCropDTO();
            recToUpd.setId(listperApplyId.get(i).getId());
            recToUpd.setResCropId(applyid);
            recToUpd.setShopResId(listperApplyId.get(i).getShopResId());
            recToUpd.setAmtApplied(String.format("%.2f",0.00));
            
            ShopResDTO shopResRec = masterDataService
                    .getShopResForId(listperApplyId.get(i).getShopResId());
            
            float stock = Float.parseFloat(shopResRec.getStockPerRate())+
                    Float.parseFloat(listperApplyId.get(i).getAmtApplied());
            shopResRec.setStockPerRate(String.format("%.2f",stock));
            
            
            int shopcropres = masterDataService.editShopResCrop(recToUpd);
            if (shopcropres != SUCCESS) {
//                resCropAppliedCost = 0;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "shoprescrop record could not be updated");
                f.addMessage(null, message);
                return redirectUrl;
            }  
            int shopres = masterDataService.editShopForRes(shopResRec);
            if (shopres != SUCCESS) {
//                resCropAppliedCost = 0;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure",
                        "shopres record could not be updated");
                f.addMessage(null, message);
                return redirectUrl;
            } 
        }
        return "OKADD";
    }

    public float getResCropAppliedCost() {
        return resCropAppliedCost;
    }

    public void setResCropAppliedCost(float resCropAppliedCost) {
        this.resCropAppliedCost = resCropAppliedCost;
    }
    
    public String goToAppliedRes() {
        String redirectUrl = "/secured/harvest/appliedresperharvest?faces-redirect=true&appliedHarvest=" + rescropPrev.getHarvestId();
        return redirectUrl; 
    }

    public String getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    public String getSelectedRescrop() {
        return selectedRescrop;
    }

    public void setSelectedRescrop(String selectedRescrop) {
        this.selectedRescrop = selectedRescrop;
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

    public FarmresourceDTO getFarmresPrev() {
        return farmresPrev;
    }

    public void setFarmresPrev(FarmresourceDTO farmresPrev) {
        this.farmresPrev = farmresPrev;
    }

    public ResourceCropDTO getRescropPrev() {
        return rescropPrev;
    }

    public void setRescropPrev(ResourceCropDTO rescropPrev) {
        this.rescropPrev = rescropPrev;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
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
    
    
}
