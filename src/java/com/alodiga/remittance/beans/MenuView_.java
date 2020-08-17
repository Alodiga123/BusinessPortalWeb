package com.alodiga.remittance.beans;

import com.alodiga.remittance.parent.GenericController;
import com.portal.business.commons.managers.PermissionManager;
import com.portal.business.commons.models.Permission;
import com.portal.business.commons.models.PermissionGroup;
import com.portal.business.commons.models.Profile;
import com.portal.business.commons.models.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author usuario
 */
@ManagedBean
public class MenuView_ extends GenericController {

    private MenuModel model;
    private Long languageId = 2L;
    private Profile profile;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        User user = new User();
        user = (User) session.getAttribute("user");
        profile = (Profile) session.getAttribute("profile");
        String locale = (String) session.getAttribute("languaje");
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
        }
        if (locale.equals("en")) {
            languageId = 1L;
        } else {
            languageId = 2L;
        }

        model = new DefaultMenuModel();
        DefaultMenuItem menuItem = new DefaultMenuItem(getStandarMessage("start"));
        menuItem.setOutcome("/dashboard");
        model.addElement(menuItem);

        try {
            PermissionManager pm = new PermissionManager(profile);

            List<PermissionGroup> permissionsGroups = pm.getPermissionGroups();
            for (PermissionGroup pg : permissionsGroups) {
                DefaultSubMenu dm = new DefaultSubMenu(pg.getPermissionGroupDataByLanguageId(languageId).getAlias());
                List<Permission> permissions = pm.getPermissionByGroupId(pg.getId());
                if (permissions != null) {
                    for (Permission p : permissions) {
                        DefaultMenuItem menuItem_ = new DefaultMenuItem(p.getPermissionDataByLanguageId(languageId).getAlias());
                        menuItem_.setUrl(p.getAction());
                        menuItem_.setAjax(false);
                        dm.addElement(menuItem_);
                    }
                }
                model.addElement(dm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tes " + e.getMessage());
        }
    }

    public MenuModel getModel() {
        return model;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
