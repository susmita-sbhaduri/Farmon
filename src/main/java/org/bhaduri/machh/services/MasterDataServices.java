/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.machh.services;

import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.transaction.UserTransaction;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bhaduri.machh.DA.CropDAO;
import org.bhaduri.machh.DA.EmpLeaveDAO;
import org.bhaduri.machh.DA.EmpexpenseDAO;
import org.bhaduri.machh.DA.EmployeeDAO;
import org.bhaduri.machh.DA.ExpenseDAO;
import org.bhaduri.machh.DA.HarvestDAO;
import org.bhaduri.machh.DA.FarmresourceDAO;
import org.bhaduri.machh.DA.LabourCropDAO;
import org.bhaduri.machh.DA.ResAcquireDAO;
import org.bhaduri.machh.DA.ResourceCropDAO;
import org.bhaduri.machh.DA.ShopDAO;
import org.bhaduri.machh.DA.ShopResCropDAO;
import org.bhaduri.machh.DA.ShopResDAO;
import org.bhaduri.machh.DA.SiteDAO;
import org.bhaduri.machh.DA.TaskplanDAO;



import org.bhaduri.machh.DA.UsersDAO;
import org.bhaduri.machh.DTO.CropDTO;
import org.bhaduri.machh.DTO.EmpExpDTO;
import org.bhaduri.machh.DTO.EmpLeaveDTO;
import org.bhaduri.machh.DTO.EmployeeDTO;
import org.bhaduri.machh.DTO.ExpenseDTO;
import org.farmon.farmondto.HarvestDTO;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_DUPLICATE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_NON_EXISTING;
import static org.bhaduri.machh.DTO.MachhResponseCodes.DB_SEVERE;
import static org.bhaduri.machh.DTO.MachhResponseCodes.SUCCESS;
import org.bhaduri.machh.DTO.FarmresourceDTO;
import org.bhaduri.machh.DTO.LabourCropDTO;
import org.bhaduri.machh.DTO.ResAcqReportDTO;
import org.bhaduri.machh.DTO.ResAcquireDTO;
import org.bhaduri.machh.DTO.ResCropAllSummaryDTO;
import org.bhaduri.machh.DTO.ResCropSummaryDTO;
import org.bhaduri.machh.DTO.ResourceCropDTO;
import org.bhaduri.machh.DTO.ShopDTO;
import org.bhaduri.machh.DTO.ShopResCropDTO;
import org.bhaduri.machh.DTO.ShopResDTO;
import org.bhaduri.machh.DTO.SiteDTO;
import org.bhaduri.machh.DTO.TaskPlanDTO;
import org.bhaduri.machh.entities.Users;
//import org.bhaduri.machh.UserAuthentication;
import org.bhaduri.machh.DTO.UsersDTO;
import org.bhaduri.machh.JPA.exceptions.NonexistentEntityException;
import org.bhaduri.machh.JPA.exceptions.PreexistingEntityException;
import org.bhaduri.machh.entities.Crop;
import org.bhaduri.machh.entities.Empexpense;
import org.bhaduri.machh.entities.Empleave;
import org.bhaduri.machh.entities.Employee;
import org.bhaduri.machh.entities.Expense;

import org.bhaduri.machh.entities.Harvest;
import org.bhaduri.machh.entities.Shop;
import org.bhaduri.machh.entities.Shopresource;
import org.bhaduri.machh.entities.Site;
import org.bhaduri.machh.entities.Farmresource;
import org.bhaduri.machh.entities.Labourcrop;
import org.bhaduri.machh.entities.Resourceaquire;
import org.bhaduri.machh.entities.Resourcecrop;
import org.bhaduri.machh.entities.Shoprescrop;
import org.bhaduri.machh.entities.Taskplan;

public class MasterDataServices {
    private final EntityManagerFactory emf;
    private final UserTransaction utx;

    
    public MasterDataServices() throws NamingException {
        emf = Persistence.createEntityManagerFactory("org.bhaduri_Machh_jar_1.0-SNAPSHOT");
        utx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
//        utx = null;
    }
    public UsersDTO getUserAuthDetails(String username, String password) {
        UsersDAO useraccess = new UsersDAO(utx,emf);  
        UsersDTO userAuthDto = new UsersDTO();
        try {            
            Users userInfo = useraccess.getUserDetails(username, password);
            userAuthDto.setID(userInfo.getId());
            userAuthDto.setUsername(userInfo.getUsername());
            userAuthDto.setPassword(userInfo.getPassword());
            return userAuthDto;
        }
        catch (NoResultException e) {
            System.out.println("No user found with provided credentials.");
            userAuthDto.setID("null");
            return userAuthDto;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getUserAuthDetails.");
            return null;
        }
    }
    
    public List<String> getCropCat() {
        CropDAO cropdao = new CropDAO(utx, emf);  
        List<String> recordList = new ArrayList<>();
        try {  
            List<String> cropcategories = cropdao.listCropCat();
            for (int i = 0; i < cropcategories.size(); i++) {
                recordList.add(cropcategories.get(i));
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No crop categories are found.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getCropCat().");
            return null;
        }
    }
    
    public List<String> getCropNameForCat(String cropcat) {
        CropDAO cropdao = new CropDAO(utx, emf);  
        List<String> recordList = new ArrayList<>();
//        CropPk record = new CropPk();
        try {  
            List<String> cropnamesforcat = cropdao.listCropName(cropcat);
            for (int i = 0; i < cropnamesforcat.size(); i++) {
//                record.setCropCategory(croppkentities.get(i).getCropPK().getCropcategory());
//                record.setCropName(cropnamesforcat.get(i).getCropPK().getCrop());                
                recordList.add(cropnamesforcat.get(i));
//                record = new String();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No crop names are found for this crop category");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getCropNameForCat.");
            return null;
        }
    }
    
    
    public List<SiteDTO> getSites() {
        SiteDAO sitedao = new SiteDAO(utx, emf);  
        List<SiteDTO> recordList = new ArrayList<>();
        SiteDTO record = new SiteDTO();
        try {  
            List<Site> sites = sitedao.listSite();
            for (int i = 0; i < sites.size(); i++) {
                record.setSiteID(sites.get(i).getId().toString());
                record.setSiteName(sites.get(i).getSitename());
                record.setSiteType(sites.get(i).getSitetype());
                if (sites.get(i).getSize() != null) {
                    record.setSize(String.format("%.2f", sites.get(i).getSize()));
                } else
                    record.setSize(null);
                record.setUnit(sites.get(i).getUnit());
                
                recordList.add(record);
                record = new SiteDTO();
            }    
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No sites are found.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getSiteNames.");
            return null;
        }
    }
    
    public int getMaxHarvestId(){
        HarvestDAO harvestdao = new HarvestDAO(utx, emf); 
        try {
            return harvestdao.getMaxId();
        }
        catch (NoResultException e) {
            System.out.println("No records in harvest table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxHarvestId().");
//            return DB_SEVERE;
            return 0;
        }
    }
    
      public List<HarvestDTO> getActiveHarvestList() {
        HarvestDAO harvestdao = new HarvestDAO(utx, emf);  
        List<HarvestDTO> recordList = new ArrayList<>();
        HarvestDTO record = new HarvestDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Harvest> harvestlist = harvestdao.getActiveList();
            for (int i = 0; i < harvestlist.size(); i++) {
                record.setHarvestid(String.valueOf(harvestlist.get(i).getHarvestid()));
                record.setSiteid(String.valueOf(harvestlist.get(i).getSiteid()));
                record.setSiteName(getSiteNameForId(String.valueOf(harvestlist.get(i).getSiteid()))
                        .getSiteName());
                
                record.setCropid(String.valueOf(harvestlist.get(i).getCropid()));
                record.setCropName(getCropPerPk(String.valueOf(harvestlist.get(i).getCropid()))
                        .getCropName());
                record.setCropCategory(getCropPerPk(String.valueOf(harvestlist.get(i).getCropid()))
                        .getCropCategory());
                
                mysqlDate = harvestlist.get(i).getSowingdt();                    
                record.setSowingDate(formatter.format(mysqlDate));
                record.setDesc(harvestlist.get(i).getDescription());
                recordList.add(record);
                record = new HarvestDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No crops are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getActiveHarvestList.");
            return null;
        }
    }
     
    public HarvestDTO getHarvestRecForId(String harvestid) {
        HarvestDAO harvestdao = new HarvestDAO(utx, emf);
        HarvestDTO record = new HarvestDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Harvest harvestrec = harvestdao.getHarvestForId(Integer.parseInt(harvestid));
            record.setHarvestid(harvestid);
            record.setSiteid(String.valueOf(harvestrec.getSiteid()));
            record.setSiteName(getSiteNameForId(String.valueOf(harvestrec.getSiteid()))
                    .getSiteName());
            
            record.setCropid(String.valueOf(harvestrec.getCropid()));
            record.setCropName(getCropPerPk(String.valueOf(harvestrec.getCropid()))
                    .getCropName());
            record.setCropCategory(getCropPerPk(String.valueOf(harvestrec.getCropid()))
                    .getCropCategory());
            
            mysqlDate = harvestrec.getSowingdt();
            record.setSowingDate(formatter.format(mysqlDate));
            mysqlDate = harvestrec.getHarvestingdt();
            if (mysqlDate != null) {
                record.setHarvestDate(formatter.format(mysqlDate));
            } else {
                record.setHarvestDate(null);
            }
            record.setDesc(harvestrec.getDescription());
            return record;
        } catch (NoResultException e) {
            System.out.println("No harvest record found for the given harvestid.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getHarvestRecForId(String harvestid).");
            return null;
        }
    }  
    public int editHarvestRecord(HarvestDTO harvestrec) {        
        HarvestDAO harvestdao = new HarvestDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Harvest record = new Harvest();
            record.setHarvestid(Integer.valueOf(harvestrec.getHarvestid()));
            record.setSiteid(Integer.parseInt(harvestrec.getSiteid()));                      
            record.setCropid(Integer.parseInt(harvestrec.getCropid()));
            
            mysqlDate = formatter.parse(harvestrec.getSowingDate());
            record.setSowingdt(mysqlDate);
            if (harvestrec.getHarvestDate() != null) {
                mysqlDate = formatter.parse(harvestrec.getHarvestDate());
                record.setHarvestingdt(mysqlDate);
            } else {
                record.setHarvestingdt(null);
            }
            record.setDescription(harvestrec.getDesc());            
            harvestdao.edit(record);
            return SUCCESS;
        } catch (NoResultException e) {
            System.out.println("This harvest record to be edited, does not exist ");
            return DB_NON_EXISTING;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in editHarvestRecord.");
            return DB_SEVERE;
        }
    }
    public int addHarvestRecord(HarvestDTO harvestrec) {        
        HarvestDAO harvestdao = new HarvestDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Harvest record = new Harvest();
            record.setHarvestid(Integer.valueOf(harvestrec.getHarvestid()));
            record.setSiteid(Integer.parseInt(harvestrec.getSiteid()));                      
            record.setCropid(Integer.parseInt(harvestrec.getCropid()));
            
            mysqlDate = formatter.parse(harvestrec.getSowingDate());
            record.setSowingdt(mysqlDate);
            if (harvestrec.getHarvestDate() != null) {
                mysqlDate = formatter.parse(harvestrec.getHarvestDate());
                record.setHarvestingdt(mysqlDate);
            } else {
                record.setHarvestingdt(null);
            }
            record.setDescription(harvestrec.getDesc());            
            harvestdao.create(record);
            return SUCCESS;
        } 
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this Harvest record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addHarvestRecord.");
            return DB_SEVERE;
        }
    }
    
    public List<ShopResDTO> getShopResName() {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
        List<ShopResDTO> recordList = new ArrayList<>();
        ShopResDTO record = new ShopResDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Shopresource> shopreslist = shopresdao.getShopResList();
            for (int i = 0; i < shopreslist.size(); i++) {
                record.setId(String.valueOf(shopreslist.get(i).getId()));
                record.setShopId(String.valueOf(shopreslist.get(i).getShopid()));
                record.setResourceId(String.valueOf(shopreslist.get(i).getResourceid()));
                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
                record.setRate(String.format("%.2f", shopreslist.get(i).getRate().floatValue()));              
                record.setResAppId(String.valueOf(shopreslist.get(i).getResappid()));
                record.setAmtApplied(String.format("%.2f", shopreslist.get(i).getAppliedamt().floatValue()));
                mysqlDate = shopreslist.get(i).getResrtdate();
                
                if (mysqlDate != null) {
                    record.setResRateDate(formatter.format(mysqlDate));
                } else {
                    record.setResRateDate("");
                }
                record.setStockPerRate(String.format("%.2f", shopreslist.get(i).getStockperrt()));
                recordList.add(record);
                record = new ShopResDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No ShopResource records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResName().");
            return null;
        }
    }
    public List<ShopResDTO> getShopResForResid(String resid) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
        List<ShopResDTO> recordList = new ArrayList<>();
        ShopResDTO record = new ShopResDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Shopresource> shopreslist = shopresdao.getShopDtlssForRes(Integer.parseInt(resid));
            for (int i = 0; i < shopreslist.size(); i++) {
                record.setId(String.valueOf(shopreslist.get(i).getId()));
                record.setShopId(String.valueOf(shopreslist.get(i).getShopid()));
                record.setResourceId(String.valueOf(shopreslist.get(i).getResourceid()));
                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
                record.setRate(String.format("%.2f", shopreslist.get(i).getRate().floatValue()));              
                record.setUnit(getResourceNameForId(shopreslist.get(i).getResourceid()).getUnit());
                record.setResAppId(String.valueOf(shopreslist.get(i).getResappid()));
                record.setAmtApplied(String.format("%.2f", shopreslist.get(i).getAppliedamt().floatValue()));
                mysqlDate = shopreslist.get(i).getResrtdate();
                
                if (mysqlDate != null) {
                    record.setResRateDate(formatter.format(mysqlDate));
                } else {
                    record.setResRateDate("");
                }
                record.setStockPerRate(String.format("%.2f", shopreslist.get(i).getStockperrt()));
                recordList.add(record);
                record = new ShopResDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No ShopResource records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResName().");
            return null;
        }
    }
    public List<ShopResDTO> getAllShopResForResid(String resid) { // for AcquireResource.java
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
        List<ShopResDTO> recordList = new ArrayList<>();
        ShopResDTO record = new ShopResDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Shopresource> shopreslist = shopresdao.getShopResLstPerRes(Integer.parseInt(resid));
            for (int i = 0; i < shopreslist.size(); i++) {
                record.setId(String.valueOf(shopreslist.get(i).getId()));
                record.setShopId(String.valueOf(shopreslist.get(i).getShopid()));
                record.setResourceId(String.valueOf(shopreslist.get(i).getResourceid()));
                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
                record.setRate(String.format("%.2f", shopreslist.get(i).getRate().floatValue()));              
                record.setUnit(getResourceNameForId(shopreslist.get(i).getResourceid()).getUnit());
                record.setResAppId(String.valueOf(shopreslist.get(i).getResappid()));
                record.setAmtApplied(String.format("%.2f", shopreslist.get(i).getAppliedamt().floatValue()));
                mysqlDate = shopreslist.get(i).getResrtdate();
                
                if (mysqlDate != null) {
                    record.setResRateDate(formatter.format(mysqlDate));
                } else {
                    record.setResRateDate("");
                }
                record.setStockPerRate(String.format("%.2f", shopreslist.get(i).getStockperrt()));
                recordList.add(record);
                record = new ShopResDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No ShopResource records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResName().");
            return null;
        }
    }
    
    public List<ShopResDTO> getShopResForApplyRes(String appid, String resid) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
        List<ShopResDTO> recordList = new ArrayList<>();
        ShopResDTO record = new ShopResDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Shopresource> shopreslist = shopresdao.getShopResPerAppid(Integer.parseInt(appid), Integer.parseInt(resid));
            for (int i = 0; i < shopreslist.size(); i++) {
                record.setId(String.valueOf(shopreslist.get(i).getId()));
                record.setShopId(String.valueOf(shopreslist.get(i).getShopid()));
                record.setResourceId(String.valueOf(shopreslist.get(i).getResourceid()));
                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
                record.setRate(String.format("%.2f", shopreslist.get(i).getRate().floatValue()));              
                record.setUnit(getResourceNameForId(shopreslist.get(i).getResourceid()).getUnit());
                record.setResAppId(String.valueOf(shopreslist.get(i).getResappid()));
                record.setAmtApplied(String.format("%.2f", shopreslist.get(i).getAppliedamt().floatValue()));
                mysqlDate = shopreslist.get(i).getResrtdate();
                
                if (mysqlDate != null) {
                    record.setResRateDate(formatter.format(mysqlDate));
                } else {
                    record.setResRateDate("");
                }
                record.setStockPerRate(String.format("%.2f", shopreslist.get(i).getStockperrt()));
                recordList.add(record);
                record = new ShopResDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No ShopResource records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResName().");
            return null;
        }
    }
    
    public ShopResDTO getShopResForId(String Id) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);

        ShopResDTO record = new ShopResDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Shopresource shopres = shopresdao.getShopResForId(Integer.parseInt(Id));
            record.setId(String.valueOf(shopres.getId()));
            record.setShopId(String.valueOf(shopres.getShopid()));
            record.setResourceId(String.valueOf(shopres.getResourceid()));
