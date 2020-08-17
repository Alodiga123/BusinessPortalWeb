package com.alodiga.primefaces.ultima.controller.pos;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.data.PosData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.models.Pos;
import com.portal.business.commons.models.Store;
import com.portal.business.commons.utils.AlodigaCryptographyUtils;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author hvarona
 */
@ManagedBean
public class PosController {

    private Long id;
    private String posCode;
    private Store store;
    private Date openTime = Date.from(Instant.ofEpochMilli(4 * 60 * 60 * 1000));
    ;
    private Date closeTime = Date.from(Instant.ofEpochMilli((((27 * 60) + 55) * 60) * 1000));
    private PosData posData = null;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    @PostConstruct
    public void init() {
        posData = new PosData();
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public void save() {
        try {
            Pos pos = new Pos();
            pos.setPosCode(posCode);
            pos.setStore(store);
            pos.setCloseTime(closeTime);
            pos.setOpenTime(openTime);
            pos.setEnabled(true);
            posData.savePos(pos);

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("posSaveSuccesfull")));
            FacesContext.getCurrentInstance().getExternalContext().redirect("listPos.xhtml");
        } catch (NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.missparameters")));
        } catch (GeneralException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.missparameters")));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.general")));
        }
    }

    public void reset() {
        RequestContext.getCurrentInstance().reset("PosCreateForm:dataGrid");
    }

    public void doRediret() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("listPos.xhtml");
        } catch (IOException ex) {
            System.out.println("com.alodiga.primefaces.ultima.controller.PosController.doRediret()");
        }
    }

    public String getQrText() {
        try {
            if (store == null) {
                return "";
            }
            String toEncrypt = store.getCommerce().getCode() + ";" + store.getStoreCode() + ";" + posCode;
            String value = AlodigaCryptographyUtils.encrypt(toEncrypt, "1nt3r4xt3l3ph0ny");
            return value;
        } catch (Exception ex) {
            Logger.getLogger(Pos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
