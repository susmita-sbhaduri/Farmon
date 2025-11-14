/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.machh.resource;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.farmon.farmondto.ExpenseDTO;
import org.farmon.farmondto.FarmresourceDTO;
import static org.farmon.farmondto.FarmonResponseCodes.DB_DUPLICATE;
import static org.farmon.farmondto.FarmonResponseCodes.DB_NON_EXISTING;
import static org.farmon.farmondto.FarmonResponseCodes.DB_SEVERE;
import static org.farmon.farmondto.FarmonResponseCodes.SUCCESS;
import org.farmon.farmondto.ResAcquireDTO;
import org.farmon.farmondto.ShopResDTO;
import java.util.*;
import java.util.stream.*;
import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
/**
 *
 * @author sb
 */
@Named(value = "acquireResource")
@ViewScoped
public class AcquireResource implements Serializable {

    private boolean saveDisabled = true;
    private String selectedRes;
    private String selectedResName;
    private String selectedShop;
    private float rate;
    private String unit;
    private List<ShopResDTO> shopForSelectedRes;
    private List<FarmresourceDTO> existingresources;
    private ShopResDTO selectedShopRes;
    private float amount;
    private Date purchaseDt = new Date();
    private String rescat;
    private float cropwt;
    private String cropwtunit;
    private String comments;
    private List<ShopResDTO> selectedShopResLst;
    private boolean cropwtReadonly = true; // default as readonly

    public AcquireResource() {
    }

