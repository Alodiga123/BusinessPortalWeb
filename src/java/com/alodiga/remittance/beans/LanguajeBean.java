/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.remittance.beans;

import com.portal.business.commons.data.UtilsData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.models.Language;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author usuario
 */
@SessionScoped
@ManagedBean(name = "languajeBean")
public class LanguajeBean implements Serializable {

    private String languaje;

    private Language language;
    
    @PostConstruct
    public void init(){
        try {
            UtilsData utilsData = new UtilsData();
            language = utilsData.getLanguage("es");
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(LanguajeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LanguajeBean() {
        languaje = "es";
    }

    public String getLanguaje() {
        return languaje;
    }

    public void setLanguaje(String languaje) {
        this.languaje = languaje;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void localityChanged(ValueChangeEvent e) {
        String newLocaleValue = e.getNewValue().toString();
        try {
            language = new UtilsData().getLanguage(newLocaleValue);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(LanguajeBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (newLocaleValue.equals("en")) {
            languaje = "en";
            FacesContext.getCurrentInstance().getViewRoot().setLocale(Locale.ENGLISH);
        } else {
            FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("es", "espana"));
            languaje = "es";
        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        session.setAttribute("languaje", languaje);
    }

}
