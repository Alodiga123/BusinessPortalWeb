package com.alodiga.remittance.beans;

import com.alodiga.remittance.parent.GenericController;
import com.portal.business.commons.data.UserData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.models.Business;
import com.portal.business.commons.models.Operator;
import com.portal.business.commons.models.Profile;
import com.portal.business.commons.models.User;
import com.portal.business.commons.utils.Encoder;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean extends GenericController implements Serializable {

    private static final long serialVersionUID = 1L;
    private String uname;
    private String password;
    private User userSession;
    private Map<String, String> profiles = null;
    private Profile profile;
    private UserData userData = null;

    private String locale;

    private float businessPercentFee = 1;

    @PostConstruct
    public void init() {
        userData = new UserData();
        locale = "es";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public User getUserSession() {
        return userSession;
    }

    public void setUserSession(User userSession) {
        this.userSession = userSession;
    }

    public float getBusinessPercentFee() {
        return businessPercentFee;
    }

    public void setBusinessPercentFee(float businessPercentFee) {
        this.businessPercentFee = businessPercentFee;
    }

    public Map<String, String> getProfiles() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        locale = (String) session.getAttribute("languaje");
        Long languageId;
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
        }
        if (locale.equals("en")) {
            languageId = 1L;
        } else {
            languageId = 2L;
        }
        profiles = new TreeMap<String, String>();
        try {
            List<Profile> profile1 = userData.getProfiles();
            for (Profile prof : profile1) {
                profiles.put(prof.getProfileDataByLanguageId(languageId).getAlias(), prof.getId().toString());
            }
        } catch (EmptyListException | GeneralException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String loginProject() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (uname.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, getStandarMessage("emptyField"),
                            getStandarMessage("emptyFieldLogin")));
            RequestContext.getCurrentInstance().scrollTo("username");

            uname = null;
        } else if (password.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            getStandarMessage("emptyField"),
                            getStandarMessage("emptyFieldPassword")));
            password = null;
        } else {
            UserData ud = new UserData();

            User user;
            try {
                user = ud.loadUserByLogin(uname, Encoder.MD5(password));
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                HttpSession session = request.getSession(false);
                session.setAttribute("user", user);
                session.setAttribute("profile", user.getProfile());
                profile = user.getProfile();
                userSession = user;
                return "dashboard.xhtml?faces-redirect=true";
            } catch (RegisterNotFoundException ex) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Usuario o password invalido",
                                "Intente nuevamente"));
                return "login.xhtml";
            } catch (NullParameterException ex) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Usuario vacio",
                                "Intente nuevamente"));
                return "login.xhtml";
            } catch (GeneralException ex) {
                ex.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error general",
                                "Intente nuevamente"));
                return "login.xhtml";
            }
        }

        return null;
    }

    public String logout() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "login.xhtml";
    }

    public void rechargeDashboard(AjaxBehaviorEvent event) {
        Profile p = (Profile) ((UIOutput) event.getSource()).getValue();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        session.setAttribute("profile", p);
        profile = p;
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard.xhtml");
        } catch (IOException ex) {
            System.out.println("com.alodiga.remittance.beans.LoginBean.rechargeDashboard()");
        }
    }

    public Business getCurrentBusiness() {
        if (this.userSession instanceof Business) {
            return (Business) this.userSession;
        } else if (this.userSession instanceof Operator) {
            return ((Operator) this.userSession).getCommerce();
        }
        return null;
    }
}
