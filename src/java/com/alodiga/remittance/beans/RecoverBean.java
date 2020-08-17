/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.remittance.beans;
import com.alodiga.remittance.parent.GenericController;
import com.portal.business.commons.data.AccessControlData;
import com.portal.business.commons.data.UserData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.models.User;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.Serializable;
import java.util.Hashtable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "recoverBean")
@SessionScoped
public class RecoverBean extends GenericController implements Serializable {

    
    private static final long serialVersionUID = 1L;
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

 


   
            
    public void validateEmail() {
        String email = getEmailAddress();
        if (!isValid(email)) {
             FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                    getStandarMessage("invalidFielAddress"),
                    getStandarMessage("invalidEmailAddress")));
 
        }
    }
 
    boolean isValid(String email) {
        // Reqular expression pattern to validate the format submitted
        String validator = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+"
                + "(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$";
        if (!email.matches(validator)) {
            return false;
        }
        // Split the user and the domain name
        String[] parts = email.split("@");
 
        boolean retval=true;
        // This is similar to nslookup –q=mx domain_name.com to query
        // the mail exchanger of the domain.
        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial",
                    "com.sun.jndi.dns.DnsContextFactory");
            DirContext context = new InitialDirContext(env);
            Attributes attributes =
                    context.getAttributes(parts[1], new String[]{"MX"});
            Attribute attribute = attributes.get("MX");
            if (attribute.size() == 0) {
               retval=false;
            }
            context.close();
            return retval;
 
        } catch (Exception exception) {
            return false;
        }        
    }
    
    public String recover() {
        User user = null;
        UserData userData = new UserData();
        AccessControlData accessControlData = new AccessControlData();
        try {
            user = userData.loadUserByEmail(emailAddress);
            accessControlData.generateNewPassword(user);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            getStandarMessage("successRecoverMail"),
                            getStandarMessage("successRecoverMailMessage")));
            return "recover.xhtml";
        } catch (RegisterNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            getStandarMessage("registerNotFound"),
                            getStandarMessage("emailNotFound")));
            return "recover.xhtml";
        } catch (NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            getStandarMessage("generalError"),
                            getStandarMessage("generalErrorMessage")));
            return "recover.xhtml";
        } catch (GeneralException ex) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            getStandarMessage("generalError"),
                            getStandarMessage("generalErrorMessage")));
            return "recover.xhtml";
        }
    }

    
    public String cancel() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "login.xhtml";
    }
}