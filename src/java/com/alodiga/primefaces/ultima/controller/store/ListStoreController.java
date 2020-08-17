package com.alodiga.primefaces.ultima.controller.store;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.data.StoreData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.generic.WsRequest;
import com.portal.business.commons.models.Store;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "dtListStoreController", eager = false)
@ViewScoped
public class ListStoreController implements Serializable {

    private Store selectedStore;
    private StoreData storeData = null;
    List<Store> stores;
    List<Store> filteredStore;

    @ManagedProperty(value = "#{loginBean}")
    LoginBean loginBean;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    private ResourceBundle msg;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
        storeData = new StoreData();
        try {
            stores = storeData.getStores(loginBean.getCurrentBusiness());

        } catch (NullParameterException | GeneralException ex) {
            Logger.getLogger(ListStoreController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyListException ex) {
        }
    }

    public Store getSelectedStore() {
        return selectedStore;
    }

    public void setSelectedStore(Store selectedStore) {
        this.selectedStore = selectedStore;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public List<Store> getFilteredStore() {
        return filteredStore;
    }

    public void setFilteredStore(List<Store> filteredStore) {
        this.filteredStore = filteredStore;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public void changeEnable(Store store) {
        try {
            storeData.saveStore(store);
            if (store.getEnabled()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("storeEnabled")));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("storeDisabled")));
            }
        } catch (NullParameterException | GeneralException ex) {
            Logger.getLogger(ListStoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void save() {
        try {
            Store store = null;
            if (selectedStore.getId() != null) {
                store = selectedStore;
            }
            storeData.saveStore(store);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("storeModified")));
        } catch (NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Faltan parametros"));
        } catch (GeneralException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("errorGeneral")));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("errorGeneral")));
        }
    }

    public void handleReturnDialog(SelectEvent event) {
        if (event != null && event.getObject() != null) {
        }
    }

    public void doRediret() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("storeView.xhtml");
        } catch (IOException ex) {
            System.out.println("com.alodiga.primefaces.ultima.controller.ListStoreController.doRediret()");
        }
    }
}
