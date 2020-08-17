package com.alodiga.primefaces.ultima.controller.operator;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.data.OperatorData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.models.Language;
import com.portal.business.commons.models.Operator;
import com.portal.business.commons.models.Permission;
import com.portal.business.commons.models.PermissionHasProfile;
import com.portal.business.commons.models.Profile;
import com.portal.business.commons.models.ProfileData;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "bpListOperatorController")
@ViewScoped
public class ListOperatorController implements Serializable {

    private Operator selectedOperator;

    private OperatorData operatorData = null;

    private List<Operator> operatorList;
    private List<Operator> filteredOperator;

    private Map<String, String> languages = new TreeMap();
    private Map<String, String> profiles = new TreeMap();

    private Language language = null;
    private Profile profile = null;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    private ResourceBundle msg;

    private List<Permission> availablePermissions;
    private List<Permission> includedPermissions;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
        try {
            operatorData = new OperatorData();
            operatorList = operatorData.getOperatorList(loginBean.getCurrentBusiness());
        } catch (GeneralException ex) {
            Logger.getLogger(ListOperatorController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyListException ignored) {

        }
    }

    public Operator getSelectedOperator() {
        return selectedOperator;
    }

    public void setSelectedOperator(Operator selectedOperator) {
        this.selectedOperator = selectedOperator;
        getPermissions();
    }

    public List<Operator> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<Operator> operatorList) {
        this.operatorList = operatorList;
    }

    public List<Operator> getFilteredOperator() {
        return filteredOperator;
    }

    public void setFilteredOperator(List<Operator> filteredOperator) {
        this.filteredOperator = filteredOperator;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public Map<String, String> getLanguages() {
        if (languages == null || languages.isEmpty()) {
            languages = new TreeMap();
            try {
                List<Language> languageList = operatorData.getLanguageList();
                for (Language singleLangugage : languageList) {
                    languages.put(singleLangugage.getDescription(), singleLangugage.getId().toString());
                }
            } catch (EmptyListException | GeneralException ex) {
                Logger.getLogger(ListOperatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return languages;
    }

    public void setLanguages(Map<String, String> languages) {
        this.languages = languages;
    }

    public Map<String, String> getProfiles() {
        if (profiles == null || profiles.isEmpty()) {
            profiles = new TreeMap();
            try {
                List<Profile> profileList = operatorData.getProfileList();
                for (Profile singleProfile : profileList) {
                    String name = singleProfile.getName();
                    for (ProfileData data : singleProfile.getProfileData()) {
                        if (new Locale(data.getLanguage().getIso()).getLanguage().equals(msg.getLocale().getLanguage())) {
                            name = data.getAlias();
                        }
                    }
                    profiles.put(name, singleProfile.getId().toString());
                }

            } catch (EmptyListException | GeneralException ex) {
                Logger.getLogger(ListOperatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return profiles;
    }

    public void setProfiles(Map<String, String> profiles) {
        this.profiles = profiles;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void getPermissions() {
        availablePermissions = new ArrayList();
        includedPermissions = new ArrayList();
        if (selectedOperator == null || selectedOperator.getProfile() == null) {
            return;
        }
        List<PermissionHasProfile> phps = selectedOperator.getProfile().getPermissionHasProfiles();
        for (PermissionHasProfile php : phps) {
            availablePermissions.add(php.getPermission());
        }
        for (Permission perm : availablePermissions) {
            if (!selectedOperator.getExcludedPermission().contains(perm)) {
                includedPermissions.add(perm);
            }
        }
    }

    public List<Permission> getAvailablePermissions() {
        return availablePermissions;
    }

    public void setAvailablePermissions(List<Permission> availablePermissions) {
        this.availablePermissions = availablePermissions;
    }

    public List<Permission> getIncludedPermissions() {
        return includedPermissions;
    }

    public void setIncludedPermissions(List<Permission> includedPermissions) {
        this.includedPermissions = includedPermissions;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public void changeEnable(Operator operator) {
        try {
            operatorData.saveOperator(operator);
            if(operator.getEnabled()){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("operatorEnabled")));
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("operatorDisabled")));
            }
        } catch (NullParameterException | GeneralException ex) {
            Logger.getLogger(ListOperatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doRediret() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("createOperator.xhtml");
        } catch (IOException ex) {
            System.out.println("com.alodiga.primefaces.ultima.controller.ListOperatorController.doRediret()");
        }
    }

    public void save() {
        try {
            Operator operator = null;
            if (selectedOperator.getId() != null) {
                operator = selectedOperator;
            } else {
                throw new NullParameterException("ID null");
            }

            List<Permission> excludedPermission = new ArrayList();

            for (Permission perm : availablePermissions) {
                if (!includedPermissions.contains(perm)) {
                    excludedPermission.add(perm);
                }
            }
            operator.setExcludedPermission(excludedPermission);
            operatorData.saveOperator(operator);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("operatorSaveSuccesfull")));
        } catch (GeneralException | NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("errorGeneral")));
        }
    }

}