//                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
//                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
            record.setRate(String.format("%.2f", shopres.getRate().floatValue()));
            record.setResAppId(String.valueOf(shopres.getResappid()));
                record.setAmtApplied(String.format("%.2f", shopres.getAppliedamt().floatValue()));
//                record.setUnit(getResourceNameForId(shopreslist.get(i).getResourceid()).getUnit());
            mysqlDate = shopres.getResrtdate();

            if (mysqlDate != null) {
                record.setResRateDate(formatter.format(mysqlDate));
            } else {
                record.setResRateDate("");
            }
            record.setStockPerRate(String.format("%.2f", shopres.getStockperrt()));

            return record;
        } catch (NoResultException e) {
            System.out.println("No ShopResource records are found");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResForId.");
            return null;
        }
    }
    
    public ShopDTO getShopNameForId(String shopid) {
        ShopDAO shopdao = new ShopDAO(utx, emf);        
        ShopDTO record = new ShopDTO();
        
        try {  
           Shop shoprec = shopdao.getShopName(Integer.parseInt(shopid));           
           record.setShopName(shoprec.getShopname());
           record.setShopId(shopid);
           record.setLocation(shoprec.getLocation());
           record.setContact(shoprec.getContact());
           record.setAvailabilityTime(shoprec.getAvailabilitytime());
           return record;
        }
        catch (NoResultException e) {
            System.out.println("No shop found for this shopid");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopNameForId(int shopid).");
            return null;
        }
    }
    
    public List<ShopDTO> getOtherShopsFor(String resourceId) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);
        List<Integer> shopsforres;
        ShopDTO record = new ShopDTO();
        List<ShopDTO> shoplist;
        List<ShopDTO> othershoplist = new ArrayList<>();
        try {
           shopsforres = shopresdao.getShopsForRes(Integer.parseInt(resourceId));
           shoplist = getShopList();
           
           if(!shoplist.isEmpty()){
               for (int i = 0; i < shoplist.size(); i++) {
                   if (!shopsforres.contains(Integer.valueOf(shoplist.get(i).getShopId()))) {
                       record.setShopId(shoplist.get(i).getShopId());
                       record.setShopName(shoplist.get(i).getShopName());
                       record.setLocation(shoplist.get(i).getLocation());
                       record.setContact(shoplist.get(i).getContact());
                       record.setAvailabilityTime(shoplist.get(i).getAvailabilityTime());
                       othershoplist.add(record);
                       record = new ShopDTO();
                   } 
               }                
            }        
           return othershoplist;           
        }
        catch (NoResultException e) {
            System.out.println("No shopres reference found for this resourceId");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getOtherShopsFor(int resourceId).");
            return null;
        }
    }
    
    public FarmresourceDTO getResourceNameForId(int resourceid) {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);        
        FarmresourceDTO record = new FarmresourceDTO();        
        try {  
           Farmresource resrec = resourcedao.getResourceName(resourceid); 
           record.setResourceId(Integer.toString(resourceid));
           record.setResourceName(resrec.getResourcename());
           record.setAvailableAmt(String.format("%.2f",resrec.getAvailableamount().floatValue()));
           record.setUnit(resrec.getUnit());
           if(resrec.getCropweight()!=null){
             record.setCropweight(String.format("%.2f", resrec.getCropweight().floatValue()));  
           }
           if(resrec.getCropwtunit()!=null){
               record.setCropwtunit(resrec.getCropwtunit());
           }
           return record;
        }
        catch (NoResultException e) {
            System.out.println("No resource found for this resourceid");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getResourceNameForId(int resourceid).");
            return null;
        }
    }
    
    public FarmresourceDTO getResourceIdForName(String resourcename) {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);        
        FarmresourceDTO record = new FarmresourceDTO();        
        try {  
           Farmresource resrec = resourcedao.getResourceId(resourcename); 
           record.setResourceId(Integer.toString(resrec.getResourceid()));
           record.setResourceName(resourcename);
           record.setAvailableAmt(String.format("%.2f",resrec.getAvailableamount().floatValue()));
           record.setUnit(resrec.getUnit());
           if(resrec.getCropweight()!=null){
             record.setCropweight(String.format("%.2f", resrec.getCropweight().floatValue()));  
           }
           if(resrec.getCropwtunit()!=null){
               record.setCropwtunit(resrec.getCropwtunit());
           }
           return record;
        }
        catch (NoResultException e) {
            System.out.println("No resourceid found for this resourcename");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getResourceIdForName.");
            return null;
        }
    }
    
     public List<FarmresourceDTO> getResourceList() {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);        
        FarmresourceDTO record = new FarmresourceDTO();
        List<FarmresourceDTO> recordList = new ArrayList<>();
        try {  
            List<Farmresource> resourcelist = resourcedao.getAllResource();
            for (int i = 0; i < resourcelist.size(); i++) {
                record.setResourceId(Integer.toString(resourcelist.get(i).getResourceid()));
                record.setResourceName(resourcelist.get(i).getResourcename());
                record.setAvailableAmt(String.format("%.2f",resourcelist.get(i).getAvailableamount().floatValue()));
                record.setUnit(resourcelist.get(i).getUnit());
                if (resourcelist.get(i).getCropweight() != null) {
                    record.setCropweight(String.format("%.2f", resourcelist.get(i).getCropweight().floatValue()));
                }
                if (resourcelist.get(i).getCropwtunit() != null) {
                    record.setCropwtunit(resourcelist.get(i).getCropwtunit());
                }
                recordList.add(record);
                record = new FarmresourceDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No Resoureces are added");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getResourceList().");
            return null;
        }
    }
     
    public List<FarmresourceDTO> getNonzeroResList() {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);        
        FarmresourceDTO record = new FarmresourceDTO();
        List<FarmresourceDTO> recordList = new ArrayList<>();
        try {  
            List<Farmresource> resourcelist = resourcedao.getNonzeroResource();
            for (int i = 0; i < resourcelist.size(); i++) {
                record.setResourceId(Integer.toString(resourcelist.get(i).getResourceid()));
                record.setResourceName(resourcelist.get(i).getResourcename());
                record.setAvailableAmt(String.format("%.2f",resourcelist.get(i).getAvailableamount().floatValue()));
                record.setUnit(resourcelist.get(i).getUnit());  
                if (resourcelist.get(i).getCropweight() != null) {
                    record.setCropweight(String.format("%.2f", resourcelist.get(i).getCropweight().floatValue()));
                }
                if (resourcelist.get(i).getCropwtunit() != null) {
                    record.setCropwtunit(resourcelist.get(i).getCropwtunit());
                }
                recordList.add(record);
                record = new FarmresourceDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No Resoureces are added");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getResourceList().");
            return null;
        }
    }
     
     
    public String getNextIdForRes(){
        List<FarmresourceDTO> reclist = getResourceList();
        List<Integer> resIdList =  new ArrayList<>();
        if(!reclist.isEmpty()){
            for (int i = 0; i < reclist.size(); i++) {
                resIdList.add(i, Integer.valueOf(reclist.get(i).getResourceId()));
            }
            int maxVal = Collections.max(resIdList);
            return String.valueOf(maxVal+1);
        }
        else return null;
    }
    
    public int addResource(FarmresourceDTO res) {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);                
        try {
            Farmresource recentity = new Farmresource();
            recentity.setResourceid(Integer.valueOf(res.getResourceId()));
            recentity.setResourcename(res.getResourceName());
            recentity.setAvailableamount(BigDecimal.
                    valueOf(Double.parseDouble(res.getAvailableAmt())));
            recentity.setUnit(res.getUnit());
            if (res.getCropweight() != null) {
                recentity.setCropweight(BigDecimal.
                        valueOf(Double.parseDouble(res.getCropweight())));
            } else 
                recentity.setCropweight(null);
            recentity.setCropwtunit(res.getCropwtunit());
            
            resourcedao.create(recentity);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this Resource record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addResource(FarmresourceDTO res).");
            return DB_SEVERE;
        }
    }
    
    public int editResource(FarmresourceDTO res) {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);                
        try {
            Farmresource recentity = new Farmresource();
            recentity.setResourceid(Integer.valueOf(res.getResourceId()));
            recentity.setResourcename(res.getResourceName());
            recentity.setAvailableamount(BigDecimal.valueOf(Double.parseDouble
            (res.getAvailableAmt())));
            recentity.setUnit(res.getUnit());
            if (res.getCropweight() != null) {
                recentity.setCropweight(BigDecimal.
                        valueOf(Double.parseDouble(res.getCropweight())));
            } else 
                recentity.setCropweight(null);
            recentity.setCropwtunit(res.getCropwtunit());
            resourcedao.edit(recentity);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("This Resource record does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editResource(FarmresourceDTO res).");
            return DB_SEVERE;
        }
    }
    
    public int delResource(FarmresourceDTO res) {
        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);                
        try {
            Farmresource recentity = new Farmresource();
            recentity.setResourceid(Integer.valueOf(res.getResourceId())); 
            resourcedao.destroy(recentity.getResourceid());
            return SUCCESS;
        }
        catch (NonexistentEntityException e) {
            System.out.println("Record for this Farmresource does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in delResource(FarmresourceDTO res).");
            return DB_SEVERE;
        }
    }
    
    public int getNextIdForResAquire(){
        ResAcquireDAO resourcedao = new ResAcquireDAO(utx, emf);
        try {
            return resourcedao.getMaxAqId();
        }
        catch (NoResultException e) {
            System.out.println("No records");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getNextIdForResAquire().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public int addAcquireResource(ResAcquireDTO acqres) {
        ResAcquireDAO acqresdao = new ResAcquireDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Resourceaquire rec = new Resourceaquire();
            rec.setAquireid(Integer.valueOf(acqres.getAcquireId()));
            rec.setResourceid(Integer.parseInt(acqres.getResoureId()));
            rec.setAmount(BigDecimal.valueOf(Double.parseDouble(acqres.getAmount())));
            mysqlDate = formatter.parse(acqres.getAcquireDate());
            rec.setAquiredate(mysqlDate);
            acqresdao.create(rec);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this ResourceAcquire record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addAcquireResource(ResAcquireDTO acqres).");
            return DB_SEVERE;
        }
    }
    
    public int delAcquireResource(ResAcquireDTO acqres) {
        ResAcquireDAO acqresdao = new ResAcquireDAO(utx, emf);                
        try {
            Resourceaquire rec = new Resourceaquire();
            rec.setAquireid(Integer.valueOf(acqres.getAcquireId())); 
            acqresdao.destroy(rec.getAquireid());
            return SUCCESS;
        }
        catch (NonexistentEntityException e) {
            System.out.println("Record for this resourceacquire does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in delAcquireResource(ResAcquireDTO acqres).");
            return DB_SEVERE;
        }
    }
    public ResAcquireDTO getResAcqPerDate(String acquireDt, String resid) {
        ResAcquireDAO acqresdao = new ResAcquireDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        ResAcquireDTO record = new ResAcquireDTO();
        try {
            Resourceaquire result = acqresdao.resAcqPerDt(formatter.parse(acquireDt)
                    , Integer.parseInt(resid));

            record.setAcquireId(result.getAquireid().toString());
            record.setResoureId(String.valueOf(result.getResourceid()));
            record.setAmount(String.format("%.2f", result));
            mysqlDate = result.getAquiredate();

            if (mysqlDate != null) {
                record.setAcquireDate(formatter.format(mysqlDate));
            } else {
                record.setAcquireDate("");
            }

            return record;
        } catch (NoResultException e) {
            System.out.println("No resourceaquire records are found");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getResAcqPerDate.");
            return null;
        }
    }
    
    public List<ResAcqReportDTO> getAcqResources() {
        ResAcquireDAO acqresdao = new ResAcquireDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        List<ResAcqReportDTO> recordList = new ArrayList<>();
        ResAcqReportDTO record = new ResAcqReportDTO();
        float amounttotal = 0;
        float totalcost = 0;
        try {
            List<Resourceaquire> reclist = acqresdao.getAllResAcquired();
            int lasti = reclist.size()-1;
            for (int i = 0; i < reclist.size()-1; i++) {
                record.setResName(getResourceNameForId(reclist.get(i).getResourceid()).getResourceName());
                record.setAcqAmt(String.format("%.2f", reclist.get(i).getAmount()));
                String expenseCategory = "RES";
                record.setCost(getLabExpenseForHrvst(reclist.get(i).getAquireid().toString()
                        , expenseCategory).getExpenditure());
                mysqlDate = reclist.get(i).getAquiredate();
                if (mysqlDate != null) {
                    record.setAcqDt(formatter.format(mysqlDate));
                } else {
                    record.setAcqDt("");
                } 
                recordList.add(record);
                record = new ResAcqReportDTO();
                
                if(getResourceNameForId(reclist.get(i).getResourceid()).getResourceId().
                        equals(getResourceNameForId(reclist.get(i+1).getResourceid()).getResourceId())){
                    amounttotal = amounttotal+reclist.get(i).getAmount().floatValue();
                    totalcost = totalcost + Float.parseFloat(getLabExpenseForHrvst(reclist.get(i).getAquireid().toString()
                        , expenseCategory).getExpenditure());
                } else {
                    amounttotal = amounttotal+reclist.get(i).getAmount().floatValue();
                    totalcost = totalcost + Float.parseFloat(getLabExpenseForHrvst(reclist.get(i).getAquireid().toString()
                        , expenseCategory).getExpenditure());
                    record.setResName("Total");
                    record.setAcqDt("");
                    record.setAcqAmt(String.format("%.2f", amounttotal));
                    record.setCost(String.format("%.2f", totalcost));
                    recordList.add(record);
                    record = new ResAcqReportDTO();
                    amounttotal = 0;
                    totalcost = 0;
//                    amounttotal = reclist.get(i+1).getAmount().floatValue();  
//                    totalcost = Float.parseFloat(getLabExpenseForHrvst(reclist.get(i+1).getAquireid().toString()
//                        , expenseCategory).getExpenditure());
                }
            }
            record.setResName(getResourceNameForId(reclist.get(lasti).getResourceid()).getResourceName());
            record.setAcqAmt(String.format("%.2f", reclist.get(lasti).getAmount()));
            String expenseCategory = "RES";
            record.setCost(getLabExpenseForHrvst(reclist.get(lasti).getAquireid().toString(),
                     expenseCategory).getExpenditure());
            mysqlDate = reclist.get(lasti).getAquiredate();
            if (mysqlDate != null) {
                record.setAcqDt(formatter.format(mysqlDate));
            } else {
                record.setAcqDt("");
            }
            recordList.add(record);
            record = new ResAcqReportDTO();
            
            amounttotal = amounttotal+reclist.get(lasti).getAmount().floatValue();
                    totalcost = totalcost + Float.parseFloat(getLabExpenseForHrvst(reclist.get(lasti).getAquireid().toString()
                        , expenseCategory).getExpenditure());
            
            record.setResName("Total");
            record.setAcqDt("");
            record.setAcqAmt(String.format("%.2f", amounttotal));
            record.setCost(String.format("%.2f", totalcost));
            recordList.add(record);
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No Resourceaquire record is found.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getAcqResources.");
            return null;
        }
    }
    
    public List<ShopDTO> getShopList() {
        ShopDAO shopdao = new ShopDAO(utx, emf);        
        ShopDTO record = new ShopDTO();
        List<ShopDTO> recordList = new ArrayList<>();
        try {  
            List<Shop> shoplist = shopdao.getAllShops();
            for (int i = 0; i < shoplist.size(); i++) {
                record.setShopId(shoplist.get(i).getShopid().toString());
                record.setShopName(shoplist.get(i).getShopname());
                record.setLocation(shoplist.get(i).getLocation());
                record.setContact(shoplist.get(i).getContact());
                record.setAvailabilityTime(shoplist.get(i).getAvailabilitytime());
                recordList.add(record);
                record = new ShopDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No Shops are added");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopList().");
            return null;
        }
    }
    
    public SiteDTO getSiteNameForId(String siteid) {
        SiteDAO sitedao = new SiteDAO(utx, emf);        
        SiteDTO record = new SiteDTO();        
        try {  
           Site siterec = sitedao.getSiteName(Integer.parseInt(siteid)); 
           record.setSiteID(siteid);
           record.setSiteType(siterec.getSitetype());
           record.setSiteName(siterec.getSitename());
           if(siterec.getSize()!=null){
             record.setSize(String.format("%.2f",siterec.getSize().floatValue()));
           } else record.setSize(null);           
           record.setUnit(siterec.getUnit());
           return record;
        }
        catch (NoResultException e) {
            System.out.println("No Site found for this siteid");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getSiteNameForId(String siteid).");
            return null;
        }        
    }
    
    public int getMaxIdForShopRes(){
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);
        try {
            return shopresdao.getMaxId();
        }
        catch (NoResultException e) {
            System.out.println("No records in expense table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getNextIdForExpense().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public int addShopResource(ShopResDTO shopres) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {            
            Shopresource shopresrec = new Shopresource();
            shopresrec.setId(Integer.valueOf(shopres.getId()));
            shopresrec.setResourceid(Integer.parseInt(shopres.getResourceId()));
            shopresrec.setShopid(Integer.parseInt(shopres.getShopId()));
            shopresrec.setRate(BigDecimal.valueOf(Double.parseDouble(shopres.getRate())));
//            shopresrec.setResappid(Integer.valueOf(shopres.getResAppId()));            
//            shopresrec.setAppliedamt(BigDecimal
//                    .valueOf(Double.parseDouble(shopres.getAmtApplied())));
            if(shopres.getResRateDate()==null){
                shopresrec.setResrtdate(null);
            } else {
                mysqlDate = formatter.parse(shopres.getResRateDate());
                shopresrec.setResrtdate(mysqlDate);
            }
            shopresrec.setStockperrt(BigDecimal.valueOf(Double.parseDouble(shopres.getStockPerRate())));
            shopresrec.setResappid(Integer.valueOf("0"));            
            shopresrec.setAppliedamt(BigDecimal
                    .valueOf(Double.parseDouble("0.00")));
            shopresdao.create(shopresrec);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this ShopResource record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addShopResource(ShopResDTO shopres).");
            return DB_SEVERE;
        }
    }
    
    public int deleteShopForRes(ShopResDTO shopres) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
        try {
            Shopresource record = new Shopresource();
            record.setId(Integer.valueOf(shopres.getId()));
            shopresdao.destroy(record.getId());
            return SUCCESS;
        }
        catch (NonexistentEntityException e) {
            System.out.println("Record for this ShopResource does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in deleteShopForRes(ShopResDTO shopres).");
            return DB_SEVERE;
        }
    }
    
    public int deleteShopResForResid(String resid) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
        try {
            int rowsDeleted = shopresdao.delForResid(Integer.parseInt(resid));
            if(rowsDeleted>0){
                return SUCCESS;
            } else {
                return DB_NON_EXISTING;
            }
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in deleteShopResForResid(String resid).");
            return DB_SEVERE;
        }
    }
    
    public int editShopForRes(ShopResDTO shopres) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {            
            Shopresource shopresrec = new Shopresource();
            shopresrec.setId(Integer.valueOf(shopres.getId()));
            shopresrec.setShopid(Integer.parseInt(shopres.getShopId()));
            shopresrec.setResourceid(Integer.parseInt(shopres.getResourceId()));
            shopresrec.setRate(BigDecimal.valueOf(Double.parseDouble(shopres.getRate())));
            mysqlDate = formatter.parse(shopres.getResRateDate());
            shopresrec.setResrtdate(mysqlDate);
            shopresrec.setStockperrt(BigDecimal
                    .valueOf(Double.parseDouble(shopres.getStockPerRate())));
            shopresrec.setResappid(Integer.valueOf("0"));            
            shopresrec.setAppliedamt(BigDecimal
                    .valueOf(Double.parseDouble("0.00")));
            shopresdao.edit(shopresrec);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("No shopresource record is found for shioid and resourceid");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editShopForRes(ShopResDTO shopres).");
            return DB_SEVERE;
        }
    }
    
    public List<ShopResDTO> getResShopForPk(String resourceId, String shopId) {
        ShopResDAO shopresdao = new ShopResDAO(utx, emf);
        List<ShopResDTO> recordList = new ArrayList<>();
        ShopResDTO record = new ShopResDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Shopresource> shopreslist = shopresdao.getShopResList(Integer.parseInt(resourceId), Integer.parseInt(shopId));
            for (int i = 0; i < shopreslist.size(); i++) {
                record.setId(String.valueOf(shopreslist.get(i).getId()));
                record.setShopId(String.valueOf(shopreslist.get(i).getShopid()));
                record.setResourceId(String.valueOf(shopreslist.get(i).getResourceid()));
                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
                record.setRate(String.format("%.2f", shopreslist.get(i).getRate().floatValue()));              
                mysqlDate = shopreslist.get(i).getResrtdate();
                
                if (mysqlDate != null) {
                    record.setResRateDate(formatter.format(mysqlDate));
                } else {
                    record.setResRateDate("");
                }
                record.setStockPerRate(String.format("%.2f", shopreslist.get(i).getStockperrt()));
                recordList.add(record);
                record = new ShopResDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No ShopResource record is found for shop and resource id");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getResShopForPk.");
            return null;
        }
    }
    
    public int getNextIdForExpense(){
        ExpenseDAO expensedao = new ExpenseDAO(utx, emf);
        try {
            return expensedao.getMaxExpId();
        }
        catch (NoResultException e) {
            System.out.println("No records in expense table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getNextIdForExpense().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public int addExpenseRecord(ExpenseDTO exrec) {
        ExpenseDAO expdao = new ExpenseDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Expense rec = new Expense();
            rec.setExpenseid(Integer.valueOf(exrec.getExpenseId()));
            mysqlDate = formatter.parse(exrec.getDate());
            rec.setDate(mysqlDate);
            rec.setExpensetype(exrec.getExpenseType());
            rec.setExpenserefid(Integer.valueOf(exrec.getExpenseRefId()));
            rec.setExpediture(BigDecimal.valueOf(Double.parseDouble(exrec.getExpenditure())));
            rec.setComments(exrec.getCommString());            
            expdao.create(rec);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this Expense record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addExpenseRecord(ExpenseDTO exrec).");
            return DB_SEVERE;
        }
    }
    
    public int editExpenseRecord(ExpenseDTO exrec) {
        ExpenseDAO expdao = new ExpenseDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Expense rec = new Expense();
            rec.setExpenseid(Integer.valueOf(exrec.getExpenseId()));
            mysqlDate = formatter.parse(exrec.getDate());
            rec.setDate(mysqlDate);
            rec.setExpensetype(exrec.getExpenseType());
            rec.setExpenserefid(Integer.valueOf(exrec.getExpenseRefId()));
            rec.setExpediture(BigDecimal.valueOf(Double.parseDouble(exrec.getExpenditure())));
            rec.setComments(exrec.getCommString());
            expdao.edit(rec);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("This labourcrop record does not exist.");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editExpenseRecord.");
            return DB_SEVERE;
        }
    }
    
    public ExpenseDTO getLabExpenseForHrvst(String labappid, String expensecat) {
        ExpenseDAO expdao = new ExpenseDAO(utx, emf); 
        ExpenseDTO retexpdto = new ExpenseDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Expense rec = expdao.getExpRecForLabHarvest(Integer.parseInt(labappid), expensecat);
            
            retexpdto.setExpenseId(rec.getExpenseid().toString());
            mysqlDate = rec.getDate();
            retexpdto.setDate(formatter.format(mysqlDate));
            retexpdto.setExpenseType(rec.getExpensetype());            
            retexpdto.setExpenseRefId(rec.getExpenserefid().toString());
            retexpdto.setExpenditure(String.format("%.2f", rec.getExpediture()));
            retexpdto.setCommString(rec.getComments());
            
            return retexpdto;
        }
        catch (NoResultException e) {
            System.out.println("No Expense record found for this labor.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getLabExpenseForHrvst().");
            return null;
        }
    }
    
    public List<ExpenseDTO> getAllEmpExpenseInRange(Date sdate, Date edate) {
        ExpenseDAO expdao = new ExpenseDAO(utx, emf); 
        List<ExpenseDTO> recordlist = new ArrayList<>();
        ExpenseDTO retexpdto = new ExpenseDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Expense> reclist = expdao.getEmpExpenses(sdate, edate);
            for (int i = 0; i < reclist.size(); i++) {
                retexpdto.setExpenseId(reclist.get(i).getExpenseid().toString());
                mysqlDate = reclist.get(i).getDate();
                retexpdto.setDate(formatter.format(mysqlDate));
                retexpdto.setExpenseType(reclist.get(i).getExpensetype());
                //Here the emp id is the ref id. we are populating the employee name in this field for this method
                retexpdto.setExpenseRefId(getEmpNameForId(
                        reclist.get(i).getExpenserefid().toString()).getName());
                retexpdto.setExpenditure(String.format("%.2f", reclist.get(i).getExpediture()));
                retexpdto.setCommString(reclist.get(i).getComments());
                recordlist.add(retexpdto);
                retexpdto = new ExpenseDTO();
            }
            return recordlist;
        }
        catch (NoResultException e) {
            System.out.println("No Expense record found for this employees and date range.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getAllEmpExpenseInRange().");
            return null;
        }
    }
    
    public int delExpenseRecord(ExpenseDTO exrec) {
        ExpenseDAO expdao = new ExpenseDAO(utx, emf);                 
        try {
            Expense rec = new Expense();
            rec.setExpenseid(Integer.valueOf(exrec.getExpenseId())); 
            expdao.destroy(rec.getExpenseid());
            return SUCCESS;
        }
        catch (NonexistentEntityException e) {
            System.out.println("Record for this expense record does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in delExpenseRecord(ExpenseDTO exrec).");
            return DB_SEVERE;
        }
    }
    
    public CropDTO getCropPerPk(String cropid) {
        CropDAO cropdao = new CropDAO(utx, emf);
        CropDTO record = new CropDTO();

        try {
            Crop croprec = cropdao.getCropPerPK(Integer.parseInt(cropid));
            record.setCropCategory(croprec.getCropcategory());
            record.setCropName(croprec.getCrop());
            record.setDetails(croprec.getDetails());
            return record;
        } catch (NoResultException e) {
            System.out.println("No crop record is found for this cropid.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getCropPerPk(String cropid).");
            return null;
        }
    }
    
    public List<CropDTO> getCropList() {
        CropDAO cropdao = new CropDAO(utx, emf);  
        List<CropDTO> recordList = new ArrayList<>();
        CropDTO record = new CropDTO();        
        try {  
            List<Crop> croplist = cropdao.getCropListAll();
            for (int i = 0; i < croplist.size(); i++) {
                record.setCropId(croplist.get(i).getCropid().toString());
                record.setCropCategory(croplist.get(i).getCropcategory());
                record.setCropName(croplist.get(i).getCrop());
                record.setDetails(croplist.get(i).getDetails());
                             
                recordList.add(record);
                record = new CropDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No crops are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getCropList.");
            return null;
        }
    }
    
    public List<CropDTO> getCropListForSite(String siteid) {
        CropDAO cropdao = new CropDAO(utx, emf);  
        List<CropDTO> recordList = new ArrayList<>();
        CropDTO record = new CropDTO();        
        try {  
            List<Crop> croplist = cropdao.getCropsPerSite(Integer.parseInt(siteid));
            for (int i = 0; i < croplist.size(); i++) {
                record.setCropId(croplist.get(i).getCropid().toString());
                record.setCropCategory(croplist.get(i).getCropcategory());
                record.setCropName(croplist.get(i).getCrop());
                record.setDetails(croplist.get(i).getDetails());
                             
                recordList.add(record);
                record = new CropDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No crops for the siteid are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getCropListForSite.");
            return null;
        }
    }
    
    public int getMaxIdForResCrop(){
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        try {
            return rescropdao.getMaxResCropId();
        }
        catch (NoResultException e) {
            System.out.println("No records in resourcecrop table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxIdForResCrop().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public ResourceCropDTO getResCropForId(String appliedid) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);       
        ResourceCropDTO record = new ResourceCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Resourcecrop rec = rescropdao.getResCropHarvestId(Integer.parseInt(appliedid));

            record.setApplicationId(rec.getApplicationid().toString());
            record.setHarvestId(Integer.toString(rec.getHarvestid()));
            record.setResourceId(Integer.toString(rec.getResourceid()));
            record.setResourceName(getResourceNameForId(rec.getResourceid())
                    .getResourceName());
            mysqlDate = rec.getAppldate();
            record.setApplicationDt(formatter.format(mysqlDate));
            record.setAppliedAmount(String.format("%.2f", rec.getAppliedamt().floatValue()));
            record.setAppliedAmtCost(String.format("%.2f", rec.getAppamtcost().floatValue()));
            record.setResUnit(getResourceNameForId(rec.getResourceid()).getUnit());
            return record;
        } catch (NoResultException e) {
            System.out.println("No resourcecrop record is found for this application Id.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getResCropForId(String appliedid).");
            return null;
        }
    }
    
    
    
    public List<ResourceCropDTO> getResCropForHarvest(String harvestid) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        List<ResourceCropDTO> recordlist = new ArrayList<>();
        ResourceCropDTO record = new ResourceCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Resourcecrop> reclist = rescropdao.getResCropHarvest(Integer.parseInt(harvestid));
            
            for (int i = 0; i < reclist.size(); i++) {
                record.setApplicationId(reclist.get(i).getApplicationid().toString());
                record.setHarvestId(Integer.toString(reclist.get(i).getHarvestid()));
                record.setResourceId(Integer.toString(reclist.get(i).getResourceid()));
                record.setResourceName(getResourceNameForId(reclist.get(i).getResourceid())
                        .getResourceName());
                mysqlDate = reclist.get(i).getAppldate();
                record.setApplicationDt(formatter.format(mysqlDate));
                record.setAppliedAmount(String.format("%.2f", reclist.get(i).getAppliedamt().floatValue()));
                record.setAppliedAmtCost(String.format("%.2f", reclist.get(i).getAppamtcost().floatValue()));
                record.setResUnit(getResourceNameForId(reclist.get(i).getResourceid()).getUnit());
                recordlist.add(record);
                record = new ResourceCropDTO();
            }  
            return recordlist;
        } catch (NoResultException e) {
            System.out.println("No resourcecrop record is found for this harvest.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getResCropForHarvest(String harvestid).");
            return null;
        }
    }
    
    public List<ResourceCropDTO> getResCropForResource(String resid) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        List<ResourceCropDTO> recordlist = new ArrayList<>();
        ResourceCropDTO record = new ResourceCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Resourcecrop> reclist = rescropdao.getResCropResource(Integer.parseInt(resid));
            
            for (int i = 0; i < reclist.size(); i++) {
                record.setApplicationId(reclist.get(i).getApplicationid().toString());
                record.setHarvestId(Integer.toString(reclist.get(i).getHarvestid()));
                record.setResourceId(Integer.toString(reclist.get(i).getResourceid()));
                record.setResourceName(getResourceNameForId(reclist.get(i).getResourceid())
                        .getResourceName());
                mysqlDate = reclist.get(i).getAppldate();
                record.setApplicationDt(formatter.format(mysqlDate));
                record.setAppliedAmount(String.format("%.2f", reclist.get(i).getAppliedamt().floatValue()));
                record.setAppliedAmtCost(String.format("%.2f", reclist.get(i).getAppamtcost().floatValue()));
                record.setResUnit(getResourceNameForId(reclist.get(i).getResourceid()).getUnit());
                recordlist.add(record);
                record = new ResourceCropDTO();
            }  
            return recordlist;
        } catch (NoResultException e) {
            System.out.println("No resourcecrop record is found for this resource.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getResCropForHarvest(String harvestid).");
            return null;
        }
    }
    public int addResCropRecord(ResourceCropDTO rescroprec) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Resourcecrop rec = new Resourcecrop();
            rec.setApplicationid(Integer.valueOf(rescroprec.getApplicationId()));
            rec.setHarvestid(Integer.parseInt(rescroprec.getHarvestId()));
            rec.setResourceid(Integer.parseInt(rescroprec.getResourceId()));
            mysqlDate = formatter.parse(rescroprec.getApplicationDt());
            rec.setAppldate(mysqlDate);
            rec.setAppliedamt(BigDecimal.valueOf(Double.parseDouble(rescroprec.getAppliedAmount())));
            rec.setAppamtcost(BigDecimal.valueOf(Double.parseDouble(rescroprec.getAppliedAmtCost())));
            rescropdao.create(rec);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this resourcecrop record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addResCropRecord(ResourceCropDTO rescroprec).");
            return DB_SEVERE;
        }
    }
    
    public int editResCropRecord(ResourceCropDTO rescroprec) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Resourcecrop rec = new Resourcecrop();
            rec.setApplicationid(Integer.valueOf(rescroprec.getApplicationId()));
            rec.setHarvestid(Integer.parseInt(rescroprec.getHarvestId()));
            rec.setResourceid(Integer.parseInt(rescroprec.getResourceId()));
            mysqlDate = formatter.parse(rescroprec.getApplicationDt());
            rec.setAppldate(mysqlDate);
            rec.setAppliedamt(BigDecimal.valueOf(Double.parseDouble(rescroprec.getAppliedAmount())));
            rec.setAppamtcost(BigDecimal.valueOf(Double.parseDouble(rescroprec.getAppliedAmtCost())));
            rescropdao.edit(rec);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("This resourcecrop record does not exist.");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in aeditResCropRecord(ResourceCropDTO rescroprec).");
            return DB_SEVERE;
        }
    }
    
    public int delResCropRecord(ResourceCropDTO rescroprec) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);                 
        try {
            Resourcecrop rec = new Resourcecrop();
            rec.setApplicationid(Integer.valueOf(rescroprec.getApplicationId())); 
            rescropdao.destroy(rec.getApplicationid());
            return SUCCESS;
        }
        catch (NonexistentEntityException e) {
            System.out.println("This resourcecrop record does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " delResCropRecord(ResourceCropDTO rescroprec).");
            return DB_SEVERE;
        }
    }
    
    public int getMaxIdForShopResCrop(){
        ShopResCropDAO shoprescroprec = new ShopResCropDAO(utx, emf);
        try {
            return shoprescroprec.getMaxId();
        }
        catch (NoResultException e) {
            System.out.println("No records in shoprescrop table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxIdForShopResCrop().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public int addShopResCrop(ShopResCropDTO shoprescroprec) {
        ShopResCropDAO recorddao = new ShopResCropDAO(utx, emf);
        
        try {
            Shoprescrop rec = new Shoprescrop();
            rec.setId(Integer.valueOf(shoprescroprec.getId()));
            rec.setRecropid(Integer.parseInt(shoprescroprec.getResCropId()));
            rec.setShopresid(Integer.parseInt(shoprescroprec.getShopResId()));
            rec.setAppliedamt(BigDecimal.valueOf(Double.parseDouble(shoprescroprec.getAmtApplied())));
            recorddao.create(rec);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this shoprescrop record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addShopResCrop.");
            return DB_SEVERE;
        }
    }
    
    public int editShopResCrop(ShopResCropDTO shoprescroprec) {
        ShopResCropDAO recorddao = new ShopResCropDAO(utx, emf);
        
        try {
            Shoprescrop rec = new Shoprescrop();
            rec.setId(Integer.valueOf(shoprescroprec.getId()));
            rec.setRecropid(Integer.parseInt(shoprescroprec.getResCropId()));
            rec.setShopresid(Integer.parseInt(shoprescroprec.getShopResId()));
            rec.setAppliedamt(BigDecimal.valueOf(Double.parseDouble(shoprescroprec.getAmtApplied())));
            recorddao.edit(rec);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("No shoprescrop record is found for this rescropid");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editShopResCrop.");
            return DB_SEVERE;
        }
    }
    
    public int deleteShopResCrop(ShopResCropDTO shoprescroprec) {
        ShopResCropDAO recorddao = new ShopResCropDAO(utx, emf);        
        try {
            Shoprescrop rec = new Shoprescrop();
            rec.setId(Integer.valueOf(shoprescroprec.getId()));
            
            recorddao.destroy(rec.getId());
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("No shoprescrop record is found for this rescropid");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editShopResCrop.");
            return DB_SEVERE;
        }
    }
    
    public List<ShopResCropDTO> getShopResCropList(String rescropid) {
        ShopResCropDAO recorddao = new ShopResCropDAO(utx, emf);
        ShopResCropDTO record = new ShopResCropDTO();
        List<ShopResCropDTO> recordList = new ArrayList<>();
        try {
            List<Shoprescrop> resultlist = recorddao.
                    getShopResList(Integer.parseInt(rescropid));
            for (int i = 0; i < resultlist.size(); i++) {
                record.setId(resultlist.get(i).getId().toString());
                record.setResCropId(Integer.toString(resultlist.get(i).getRecropid()));
                record.setShopResId(Integer.toString(resultlist.get(i).getShopresid()));
                record.setAmtApplied(String.format("%.2f",resultlist.get(i).getAppliedamt()));
                recordList.add(record);
                record = new ShopResCropDTO();
            }
            return recordList;
        } catch (NoResultException e) {
            System.out.println("No shoprescrop records are found for resapplyid");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResCropList().");
            return null;
        }
    }
    
    public List<ShopResCropDTO> getShopResCropEmpty(String rescropid) {
        ShopResCropDAO recorddao = new ShopResCropDAO(utx, emf);
        ShopResCropDTO record = new ShopResCropDTO();
        List<ShopResCropDTO> recordList = new ArrayList<>();
        try {
            List<Shoprescrop> resultlist = recorddao.
                    shopResListEmpty(Integer.parseInt(rescropid));
            for (int i = 0; i < resultlist.size(); i++) {
                record.setId(resultlist.get(i).getId().toString());
                record.setResCropId(Integer.toString(resultlist.get(i).getRecropid()));
                record.setShopResId(Integer.toString(resultlist.get(i).getShopresid()));
                record.setAmtApplied(String.format("%.2f",resultlist.get(i).getAppliedamt()));
                recordList.add(record);
                record = new ShopResCropDTO();
            }
            return recordList;
        } catch (NoResultException e) {
            System.out.println("No shoprescrop records are found for resapplyid");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getShopResCropList().");
            return null;
        }
    }
    
    public ShopResCropDTO getRecForShopResCrop(String rescropid, String shopresid) {
        ShopResCropDAO recorddao = new ShopResCropDAO(utx, emf);
        ShopResCropDTO record = new ShopResCropDTO();        
        try {
            Shoprescrop result = recorddao.
                    getShopResCrop(Integer.parseInt(rescropid), Integer.parseInt(shopresid));
            record.setId(result.getId().toString());
            record.setResCropId(Integer.toString(result.getRecropid()));
            record.setShopResId(Integer.toString(result.getShopresid()));
            record.setAmtApplied(String.format("%.2f", result.getAppliedamt()));
            return record;
        } catch (NoResultException e) {
            System.out.println("No shoprescrop records are found for rescropid and shopresid");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getListForShopResCrop().");
            return null;
        }
    }
    
    public List<ResourceCropDTO> getResSummaryPerID() {
        //this is a group by one
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        List<ResourceCropDTO> recordlist = new ArrayList<>();
        ResourceCropDTO record = new ResourceCropDTO();
        
        try {
            List<ResCropSummaryDTO> recordsummary = rescropdao.getSumByResId();
            
            for (int i = 0; i < recordsummary.size(); i++) {
                record.setApplicationId(null);
                record.setHarvestId(Integer.toString(recordsummary.get(i).getHarvestId()));
                record.setResourceId(Integer.toString(recordsummary.get(i).getResourceId()));
                record.setResourceName(getResourceNameForId(recordsummary.get(i).getResourceId())
                        .getResourceName());
                record.setHarvestDto(getHarvestRecForId(Integer.toString(recordsummary.get(i).getHarvestId())));
                record.setApplicationDt(null);
                record.setAppliedAmount(String.format("%.2f", recordsummary.get(i).
                        getAppliedAmount().floatValue()));
                record.setResUnit(getResourceNameForId(recordsummary.get(i).getResourceId()).getUnit());
                recordlist.add(record);
                record = new ResourceCropDTO();
            }  
            return recordlist;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getResSummaryPerID().");
            return null;
        }
    }
    public List<ResourceCropDTO> getRescropDetails(String harvestid, Date sdate, Date edate) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        List<ResourceCropDTO> recordlist = new ArrayList<>();
        ResourceCropDTO record = new ResourceCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Resourcecrop> resultlist = rescropdao.getResCropDtls(Integer.parseInt(harvestid), 
                    sdate, edate);
            
            for (int i = 0; i < resultlist.size(); i++) {
                record.setApplicationId(Integer.toString(resultlist.get(i).getApplicationid()));
                record.setHarvestId(Integer.toString(resultlist.get(i).getHarvestid()));
                record.setResourceId(Integer.toString(resultlist.get(i).getResourceid()));
                record.setResourceName(getResourceNameForId(resultlist.get(i).getResourceid())
                        .getResourceName());
                record.setHarvestDto(getHarvestRecForId(Integer.toString(resultlist.get(i).getHarvestid())));
                mysqlDate = resultlist.get(i).getAppldate();
                record.setApplicationDt(formatter.format(mysqlDate));
                record.setAppliedAmount(String.format("%.2f", resultlist.get(i).
                        getAppliedamt().floatValue()));
                record.setAppliedAmtCost(String.format("%.2f", resultlist.get(i).
                        getAppamtcost().floatValue()));
                record.setResUnit(getResourceNameForId(resultlist.get(i).getResourceid()).getUnit());
                recordlist.add(record);
                record = new ResourceCropDTO();
            }  
            return recordlist;
           
        }catch (NoResultException e) {
            System.out.println("No Resourcecrop records are found for harvestid, start and end date");
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getRescropDetails().");
            return null;
        }
    }
    
    public List<ResourceCropDTO> getRescropDetailsForRes(String resid, Date sdate, Date edate) {
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        List<ResourceCropDTO> recordlist = new ArrayList<>();
        ResourceCropDTO record = new ResourceCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Resourcecrop> resultlist = rescropdao.getResCropDtlsResid(Integer.parseInt(resid), 
                    sdate, edate);
            
            for (int i = 0; i < resultlist.size(); i++) {
                record.setApplicationId(Integer.toString(resultlist.get(i).getApplicationid()));
                record.setHarvestId(Integer.toString(resultlist.get(i).getHarvestid()));
                record.setResourceId(Integer.toString(resultlist.get(i).getResourceid()));
                record.setResourceName(getResourceNameForId(resultlist.get(i).getResourceid())
                        .getResourceName());
                record.setHarvestDto(getHarvestRecForId(Integer.toString(resultlist.get(i).getHarvestid())));
                mysqlDate = resultlist.get(i).getAppldate();
                record.setApplicationDt(formatter.format(mysqlDate));
                record.setAppliedAmount(String.format("%.2f", resultlist.get(i).
                        getAppliedamt().floatValue()));
                record.setAppliedAmtCost(String.format("%.2f", resultlist.get(i).
                        getAppamtcost().floatValue()));
                record.setResUnit(getResourceNameForId(resultlist.get(i).getResourceid()).getUnit());
                recordlist.add(record);
                record = new ResourceCropDTO();
            }  
            return recordlist;
           
        }catch (NoResultException e) {
            System.out.println("No Resourcecrop records are found for resid, start and end date");
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getRescropDetailsForRes().");
            return null;
        }
    }
    
    public List<ResourceCropDTO> getSummaryPerResForHrvst(Date sdate, Date edate, String harvestid) {
        //this is a group by one
        ResourceCropDAO rescropdao = new ResourceCropDAO(utx, emf);
        List<ResourceCropDTO> recordlist = new ArrayList<>();
        ResourceCropDTO record = new ResourceCropDTO();
        
        try {
            List<ResCropAllSummaryDTO> recordsummary = rescropdao.getSumForHarvest(sdate, 
                    edate, Integer.parseInt(harvestid));
            
            for (int i = 0; i < recordsummary.size(); i++) {
                record.setApplicationId(null);
                record.setHarvestId(harvestid);
                record.setResourceId(Integer.toString(recordsummary.get(i).getResourceId()));
                record.setResourceName(getResourceNameForId(recordsummary.get(i).getResourceId())
                        .getResourceName());
                record.setHarvestDto(getHarvestRecForId(harvestid));
                record.setApplicationDt(null);
                record.setAppliedAmount(String.format("%.2f", recordsummary.get(i).
                        getAppliedAmount().floatValue()));
                record.setAppliedAmtCost(String.format("%.2f", recordsummary.get(i).
                        getAppliedCost().floatValue()));
                record.setResUnit(getResourceNameForId(recordsummary.get(i).getResourceId())
                        .getUnit());
                recordlist.add(record);
                record = new ResourceCropDTO();
            }  
            return recordlist;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getSummaryPerResForHrvst.");
            return null;
        }
    }
    
    public int getMaxIdForLabCrop(){
        LabourCropDAO labourcropdao = new LabourCropDAO(utx, emf);
        try {
            return labourcropdao.getMaxLabCropId();
        }
        catch (NoResultException e) {
            System.out.println("No records");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxIdForLabCrop().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    public int editLabCropRecord(LabourCropDTO labcroprec) {
        LabourCropDAO rescropdao = new LabourCropDAO(utx, emf); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Labourcrop rec = new Labourcrop();
            rec.setApplicationid(Integer.valueOf(labcroprec.getApplicationId()));
            rec.setHarvestid(Integer.parseInt(labcroprec.getHarvestId()));
            
            mysqlDate = formatter.parse(labcroprec.getApplicationDate());
            rec.setAppldate(mysqlDate);            
            rescropdao.edit(rec);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("This labourcrop record does not exist.");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editLabCropRecord.");
            return DB_SEVERE;
        }
    }
    public int addLabourCropRecord(LabourCropDTO labourcroprec) {
        LabourCropDAO labourcropdao = new LabourCropDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Labourcrop rec = new Labourcrop();
            
            rec.setApplicationid(Integer.valueOf(labourcroprec.getApplicationId()));
            rec.setHarvestid(Integer.parseInt(labourcroprec.getHarvestId()));            
            mysqlDate = formatter.parse(labourcroprec.getApplicationDate());
            rec.setAppldate(mysqlDate);
            labourcropdao.create(rec);
            return SUCCESS;
        }
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this labourcrop record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addLabourCropRecord(LabourCropDTO labourcroprec).");
            return DB_SEVERE;
        }
    }
    
    public int delLabourCropRecord(LabourCropDTO labourcroprec) {
        LabourCropDAO labourcropdao = new LabourCropDAO(utx, emf);               
        try {
            Labourcrop rec = new Labourcrop();
            rec.setApplicationid(Integer.valueOf(labourcroprec.getApplicationId())); 
            labourcropdao.destroy(rec.getApplicationid());
            return SUCCESS;
        }
        catch (NonexistentEntityException e) {
            System.out.println("Record for this labourcrop does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in delLabourCropRecord(LabourCropDTO labourcroprec).");
            return DB_SEVERE;
        }
    }
    
    public List<LabourCropDTO> getLabCropForHarvest(String harvestid) {
        LabourCropDAO labcropdao = new LabourCropDAO(utx, emf);
        List<LabourCropDTO> recordlist = new ArrayList<>();
        LabourCropDTO record = new LabourCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Labourcrop> reclist = labcropdao.getLabCropHarvest(Integer.parseInt(harvestid));
            
            for (int i = 0; i < reclist.size(); i++) {
                record.setApplicationId(reclist.get(i).getApplicationid().toString());
                record.setHarvestId(Integer.toString(reclist.get(i).getHarvestid()));                
                mysqlDate = reclist.get(i).getAppldate();
                record.setApplicationDate(formatter.format(mysqlDate));
                String labourCategory = "LABHRVST";
                record.setAppliedAmount(getLabExpenseForHrvst(record.getApplicationId()
                        , labourCategory).getExpenditure());  
                record.setExpenseComments(getLabExpenseForHrvst(record.getApplicationId()
                        , labourCategory).getCommString());
                recordlist.add(record);
                record = new LabourCropDTO();
            }  
            return recordlist;
        } catch (NoResultException e) {
            System.out.println("No labourcrop record is found for this harvest.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getResCropForHarvest(String harvestid).");
            return null;
        }
    }
    
    public LabourCropDTO getTotalLabcropReport(String harvestid, Date sdate, Date edate) {
        LabourCropDAO labcropdao = new LabourCropDAO(utx, emf);
//        List<LabourCropDTO> recordlist = new ArrayList<>();
        LabourCropDTO record = new LabourCropDTO();
//        Date mysqlDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Labourcrop> reclist = labcropdao.getForHrstDtRange(Integer.parseInt(harvestid), sdate, edate);
            float appliedamt = 0;
//            int ii = reclist.size()-1;
            for (int i = 0; i < reclist.size(); i++) {
//                record.setApplicationId(reclist.get(i).getApplicationid().toString());
//                record.setHarvestId(harvestid);                
//                mysqlDate = reclist.get(i).getAppldate();
//                record.setApplicationDate(formatter.format(mysqlDate));
                String labourCategory = "LABHRVST";
                appliedamt = appliedamt + Float.parseFloat(getLabExpenseForHrvst(reclist.get(i).getApplicationid().toString()
                        , labourCategory).getExpenditure());
//                record.setAppliedAmount(getLabExpenseForHrvst(record.getApplicationId()
//                        , labourCategory).getExpenditure());  
//                record.setExpenseComments(getLabExpenseForHrvst(record.getApplicationId()
//                        , labourCategory).getCommString());
//                recordlist.add(record);
//                record = new LabourCropDTO();
            }  
            record.setApplicationId("0");
            record.setHarvestId(harvestid);
            record.setApplicationDate(null);
            record.setAppliedAmount(String.format("%.2f", appliedamt));

            return record;
        } catch (NoResultException e) {
            System.out.println("No labourcrop record is found for this harvest and applied date.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getTotalLabcropReport.");
            return null;
        }
    }
    public List<LabourCropDTO> getLabcropDetails(String harvestid, Date sdate, Date edate) {
        LabourCropDAO labcropdao = new LabourCropDAO(utx, emf);
        List<LabourCropDTO> recordlist = new ArrayList<>();       
        LabourCropDTO record = new LabourCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            List<Labourcrop> reclist = labcropdao.getForHrstDtRange(Integer.parseInt(harvestid), 
                    sdate, edate);
            
            for (int i = 0; i < reclist.size(); i++) {
                record.setApplicationId(reclist.get(i).getApplicationid().toString());
                record.setHarvestId(harvestid);                
                mysqlDate = reclist.get(i).getAppldate();
                record.setApplicationDate(formatter.format(mysqlDate));
                String labourCategory = "LABHRVST";                
                record.setAppliedAmount(getLabExpenseForHrvst(record.getApplicationId()
                        , labourCategory).getExpenditure());  
                record.setExpenseComments(getLabExpenseForHrvst(record.getApplicationId()
                        , labourCategory).getCommString());
                recordlist.add(record);
                record = new LabourCropDTO();
            }  
            return recordlist;
           
        }catch (NoResultException e) {
            System.out.println("No labourcrop record is found for this harvest and applied date.");
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getLabcropDetails().");
            return null;
        }
    }
    public LabourCropDTO getLabCropForId(String appliedid) {
        LabourCropDAO labcropdao = new LabourCropDAO(utx, emf);       
        LabourCropDTO record = new LabourCropDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Labourcrop rec = labcropdao.getLabCropHarvestId(Integer.parseInt(appliedid));

            record.setApplicationId(rec.getApplicationid().toString());
            record.setHarvestId(Integer.toString(rec.getHarvestid()));            
            mysqlDate = rec.getAppldate();
            record.setApplicationDate(formatter.format(mysqlDate));
            String labourCategory = "LABHRVST";
            record.setAppliedAmount(getLabExpenseForHrvst(appliedid, labourCategory).getExpenditure());            
            record.setExpenseComments(getLabExpenseForHrvst(appliedid, labourCategory).getCommString());
            return record;
        } catch (NoResultException e) {
            System.out.println("No laborcrop record is found for this application Id.");
            return null;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in getLabCropForId.");
            return null;
        }
    }
    
    public int getMaxEmployeeId(){
        EmployeeDAO empdao = new EmployeeDAO(utx, emf);
        try {
            return empdao.getMaxEmpId();
        }
        catch (NoResultException e) {
            System.out.println("No records in employee table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxEmployeeId().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public int addEmployeeRecord(EmployeeDTO employeerec) {
        EmployeeDAO empdao = new EmployeeDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Employee rec = new Employee();
            rec.setId(Integer.valueOf(employeerec.getId()));
            rec.setName(employeerec.getName());
            rec.setAddress(employeerec.getAddress());
            rec.setContact(employeerec.getPhno());
            rec.setSalary(BigDecimal.valueOf(Double.parseDouble(employeerec.getSalary())));

            mysqlDate = formatter.parse(employeerec.getSdate());
            rec.setStartdate(mysqlDate);
            
            if (employeerec.getEdate() != null) {
                rec.setEnddate(formatter.parse(employeerec.getEdate()));
            } else {
                rec.setEnddate(null);
            }
            empdao.create(rec);
            return SUCCESS;
        } catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this employee record");
            return DB_DUPLICATE;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in addEmployeeRecord.");
            return DB_SEVERE;
        }
    }
    
    public int editEmployeeRecord(EmployeeDTO employeerec) {
        EmployeeDAO empdao = new EmployeeDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Employee rec = new Employee();
            rec.setId(Integer.valueOf(employeerec.getId()));
            rec.setName(employeerec.getName());
            rec.setAddress(employeerec.getAddress());
            rec.setContact(employeerec.getPhno());
            rec.setSalary(BigDecimal.valueOf(Double.parseDouble(employeerec.getSalary())));

            mysqlDate = formatter.parse(employeerec.getSdate());
            rec.setStartdate(mysqlDate);
            
            if (employeerec.getEdate() != null) {
                rec.setEnddate(formatter.parse(employeerec.getEdate()));
            } else {
                rec.setEnddate(null);
            }
            empdao.edit(rec);
            return SUCCESS;
        } catch (NoResultException e) {
            System.out.println("This employee record to be edited, does not exist ");
            return DB_NON_EXISTING;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in editEmployeeRecord.");
            return DB_SEVERE;
        }
    }
    
    public List<EmployeeDTO> getActiveEmployeeList() {
        EmployeeDAO empdao = new EmployeeDAO(utx, emf);  
        List<EmployeeDTO> recordList = new ArrayList<>();
        EmployeeDTO record = new EmployeeDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Employee> emplist = empdao.getActiveList();
            for (int i = 0; i < emplist.size(); i++) {
                record.setId(String.valueOf(emplist.get(i).getId()));
                record.setName(emplist.get(i).getName());
                record.setAddress(emplist.get(i).getAddress());
                record.setPhno(emplist.get(i).getContact());               
                record.setSalary(String.format("%.2f", emplist.get(i).getSalary()));
                mysqlDate = emplist.get(i).getStartdate();                    
                record.setSdate(formatter.format(mysqlDate));
                record.setEdate(null);
                recordList.add(record);
                record = new EmployeeDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No employee are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getActiveEmployeeList.");
            return null;
        }
    }
    
    public EmployeeDTO getEmpNameForId(String empid) {
        EmployeeDAO empdao = new EmployeeDAO(utx, emf);        
        EmployeeDTO record = new EmployeeDTO(); 
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            Employee emprec = empdao.getEmpName(Integer.parseInt(empid));
            record.setId(empid);
            record.setName(emprec.getName());

            record.setAddress(emprec.getAddress());
            record.setPhno(emprec.getContact());
            record.setSalary(String.format("%.2f", emprec.getSalary()));
            mysqlDate = emprec.getStartdate();
            record.setSdate(formatter.format(mysqlDate));
            if(emprec.getEnddate()==null){
                record.setEdate(null);
            } else {
                mysqlDate = emprec.getEnddate();
                record.setEdate(formatter.format(mysqlDate));
            }
                
            return record;
        }
        catch (NoResultException e) {
            System.out.println("No resource found for this resourceid");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getResourceNameForId(int resourceid).");
            return null;
        }
    }
    
    public List<EmpExpDTO> getEmpActiveExpRecs(String empid, String expcat) {
        EmpexpenseDAO empexpdao = new EmpexpenseDAO(utx, emf);  
        List<EmpExpDTO> recordList = new ArrayList<>();
        EmpExpDTO record = new EmpExpDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Empexpense> reclist = empexpdao.getExpenseList(Integer.parseInt(empid), expcat);
            for (int i = 0; i < reclist.size(); i++) {
                record.setId(String.valueOf(reclist.get(i).getId()));
                record.setEmpid(empid);
                record.setExpcategory(reclist.get(i).getExpcategory());
                record.setTotal(String.format("%.2f", reclist.get(i).getTotalloan()));
                record.setOutstanding(String.format("%.2f", reclist.get(i).getOutstanding()));
                record.setEmprefid(String.valueOf(reclist.get(i).getExprefid()));
                if(reclist.get(i).getStartdate()==null){
                   record.setSdate(null);
                } else {
                   mysqlDate = reclist.get(i).getStartdate();
                   record.setSdate(formatter.format(mysqlDate));
                }
                record.setEdate(null);
                recordList.add(record);
                record = new EmpExpDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No employee expense records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getEmployeeExpRecs.");
            return null;
        }
    }
    
    
    
    
    public List<EmpExpDTO> getEmpActiveLoans() {
        EmpexpenseDAO empexpdao = new EmpexpenseDAO(utx, emf);  
        List<EmpExpDTO> recordList = new ArrayList<>();
        EmpExpDTO record = new EmpExpDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Empexpense> reclist = empexpdao.getLoanList();
            for (int i = 0; i < reclist.size(); i++) {
                record.setId(String.valueOf(reclist.get(i).getId()));
                record.setEmpid(String.valueOf(reclist.get(i).getEmployeeid()));
                record.setExpcategory(reclist.get(i).getExpcategory());
                record.setTotal(String.format("%.2f", reclist.get(i).getTotalloan()));
                record.setOutstanding(String.format("%.2f", reclist.get(i).getOutstanding()));
                record.setEmprefid(String.valueOf(reclist.get(i).getExprefid()));
                if(reclist.get(i).getStartdate()==null){
                   record.setSdate(null);
                } else {
                   mysqlDate = reclist.get(i).getStartdate();
                   record.setSdate(formatter.format(mysqlDate));
                }
                record.setEdate(null);
                recordList.add(record);
                record = new EmpExpDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No employee loan records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getEmpActiveLoans.");
            return null;
        }
    }
    
    public List<EmpExpDTO> getPaybackList(String empid, String loanrefid) {
        EmpexpenseDAO empexpdao = new EmpexpenseDAO(utx, emf);  
        List<EmpExpDTO> recordList = new ArrayList<>();
        EmpExpDTO record = new EmpExpDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Empexpense> reclist = empexpdao.getPaybkLst(Integer.parseInt(empid), Integer.parseInt(loanrefid));
            for (int i = 0; i < reclist.size(); i++) {
                record.setId(String.valueOf(reclist.get(i).getId()));                
                record.setEmpid(String.valueOf(reclist.get(i).getEmployeeid()));
                record.setExpcategory(reclist.get(i).getExpcategory());
                record.setTotal(String.format("%.2f", reclist.get(i).getTotalloan()));
                record.setOutstanding(String.format("%.2f", reclist.get(i).getOutstanding()));
                record.setEmprefid(String.valueOf(reclist.get(i).getExprefid()));
                if(reclist.get(i).getStartdate()==null){
                   record.setSdate(null);
                } else {
                   mysqlDate = reclist.get(i).getStartdate();
                   record.setSdate(formatter.format(mysqlDate));
                }
                record.setEdate(null);
                recordList.add(record);
                record = new EmpExpDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No employee payback records are found");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getPaybackList.");
            return null;
        }
    }
    
    public int getMaxEmpExpenseId(){
        EmpexpenseDAO empexpdao = new EmpexpenseDAO(utx, emf);
        try {
            return empexpdao.getMaxId();
        }
        catch (NoResultException e) {
            System.out.println("No records in empexpense table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxEmpExpenseId().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    
    public int addEmpExpRecord(EmpExpDTO empexprec) {
        EmpexpenseDAO empexpdao = new EmpexpenseDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Empexpense rec = new Empexpense();
            rec.setId(Integer.valueOf(empexprec.getId()));
            rec.setEmployeeid(Integer.parseInt(empexprec.getEmpid()));
            rec.setTotalloan(BigDecimal.valueOf(Double.parseDouble(empexprec.getTotal())));
            rec.setOutstanding(BigDecimal.valueOf(Double.parseDouble(empexprec.getOutstanding())));
            rec.setExpcategory(empexprec.getExpcategory());
            if(empexprec.getExpcategory().equals("LOAN"))
                rec.setExprefid(null);
            else
                rec.setExprefid(Integer.valueOf(empexprec.getEmprefid()));
            mysqlDate = formatter.parse(empexprec.getSdate());
            rec.setStartdate(mysqlDate);
            
            if (empexprec.getEdate()!= null) {
                rec.setEnddate(formatter.parse(empexprec.getEdate()));
            } else {
                rec.setEnddate(null);
            }
            empexpdao.create(rec);
            return SUCCESS;
        } catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this empexpense record");
            return DB_DUPLICATE;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in addEmpExpRecord.");
            return DB_SEVERE;
        }
    }
    public int editEmpExpRecord(EmpExpDTO empexprec) {
        EmpexpenseDAO empexpdao = new EmpexpenseDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Empexpense rec = new Empexpense();
            
            rec.setId(Integer.valueOf(empexprec.getId()));
            rec.setEmployeeid(Integer.parseInt(empexprec.getEmpid()));
            rec.setTotalloan(BigDecimal.valueOf(Double.parseDouble(empexprec.getTotal())));
            rec.setOutstanding(BigDecimal.valueOf(Double.parseDouble(empexprec.getOutstanding())));
            rec.setExpcategory(empexprec.getExpcategory());
            
            mysqlDate = formatter.parse(empexprec.getSdate());
            rec.setStartdate(mysqlDate);
            
            if (empexprec.getEdate()!= null) {
                rec.setEnddate(formatter.parse(empexprec.getEdate()));
            } else rec.setEnddate(null);            
            empexpdao.edit(rec);
            return SUCCESS;
        }
        catch (NoResultException e) {
            System.out.println("This empexpense record does not exist.");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in editEmpExpRecord.");
            return DB_SEVERE;
        }
    }
    
    public int getMaxTaskplanId(){
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);
        try {
            return taskplandao.getMaxId();
        }
        catch (NoResultException e) {
            System.out.println("No records in resourcecrop table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxTaskplanId().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    public List<TaskPlanDTO> getTaskPlanListForDate(Date plandate) {
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);  
        List<TaskPlanDTO> recordList = new ArrayList<>();
        TaskPlanDTO record = new TaskPlanDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Taskplan> taskplanlist = taskplandao.getListForDate(plandate);
            for (int i = 0; i < taskplanlist.size(); i++) {
                record.setTaskId(taskplanlist.get(i).getId().toString());
                record.setTaskType(taskplanlist.get(i).getTasktype());
                mysqlDate = taskplanlist.get(i).getTaskdate();                    
                record.setTaskDt(formatter.format(mysqlDate));
                record.setTaskName(taskplanlist.get(i).getTaskname());
                record.setHarvestId(String.valueOf(taskplanlist.get(i).getHasvestid()));
                record.setHarvestDto(getHarvestRecForId(String.valueOf(taskplanlist.get(i).getHasvestid())));
                if (taskplanlist.get(i).getResourceid() == null)
                    record.setResourceId(null);
                else
                    record.setResourceId(String.valueOf(taskplanlist.get(i).getResourceid()));
                
                if (taskplanlist.get(i).getAppamtcost() == null)
                    record.setAppliedAmtCost(null);
                else
                    record.setAppliedAmtCost(String.format("%.2f", taskplanlist.get(i).getAppamtcost()));
                
                if (taskplanlist.get(i).getAppliedamt() == null)
                    record.setAppliedAmount(null);
                else
                    record.setAppliedAmount(String.format("%.2f", taskplanlist.get(i).getAppliedamt()));
                record.setAppliedFlag(taskplanlist.get(i).getAppliedflag());
                record.setComments(taskplanlist.get(i).getComments());
                recordList.add(record);
                record = new TaskPlanDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No task is planned for this date.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getTaskPlanListForDate.");
            return null;
        }
    }
    
    public List<TaskPlanDTO> getAllTaskPlanList() {
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);  
        List<TaskPlanDTO> recordList = new ArrayList<>();
        TaskPlanDTO record = new TaskPlanDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Taskplan> taskplanlist = taskplandao.getAllData();
            for (int i = 0; i < taskplanlist.size(); i++) {
                record.setTaskId(taskplanlist.get(i).getId().toString());
                record.setTaskType(taskplanlist.get(i).getTasktype());
                mysqlDate = taskplanlist.get(i).getTaskdate();                    
                record.setTaskDt(formatter.format(mysqlDate));
                record.setTaskName(taskplanlist.get(i).getTaskname());
                record.setHarvestId(String.valueOf(taskplanlist.get(i).getHasvestid()));
                record.setHarvestDto(getHarvestRecForId(String.valueOf(taskplanlist.get(i).getHasvestid())));
                if (taskplanlist.get(i).getResourceid() == null)
                    record.setResourceId(null);
                else
                    record.setResourceId(String.valueOf(taskplanlist.get(i).getResourceid()));
                
                if (taskplanlist.get(i).getAppamtcost() == null)
                    record.setAppliedAmtCost(null);
                else
                    record.setAppliedAmtCost(String.format("%.2f", taskplanlist.get(i).getAppamtcost()));
                
                if (taskplanlist.get(i).getAppliedamt() == null)
                    record.setAppliedAmount(null);
                else
                    record.setAppliedAmount(String.format("%.2f", taskplanlist.get(i).getAppliedamt()));
                record.setAppliedFlag(taskplanlist.get(i).getAppliedflag());
                record.setComments(taskplanlist.get(i).getComments());
                recordList.add(record);
                record = new TaskPlanDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No task is planned so far.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getAllTaskPlanList.");
            return null;
        }
    }
    
    public List<TaskPlanDTO> getTaskdDetailsBetweenDates(Date startdate, Date enddate) {
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);  
        List<TaskPlanDTO> recordList = new ArrayList<>();
        TaskPlanDTO record = new TaskPlanDTO();
        Date mysqlDate;
        String pattern = "dd MMM yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {  
            List<Taskplan> taskplanlist = taskplandao.detailsBetweenDates(startdate, enddate);
            for (int i = 0; i < taskplanlist.size(); i++) {
                record.setTaskId(taskplanlist.get(i).getId().toString());
                record.setTaskType(taskplanlist.get(i).getTasktype());
                mysqlDate = taskplanlist.get(i).getTaskdate();                    
                record.setTaskDt(formatter.format(mysqlDate));
                record.setTaskName(taskplanlist.get(i).getTaskname());
                record.setHarvestId(String.valueOf(taskplanlist.get(i).getHasvestid()));
                record.setHarvestDto(getHarvestRecForId(String.valueOf(taskplanlist.get(i).getHasvestid())));
                if (taskplanlist.get(i).getResourceid() == null){
                    record.setResourceId(null);
                    record.setResourceName(null);
                }else{
                    record.setResourceId(String.valueOf(taskplanlist.get(i).getResourceid()));
                    record.setResourceName(getResourceNameForId(taskplanlist.get(i).getResourceid())
                            .getResourceName()+"(" +getResourceNameForId(taskplanlist.get(i).getResourceid())
                                    .getUnit()+")");
                }
                if (taskplanlist.get(i).getAppamtcost() == null)
                    record.setAppliedAmtCost(null);
                else
                    record.setAppliedAmtCost(String.format("%.2f", taskplanlist.get(i).getAppamtcost()));
                
                if (taskplanlist.get(i).getAppliedamt() == null)
                    record.setAppliedAmount(null);
                else
                    record.setAppliedAmount(String.format("%.2f", taskplanlist.get(i).getAppliedamt()));
                record.setAppliedFlag(taskplanlist.get(i).getAppliedflag());
                record.setComments(taskplanlist.get(i).getComments());
                recordList.add(record);
                record = new TaskPlanDTO();
            }        
            return recordList;
        }
        catch (NoResultException e) {
            System.out.println("No task is planned for this date range.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getTaskdDetailsBetweenDates.");
            return null;
        }
    }
    public int addTaskplanRecord(TaskPlanDTO taskrec) {        
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Taskplan record = new Taskplan();
            record.setId(Integer.valueOf(taskrec.getTaskId()));
            record.setTasktype(taskrec.getTaskType());
            record.setTaskname(taskrec.getTaskName());
            record.setHasvestid(Integer.parseInt(taskrec.getHarvestId()));
            if(taskrec.getResourceId()==null){
               record.setResourceid(null);
            } else record.setResourceid(Integer.parseInt(taskrec.getResourceId()));
            
            if(taskrec.getAppliedAmount()==null){
               record.setAppliedamt(null);
            } else record.setAppliedamt(BigDecimal.valueOf(Double.parseDouble(taskrec
                    .getAppliedAmount())));
             
            if(taskrec.getAppliedAmtCost()==null){
               record.setAppamtcost(null);
            } else record.setAppamtcost(BigDecimal.valueOf(Double.parseDouble(taskrec
                    .getAppliedAmtCost())));
            
            mysqlDate = formatter.parse(taskrec.getTaskDt());
            record.setTaskdate(mysqlDate);  
            record.setAppliedflag(taskrec.getAppliedFlag());
            record.setComments(taskrec.getComments());
            taskplandao.create(record);
            return SUCCESS;
        } 
        catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this taskplan record");            
            return DB_DUPLICATE;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addTaskplanRecord.");
            return DB_SEVERE;
        }
    }
    public int editTaskplanRecord(TaskPlanDTO taskrec) {        
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Taskplan record = new Taskplan();
            record.setId(Integer.valueOf(taskrec.getTaskId()));
            record.setTasktype(taskrec.getTaskType());
            record.setTaskname(taskrec.getTaskName());
            record.setHasvestid(Integer.parseInt(taskrec.getHarvestId()));
            if(taskrec.getResourceId()==null){
               record.setResourceid(null);
            } else record.setResourceid(Integer.parseInt(taskrec.getResourceId()));
             if(taskrec.getAppliedAmount()==null){
               record.setAppliedamt(null);
            } else record.setAppliedamt(BigDecimal.valueOf(Double.parseDouble(taskrec
                    .getAppliedAmount())));
             
            if(taskrec.getAppliedAmtCost()==null){
               record.setAppamtcost(null);
            } else record.setAppamtcost(BigDecimal.valueOf(Double.parseDouble(taskrec
                    .getAppliedAmtCost())));
            
            mysqlDate = formatter.parse(taskrec.getTaskDt());
            record.setTaskdate(mysqlDate);    
            record.setAppliedflag(taskrec.getAppliedFlag());
            record.setComments(taskrec.getComments());
            taskplandao.edit(record);
            return SUCCESS;
        } 
        catch (NoResultException e) {
            System.out.println("This taskplan record does not exist.");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in addTaskplanRecord.");
            return DB_SEVERE;
        }
    }
    
    public int deleteTaskplanRecord(TaskPlanDTO taskrec) {        
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf);
        
        try {
            Taskplan record = new Taskplan();
            record.setId(Integer.valueOf(taskrec.getTaskId()));            
            taskplandao.destroy(record.getId());
            return SUCCESS;
        } 
        catch (NonexistentEntityException e) {
            System.out.println("Record for this Taskplan does not exist");            
            return DB_NON_EXISTING;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in deleteTaskplanRecord.");
            return DB_SEVERE;
        }
    }
    
    public TaskPlanDTO getTaskPlanForId(String id) {
        TaskplanDAO taskplandao = new TaskplanDAO(utx, emf); 
        TaskPlanDTO record = new TaskPlanDTO();
        Date mysqlDate;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Taskplan taskplanrec = taskplandao.getTaskplanPerId(Integer.parseInt(id));

            record.setTaskId(taskplanrec.getId().toString());
            record.setTaskType(taskplanrec.getTasktype());
            mysqlDate = taskplanrec.getTaskdate();
            record.setTaskDt(formatter.format(mysqlDate));
            record.setTaskName(taskplanrec.getTaskname());
            record.setHarvestId(String.valueOf(taskplanrec.getHasvestid()));
            record.setHarvestDto(getHarvestRecForId(String.valueOf(taskplanrec.getHasvestid())));
            if (taskplanrec.getResourceid() == null) {
                record.setResourceId(null);
            } else {
                record.setResourceId(String.valueOf(taskplanrec.getResourceid()));
            }
            
            if (taskplanrec.getAppamtcost() == null)
                    record.setAppliedAmtCost(null);
                else
                    record.setAppliedAmtCost(String.format("%.2f", taskplanrec.getAppamtcost()));
            
            if (taskplanrec.getAppliedamt() == null) {
                record.setAppliedAmount(null);
            } else {
                record.setAppliedAmount(String.format("%.2f", taskplanrec.getAppliedamt()));
            }
            record.setAppliedFlag(taskplanrec.getAppliedflag());
            record.setComments(taskplanrec.getComments());
            return record;
        }
        catch (NoResultException e) {
            System.out.println("No task is found for this id.");            
            return null;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getTaskPlanForId.");
            return null;
        }
    }
    
    public int getMaxEmpLeaveId(){
        EmpLeaveDAO leavedao = new EmpLeaveDAO(utx, emf);
        try {
            return leavedao.getMaxEmpLeaveId();
        }
        catch (NoResultException e) {
            System.out.println("No records in empleave table");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getMaxEmpLeaveId().");
            //            return DB_SEVERE;
            return 0;
        }
    }
    public int getCountLeaveEmp(String empid){
        EmpLeaveDAO leavedao = new EmpLeaveDAO(utx, emf);
        try {
            Long longcount = leavedao.getLeaveCount(Integer.parseInt(empid));            
            return longcount.intValue();
        }
        catch (NoResultException e) {
            System.out.println("No records in empleave table for this employee");            
            return 0;
        }
        catch (Exception exception) {
            System.out.println(exception + " has occurred in getCountLeaveEmp().");
            return DB_SEVERE;
        }
    }
    public int addEmpleaveRecord(EmpLeaveDTO leaverec) {
        EmpLeaveDAO leavedao = new EmpLeaveDAO(utx, emf);
        Date mysqlDate;
        String pattern = "dd MMM yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Empleave rec = new Empleave();
            rec.setId(Integer.valueOf(leaverec.getId()));
            rec.setEmployeeid(Integer.parseInt(leaverec.getEmpid()));
            rec.setComments(leaverec.getComments());
            

            mysqlDate = formatter.parse(leaverec.getLeavedate());
            rec.setLeavedate(mysqlDate);
            leavedao.create(rec);
            return SUCCESS;
        } catch (PreexistingEntityException e) {
            System.out.println("Record is already there for this empleave record");
            return DB_DUPLICATE;
        } catch (Exception exception) {
            System.out.println(exception + " has occurred in addEmpleaveRecord.");
            return DB_SEVERE;
        }
    }