    public void fillResourceValues() throws NamingException {
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        ShopResDTO shopresrec = new ShopResDTO();
        shopresrec.setResourceId(selectedRes);
        farmondto.setShopresrec(shopresrec);
        farmondto = clientService.callShopResForResidService(farmondto);        
        List<ShopResDTO> shopForSelectedResAll = farmondto.getShopreslist();
        shopForSelectedRes = shopForSelectedResAll.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                row -> Arrays.asList(row.getResourceId(), row.getShopId()), // key: two columns
                                row -> row,
                                (row1, row2) -> row1 // keep the first occurrence
                        ),
                        map -> new ArrayList<>(map.values())
                ));
        FarmresourceDTO farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        
        FarmresourceDTO selectedResDto = farmondto.getFarmresourcerec();
        if(selectedResDto.getCropwtunit()!=null){
            rescat = "Crop";
            cropwtunit = selectedResDto.getCropwtunit();
            cropwtReadonly = false;
        } else rescat = "Other";
        
        selectedResName = selectedResDto.getResourceName();
    }

    public void onShopResSelect() throws NamingException {
        System.out.println("No crop categories are found." + selectedShop);
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
        ShopResDTO shopresrec = new ShopResDTO();
        shopresrec.setResourceId(selectedRes);
        shopresrec.setShopId(selectedShop);
        farmondto.setShopresrec(shopresrec);
        farmondto = clientService.callShopResListService(farmondto);
//        MasterDataServices masterDataService = new MasterDataServices();
//        selectedShopResLst would contain the existing rates for the selectedRes, selectedShop combination.
//        they are shown in the datatable of the xhtml page.
        selectedShopResLst = farmondto.getShopreslist();
        ///
        ///It means if the shopresource record is newly added 
//        then there would be only one record with resid and shopid combination. Hence first record is fetched 
//        just to check if it's a new record or one of the existing multiple shopresource record. If it's a new 
//        record, then selectedShopResLst will contain only one record, as resource acquiring has not started
//        else it will fetch multiple records with same shop and resource id. As we need to take out just the 
//        shopid and resid hence fetching first record will suffice.
///
        selectedShopRes = selectedShopResLst.get(0);
        rate = 0;
        FarmresourceDTO farmresrec = new FarmresourceDTO();
        farmresrec.setResourceId(selectedRes);
        farmondto.setFarmresourcerec(farmresrec);
        farmondto = clientService.callResnameForIdService(farmondto);
        unit = farmondto.getFarmresourcerec().getUnit();
    }

    public String goToReviewRes() {
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        if (selectedShop == null || selectedShop.trim().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Select one shop.");
            f.addMessage("shopid", message);
            return null;
        }
        
        if(rate==0){
           message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide a non-zero rate.");
            f.addMessage("rate", message); 
            return null;
        }
        
        if (amount == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                    "Provide non-zero purchase amount.");
            f.addMessage("amount", message);
            return null;
        }
        
        if(rescat.equals("Crop")){
            if (cropwt == 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure",
                        "Cropweight has to be given.");
                f.addMessage("cropwt", message);
                return null;
            }            
        }
        
        float calculatedAmount = rate*amount;
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Total cost for "+selectedShopRes.getResourceName()
                +" for shop "+selectedShopRes.getShopName(),
                    "=Rs."+String.format("%.2f", calculatedAmount));
        
        f.addMessage(null, message);
        saveDisabled = false; // Enable the save button
        return null;
    }

    public String goToSaveRes() throws NamingException {
        int sqlFlag = 0;
//        String redirectUrl = "/secured/resource/maintainresource?faces-redirect=true";
        String redirectUrl = "/secured/resource/acquireresource?faces-redirect=true&selectedRes="
                        + selectedShopRes.getResourceId();
        FacesMessage message;
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        
        FarmonDTO farmondto= new FarmonDTO();
        FarmonClient clientService = new FarmonClient();
//        MasterDataServices masterDataService = new MasterDataServices();
        
//        construction of shopresource record
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ShopResDTO shopResRec = new ShopResDTO();
        int shopresflag = 0;
        shopResRec.setShopId(selectedShopRes.getShopId());
        shopResRec.setShopName(selectedShopRes.getShopName());
        shopResRec.setResourceId(selectedShopRes.getResourceId());
        shopResRec.setResourceName(selectedShopRes.getResourceName());
        shopResRec.setRate(String.format("%.2f", rate));
        shopResRec.setResRateDate(sdf.format(purchaseDt));
        shopResRec.setStockPerRate(String.format("%.2f", amount));
        if (Float.parseFloat(selectedShopRes.getRate())==0){ 
            shopResRec.setId(selectedShopRes.getId()); //if the rate= 0, 
//            it means this shop resource has just been linked, and no
//            acquiring has been done so far.So, this shopresource is will be updated
            shopresflag = 1;
        } else {  
//            else while we are creating ShopResDTO to add shop acquire record, with the same shop and res id 
            farmondto = clientService.callMaxShopResIdService(farmondto);
            int shopresid = Integer.parseInt(farmondto.getShopresrec().getId());
            if (shopresid == 0) {
                shopresid = 1;
            } else {
                shopresid = shopresid + 1;
            }
            shopResRec.setId(String.valueOf(shopresid));
            shopresflag =2;
        }
//        contruction of resourceacquire record
        ResAcquireDTO resAcquireRec = new ResAcquireDTO();  
        farmondto = clientService.callMaxResAcqIdService(farmondto);
        int acquireid = Integer.parseInt(farmondto.getResacqrec().getAcquireId());
        if (acquireid == 0) {
            resAcquireRec.setAcquireId("1");
        } else {
            resAcquireRec.setAcquireId(String.valueOf(acquireid + 1));
        }
        
        resAcquireRec.setResoureId(selectedShopRes.getResourceId());
        resAcquireRec.setAmount(String.format("%.2f", amount));
        resAcquireRec.setAcquireDate(sdf.format(purchaseDt));

//        contruction of expense record
        ExpenseDTO expenseRec = new ExpenseDTO();
        farmondto = clientService.callMaxExpIdService(farmondto);
        int expenseid = Integer.parseInt(farmondto.getExpenserec().getExpenseId());
        if (expenseid == 0) {
            expenseRec.setExpenseId("1");
        } else {
            expenseRec.setExpenseId(String.valueOf(expenseid + 1));
        }
        expenseRec.setDate(sdf.format(purchaseDt));
        expenseRec.setExpenseType("RES");
        expenseRec.setExpenseRefId(resAcquireRec.getAcquireId()); //######resourcecrop acq id
        float calculatedAmount = rate * amount;
        expenseRec.setExpenditure(String.format("%.2f", calculatedAmount));
        expenseRec.setCommString(comments);


//        contruction of farmresource record
//        pt:selectedShopRes has the corresponding resource record in the farmresource 
//        which is fetched here for updating with available amount of resource.
        FarmresourceDTO resourceRec = new FarmresourceDTO();
        resourceRec.setResourceId(selectedShopRes.getResourceId());
        farmondto.setFarmresourcerec(resourceRec);
        farmondto = clientService.callResnameForIdService(farmondto);
        resourceRec = farmondto.getFarmresourcerec();
        float amountAcquired = amount + Float.parseFloat(resourceRec.getAvailableAmt());
        resourceRec.setAvailableAmt(String.format("%.2f", amountAcquired));
        if (cropwt > 0) {
            if(resourceRec.getCropweight()==null)
                amountAcquired = cropwt + Float.parseFloat("0.00");
            else
                amountAcquired = cropwt + Float.parseFloat(resourceRec.getCropweight());
            resourceRec.setCropweight(String.format("%.2f", amountAcquired));
        } else resourceRec.setCropweight(null);
        
//        starting of database operation
        farmondto.setResacqrec(resAcquireRec);
        farmondto = clientService.callAddResacqService(farmondto);
        int acqres = farmondto.getResponses().getFarmon_ADD_RES();
        if (acqres == SUCCESS) {
            sqlFlag = sqlFlag + 1;
        } else {
            if (acqres == DB_DUPLICATE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        "Duplicate record error for resourceacquire table");
                f.addMessage(null, message);
            }
            if (acqres == DB_SEVERE) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                        "Failure on insert in resourceacquire table");
                f.addMessage(null, message);
            }

        }

        if (sqlFlag == 1) {
            farmondto.setExpenserec(expenseRec);
            farmondto = clientService.callAddExpService(farmondto);
            int expres = farmondto.getResponses().getFarmon_ADD_RES();
            if (expres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (expres == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "Duplicate record error for expense table");
                    f.addMessage(null, message);
                }
                if (expres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure"
                            , "Failure on insert in expense table");
                    f.addMessage(null, message);
                }
                farmondto = clientService.callDelResAcqService(farmondto);
                int delacq = farmondto.getResponses().getFarmon_DEL_RES();
                if (delacq == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "acquireresource record could not be deleted");
                    f.addMessage(null, message);
                }
            }
        }

        if (sqlFlag == 2) {
            farmondto.setFarmresourcerec(resourceRec);
            farmondto = clientService.callEditFarmresService(farmondto);
            int resres = farmondto.getResponses().getFarmon_EDIT_RES();
            if (resres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (resres == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "This resource record does not exist.");
                    f.addMessage(null, message);
                }
                if (resres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "Failure on edit in resource table");
                    f.addMessage(null, message);
                }
                
                farmondto = clientService.callDelResAcqService(farmondto);
                int delacq = farmondto.getResponses().getFarmon_DEL_RES();
                if (delacq == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "acquireresource record could not be deleted");
                    f.addMessage(null, message);
                }
                
                farmondto = clientService.callDelExpService(farmondto);
                int delexpense = farmondto.getResponses().getFarmon_DEL_RES();
                if (delexpense == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "expense record could not be deleted");
                    f.addMessage(null, message);
                }
            }
        }
        
        if (sqlFlag == 3) {            
            int shopres;
            farmondto.setShopresrec(shopResRec);
            if(shopresflag==1){
                farmondto = clientService.callEditShopresService(farmondto);
                shopres = farmondto.getResponses().getFarmon_EDIT_RES();
            } else {
                farmondto = clientService.callAddShopresService(farmondto);
                shopres = farmondto.getResponses().getFarmon_ADD_RES();
            }
            
            if (shopres == SUCCESS) {
                sqlFlag = sqlFlag + 1;
            } else {
                if (shopres == DB_NON_EXISTING) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "This shopresource record does not exist.");
                    f.addMessage(null, message);
                }
                if (shopres == DB_DUPLICATE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "This shopresource record does not exist.");
                    f.addMessage(null, message);
                }
                if (shopres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "Failure on edit in shopresource table");
                    f.addMessage(null, message);
                }
                farmondto = clientService.callDelResAcqService(farmondto);
                int delacq = farmondto.getResponses().getFarmon_DEL_RES();
                if (delacq == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "acquireresource record could not be deleted");
                    f.addMessage(null, message);
                }
                farmondto = clientService.callDelExpService(farmondto);
                int delexpense = farmondto.getResponses().getFarmon_DEL_RES();
                if (delexpense == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "expense record could not be deleted");
                    f.addMessage(null, message);
                }
                amountAcquired = Float.parseFloat(resourceRec.getAvailableAmt())-amount;
                resourceRec.setAvailableAmt(String.format("%.2f", amountAcquired));
                farmondto.setFarmresourcerec(resourceRec);
                farmondto = clientService.callEditFarmresService(farmondto);
                int resres = farmondto.getResponses().getFarmon_EDIT_RES();
                if (resres == DB_SEVERE) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Failure", 
                            "farmresource record could not be corrected");
                    f.addMessage(null, message);
                }
            }
        }
        
        if (sqlFlag==4) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", 
                    "Resource acquired successfully");
            f.addMessage(null, message);
        }
        return redirectUrl;
    }
    
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    
    public String getSelectedRes() {
        return selectedRes;
    }

    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    public String getSelectedShop() {
        return selectedShop;
    }

    public void setSelectedShop(String selectedShop) {
        this.selectedShop = selectedShop;
    }

    public List<ShopResDTO> getShopForSelectedRes() {
        return shopForSelectedRes;
    }

    public void setShopForSelectedRes(List<ShopResDTO> shopForSelectedRes) {
        this.shopForSelectedRes = shopForSelectedRes;
    }

    public List<FarmresourceDTO> getExistingresources() {
        return existingresources;
    }

    public void setExistingresources(List<FarmresourceDTO> existingresources) {
        this.existingresources = existingresources;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ShopResDTO getSelectedShopRes() {
        return selectedShopRes;
    }

    public void setSelectedShopRes(ShopResDTO selectedShopRes) {
        this.selectedShopRes = selectedShopRes;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getPurchaseDt() {
        return purchaseDt;
    }

    public void setPurchaseDt(Date purchaseDt) {
        this.purchaseDt = purchaseDt;
    }

    public boolean isSaveDisabled() {
        return saveDisabled;
    }

    public void setSaveDisabled(boolean saveDisabled) {
        this.saveDisabled = saveDisabled;
    }

    public String getSelectedResName() {
        return selectedResName;
    }

    public void setSelectedResName(String selectedResName) {
        this.selectedResName = selectedResName;
    }

    public List<ShopResDTO> getSelectedShopResLst() {
        return selectedShopResLst;
    }

    public void setSelectedShopResLst(List<ShopResDTO> selectedShopResLst) {
        this.selectedShopResLst = selectedShopResLst;
    }

    public String getRescat() {
        return rescat;
    }

    public void setRescat(String rescat) {
        this.rescat = rescat;
    }

    public float getCropwt() {
        return cropwt;
    }

    public void setCropwt(float cropwt) {
        this.cropwt = cropwt;
    }

    public String getCropwtunit() {
        return cropwtunit;
    }

    public void setCropwtunit(String cropwtunit) {
        this.cropwtunit = cropwtunit;
    }

    public boolean isCropwtReadonly() {
        return cropwtReadonly;
    }

    public void setCropwtReadonly(boolean cropwtReadonly) {
        this.cropwtReadonly = cropwtReadonly;
    }

    

    
}
