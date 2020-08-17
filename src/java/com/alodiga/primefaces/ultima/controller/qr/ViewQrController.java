package com.alodiga.primefaces.ultima.controller.qr;

import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.data.PosData;
import com.portal.business.commons.data.StoreData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.models.Business;
import com.portal.business.commons.models.Pos;
import com.portal.business.commons.models.Store;
import com.portal.business.commons.utils.AlodigaCryptographyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "viewqrController")
@ViewScoped
public class ViewQrController {

    private List<Store> storeList;
    private Store selectedStore;

    private List<Pos> posList = new ArrayList();
    private Pos selectedPos;

    private String qrtext;

    private Business business;

    private boolean showStore = false;
    private boolean showPos = false;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        try {
            business = loginBean.getCurrentBusiness();
            StoreData storeData = new StoreData();
            storeList = storeData.getStores(business);
        } catch (EmptyListException | GeneralException | NullParameterException ex) {
            Logger.getLogger(ViewQrController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Store> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList = storeList;
    }

    public Store getSelectedStore() {
        return selectedStore;
    }

    public void setSelectedStore(Store selectedStore) {
        this.selectedStore = selectedStore;
        this.selectedPos = null;
    }

    public List<Pos> getPosList() {
        return posList;
    }

    public void setPosList(List<Pos> posList) {
        this.posList = posList;
    }

    public Pos getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(Pos selectedPos) {
        this.selectedPos = selectedPos;

    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getQrtext() {
        return qrtext;
    }

    public void setQrtext(String qrtext) {
        this.qrtext = qrtext;
    }

    public void reloadPosList() {
        try {
            if (selectedStore != null) {

                PosData posData = new PosData();
                posList = posData.getPosByStore(selectedStore);
            }
        } catch (EmptyListException ex) {
            posList = new ArrayList();
        } catch (GeneralException ex) {
            Logger.getLogger(ViewQrController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void viewBusinessQrCode() {
        showStore = false;
        showPos = false;
        this.qrtext = "";
        try {
            String toEncrypt = business.getCode();
            this.qrtext = AlodigaCryptographyUtils.encrypt(toEncrypt, "1nt3r4xt3l3ph0ny");
        } catch (Exception ex) {
            Logger.getLogger(Pos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void viewStoreQrCode() {
        showStore = true;
        showPos = false;

        this.qrtext = "";
        if (selectedStore != null) {
            try {
                String toEncrypt = business.getCode() + ";" + selectedStore.getStoreCode();
                this.qrtext = AlodigaCryptographyUtils.encrypt(toEncrypt, "1nt3r4xt3l3ph0ny");
            } catch (Exception ex) {
                Logger.getLogger(Pos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void viewPosQrCode() {
        showStore = true;
        showPos = true;
        if (selectedPos != null) {
            this.qrtext = selectedPos.getQrText();
        }
    }

    public boolean isShowStore() {
        return showStore;
    }

    public void setShowStore(boolean showStore) {
        this.showStore = showStore;
    }

    public boolean isShowPos() {
        return showPos;
    }

    public void setShowPos(boolean showPos) {
        this.showPos = showPos;
    }

}