// **********************commented   
//    public List<ShopResDTO> getShopResForResidRate(String resid) {
//        ShopResDAO shopresdao = new ShopResDAO(utx, emf);  
//        List<ShopResDTO> recordList = new ArrayList<>();
//        ShopResDTO record = new ShopResDTO();
//        Date mysqlDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
//        try {  
//            List<Shopresource> shopreslist = shopresdao.getShopDtlssForResRt(Integer.parseInt(resid));
//            for (int i = 0; i < shopreslist.size(); i++) {
//                record.setId(String.valueOf(shopreslist.get(i).getId()));
//                record.setShopId(String.valueOf(shopreslist.get(i).getShopid()));
//                record.setResourceId(String.valueOf(shopreslist.get(i).getResourceid()));
//                record.setShopName(getShopNameForId(String.valueOf(shopreslist.get(i).getShopid())).getShopName());
//                record.setResourceName(getResourceNameForId(shopreslist.get(i).getResourceid()).getResourceName());
//                record.setRate(String.format("%.2f", shopreslist.get(i).getRate().floatValue()));              
//                record.setUnit(getResourceNameForId(shopreslist.get(i).getResourceid()).getUnit());
//                mysqlDate = shopreslist.get(i).getResrtdate();
//                
//                if (mysqlDate != null) {
//                    record.setResRateDate(formatter.format(mysqlDate));
//                } else {
//                    record.setResRateDate("");
//                }
//                record.setStockPerRate(String.format("%.2f", shopreslist.get(i).getStockperrt()));
//                recordList.add(record);
//                record = new ShopResDTO();
//            }        
//            return recordList;
//        }
//        catch (NoResultException e) {
//            System.out.println("No ShopResource records are found");            
//            return null;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in getShopResName().");
//            return null;
//        }
//    }
//    public List<SiteCropDTO> getSiteCropsPerId(String siteid) {
//        SiteCropDAO sitecropdao = new SiteCropDAO(utx, emf);  
//        List<SiteCropDTO> recordList = new ArrayList<>();
//        SiteCropDTO record = new SiteCropDTO();
//        try {  
//            List<Sitecrop> sitecrops = sitecropdao.listSitCropForId(siteid);
//            for (int i = 0; i < sitecrops.size(); i++) {                 
//                record.setSiteId(sitecrops.get(i).getSitecropPK().getSiteid());
//                record.setCropName(sitecrops.get(i).getSitecropPK().getCrop());
//                record.setSowingDate(sitecrops.get(i).getSitecropPK().getSowingdt());
//                record.setHarvestDate(sitecrops.get(i).getHarvestingdt());
//                recordList.add(record);
//                record = new SiteCropDTO();
//            }        
//            return recordList;
//        }
//        catch (NoResultException e) {
//            System.out.println("No sitecrop records are found for this site");            
//            return null;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in getSiteCropsPerId.");
//            return null;
//        }
//    }
//    
//    public List<CropDTO> getCropList() {
//        CropDAO cropdao = new CropDAO(utx, emf);  
//        List<CropDTO> recordList = new ArrayList<>();
//        CropDTO record = new CropDTO();
//        Date mysqlDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
//        try {  
//            List<Crop> croplist = cropdao.getCropListAll();
//            for (int i = 0; i < croplist.size(); i++) {
//                record.setCropCategory(croplist.get(i).getCropPK().getCropcategory());
//                record.setCropName(croplist.get(i).getCropPK().getCrop());  
//                record.setDetails(croplist.get(i).getDetails());
//                if (croplist.get(i).getSowingdt() != null) {
//                    mysqlDate = croplist.get(i).getSowingdt();                    
//                    record.setSowingDate(formatter.format(mysqlDate));
//                }else{
//                    record.setSowingDate("");
//                }
//                if (croplist.get(i).getHarvestingdt() != null) {
//                    mysqlDate = croplist.get(i).getHarvestingdt();
//                    record.setHarvestDate(formatter.format(mysqlDate));
//                }else{
//                    record.setHarvestDate("");
//                }                               
//                recordList.add(record);
//                record = new CropDTO();
//            }        
//            return recordList;
//        }
//        catch (NoResultException e) {
//            System.out.println("No crops are found");            
//            return null;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in getCropList.");
//            return null;
//        }
//    }
    
