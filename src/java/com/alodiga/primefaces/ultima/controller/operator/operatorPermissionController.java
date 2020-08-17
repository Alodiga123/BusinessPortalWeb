package com.alodiga.primefaces.ultima.controller.operator;

import com.alodiga.remittance.beans.LanguajeBean;
import com.portal.business.commons.models.Permission;
import com.portal.business.commons.models.PermissionHasProfile;
import com.portal.business.commons.models.Profile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "operatorPermission")
@ViewScoped
public class operatorPermissionController {

    List<Permission> availablePermissions;

    Profile profile;

    private ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }

    }

    public List<Permission> getAvailablePermissions() {
        return availablePermissions;
    }

    public void setAvailablePermissions(List<Permission> availablePermissions) {
        this.availablePermissions = availablePermissions;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public void reloadPermission() {
        availablePermissions = new ArrayList();
        List<PermissionHasProfile> phps = profile.getPermissionHasProfiles();
        for (PermissionHasProfile php : phps) {
            availablePermissions.add(php.getPermission());
        }
    }

    public String getPermissionLabel(Permission permission) {
        return permission.getPermissionDataByLanguageId(lenguajeBean.getLanguage().getId()).getAlias();
    }

}
