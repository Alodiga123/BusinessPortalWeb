package com.alodiga.remittance.beans;

import com.portal.business.commons.data.UserData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.models.User;
import com.portal.business.commons.utils.Encoder;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "changePasswordBean")
@ViewScoped
public class ChangePasswordBean {

    private String oldPassword;

    private String newPassword;

    private String verifyPassword;

    private String messages = null;
    ResourceBundle bundle;

    @ManagedProperty(value = "#{loginBean}")
    LoginBean loginBean;

    @ManagedProperty(value = "#{languajeBean}")
    LanguajeBean lenguajeBean;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            bundle = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            bundle = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public boolean validate() {
        try {
            FacesContext.getCurrentInstance().getMessageList().clear();
        } catch (Exception ignored) {
        }
        if (newPassword == null) {

            return false;
        }
        boolean valid = true;
        if (!newPassword.matches(".*[-+()~!@#$%^&*+}{:;/?.><,]+.*")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.passwordNoSpecialSymbol")));
            valid = false;
        }
        if (!newPassword.matches(".*[0-9]+.*")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.passwordNoNumber")));
            valid = false;
        }
        if (!newPassword.matches(".*[a-z]+.*")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.passwordNoAlpha")));
            valid = false;
        }
        if (!newPassword.matches(".*[A-Z]+.*")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.passwordNoCapital")));
            valid = false;
        }
        if (!newPassword.matches(".{8,}")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.passwordMinLenght")));
            valid = false;
        }
        if (!newPassword.equals(verifyPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.verifypassword")));
            valid = false;
        }
        return valid;
    }

    public void save() {
        try {
            if (!validate()) {
                return;
            }
            User user = loginBean.getUserSession();
            if (!user.getPassword().equals(Encoder.MD5(oldPassword))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                bundle.getString("error"), bundle.getString("error.badoldpassword")));
                return;
            }

            user.setPassword(Encoder.MD5(newPassword));

            UserData userData = new UserData();
            userData.saveUser(user);
            messages = bundle.getString("changePasswordsuccesfull");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(messages));
        } catch (NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.missparameters")));
        } catch (GeneralException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            bundle.getString("error"), bundle.getString("error.general")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
    }

}