//    public CropDTO getCropsPerPk(String cropcategory, String cropname) {
//        CropDAO cropdao = new CropDAO(utx, emf);        
//        CropDTO record = new CropDTO();
//        Date mysqlDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
//        try {
//            Crop croprec = cropdao.getCropPerPK(cropcategory, cropname);
//
//            record.setCropCategory(croprec.getCropPK().getCropcategory());
//            record.setCropName(croprec.getCropPK().getCrop());
//            record.setDetails(croprec.getDetails());
//            if (croprec.getSowingdt() != null) {
//                mysqlDate = croprec.getSowingdt();
//                record.setSowingDate(formatter.format(mysqlDate));
//            } else {
//                record.setSowingDate("");
//            }
//            if (croprec.getHarvestingdt() != null) {
//                mysqlDate = croprec.getHarvestingdt();
//                record.setHarvestDate(formatter.format(mysqlDate));
//            } else {
//                record.setHarvestDate("");
//            }
//
//            return record;
//        }
//        catch (NoResultException e) {
//            System.out.println("No crop record is found for cropcategory, cropname");            
//            return null;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in getCropsPerPk.");
//            return null;
//        }
//    }
    
//    public int editCrop(CropDTO cropdto) {
//        CropDAO cropdao = new CropDAO(utx, emf);          
//        
//        Date mysqlDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
//        try {
//            CropPK croprecpk = new CropPK();
//            Crop croprec = new Crop();
//            croprecpk.setCropcategory(cropdto.getCropCategory());
//            croprecpk.setCrop(cropdto.getCropName());
//            croprec.setCropPK(croprecpk);
//            croprec.setDetails(cropdto.getDetails());
//            mysqlDate = formatter.parse(cropdto.getSowingDate());
//            croprec.setSowingdt(mysqlDate);
//            mysqlDate = formatter.parse(cropdto.getHarvestDate());
//            croprec.setHarvestingdt(mysqlDate);
//            cropdao.edit(croprec);
//            return SUCCESS;
//        }
//        catch (NoResultException e) {
//            System.out.println("No crop record is found for cropcategory, cropname");            
//            return DB_NON_EXISTING;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in editCrop.");
//            return DB_SEVERE;
//        }
//    }
    
//    public int addCrop(CropDTO cropdto) {
//        CropDAO cropdao = new CropDAO(utx, emf);         
//        
//        Date mysqlDate;
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
//        try {
//            CropPK croprecpk = new CropPK();
//            Crop croprec = new Crop();
//            croprecpk.setCropcategory(cropdto.getCropCategory());
//            croprecpk.setCrop(cropdto.getCropName());
//            croprec.setCropPK(croprecpk);
//            croprec.setDetails(cropdto.getDetails());
//            mysqlDate = formatter.parse(cropdto.getSowingDate());
//            croprec.setSowingdt(mysqlDate);
//            mysqlDate = formatter.parse(cropdto.getHarvestDate());
//            croprec.setHarvestingdt(mysqlDate);
//            cropdao.create(croprec);
//            return SUCCESS;
//        }
//        catch (PreexistingEntityException e) {
//            System.out.println("Record is already there for this cropcategory, cropname");            
//            return DB_DUPLICATE;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in addCrop.");
//            return DB_SEVERE;
//        }
//    }
    
//    public List<String> getResCatForCrop(String cropcat, String cropname) {
//        FarmresourceDAO resourcedao = new FarmresourceDAO(utx,emf);  
//        List<String> recordList = new ArrayList<>();
//        try {  
//            List<String> rescatforcrop = resourcedao.listResCat(cropcat, cropname);
//            for (int i = 0; i < rescatforcrop.size(); i++) {              
//                recordList.add(rescatforcrop.get(i));
//            }        
//            return recordList;
//        }
//        catch (NoResultException e) {
//            System.out.println("No resource is found for this crop");            
//            return null;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in getResCatForCrop.");
//            return null;
//        }
//    }
//    
//    public List<String> getResDetForCrop(String cropcat, String cropname, String resourcecat) {
//        FarmresourceDAO resourcedao = new FarmresourceDAO(utx,emf);  
//        List<String> recordList = new ArrayList<>();
//        try {  
//            List<String> rescatforcrop = resourcedao.listResDet(cropcat, cropname, resourcecat);
//            for (int i = 0; i < rescatforcrop.size(); i++) {              
//                recordList.add(rescatforcrop.get(i));
//            }        
//            return recordList;
//        }
//        catch (NoResultException e) {
//            System.out.println("No resource is found for this crop and resource category");            
//            return null;
//        }
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in getResDetForCrop.");
//            return null;
//        }
//    }
    
//    public int existsSiteForCrop(String cropcategory, String cropname) {
//        SiteDAO sitedao = new SiteDAO(utx, emf);
//        try {
//            int countofsiteid = sitedao.listSiteForCrop(cropcategory, cropname);
//            return countofsiteid;
//        }
//        
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in existsSiteForCrop.");
//            return DB_SEVERE;
//        }
//    }
//    
//    public int existsResourceForCrop(String cropcategory, String cropname) {
//        FarmresourceDAO resourcedao = new FarmresourceDAO(utx, emf);
//        try {
//            int count = resourcedao.listResourceForCrop(cropcategory, cropname);
//            return count;
//        }
//        
//        catch (Exception exception) {
//            System.out.println(exception + " has occurred in existsResourceForCrop.");
//            return DB_SEVERE;
//        }
//    }
    
}
