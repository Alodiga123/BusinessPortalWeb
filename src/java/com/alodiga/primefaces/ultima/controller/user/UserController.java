/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alodiga.primefaces.ultima.controller.user;

import com.portal.business.commons.data.AccessControlData;
import com.portal.business.commons.data.UserData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.generic.WsRequest;
import com.portal.business.commons.models.Profile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.DualListModel;
import com.portal.business.commons.models.User;
import com.portal.business.commons.utils.Encoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;

@ManagedBean
public class UserController {
    private Long id;
    private Timestamp creationDate;
    private String email;
    private boolean receiveNotification;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String password2;
    private String phoneNumber;
    private DualListModel<Profile> profiles;
    private AccessControlData accessControlData = null;
    private UserData userData = null;
    private String messages = null;
    private User user;
    
    @PostConstruct
    public void init(){
        try {
            HttpServletRequest req= (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            user = (User) req.getAttribute("user");
            accessControlData = new AccessControlData();
            userData = new UserData();
            WsRequest request = new WsRequest();
            List<Profile>  profilesSource = accessControlData.getProfiles(request);
            List<Profile> profilesTarget =  new ArrayList<Profile>();
            profiles = new DualListModel<Profile>(profilesSource, profilesTarget);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
        } catch (NullParameterException ex) {
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public DualListModel<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(DualListModel<Profile> profiles) {
        this.profiles = profiles;
    }

    public void save() {
        try {
            user = new User();
            user.setLogin(login);

            user.setPhoneNumber(phoneNumber);
            user.setCreationDate(new Timestamp(System.currentTimeMillis()));
            user.setEmail(email);
            user.setEnabled(true);
            user.setPassword(Encoder.MD5(password));
            user.setReceiveNotification(true);
            WsRequest request = new WsRequest();
            request.setParam(user);
            userData.saveUser(request);
            messages = "El usuario " + login + " ha sido guardado con exito";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(messages));
        } catch (NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Faltan parametros"));
        } catch (GeneralException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Error General"));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public boolean validatePassword() {
        boolean valid = false;
        if (password.equals(password2))
            valid = true;
        return valid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public void reset() {
        RequestContext.getCurrentInstance().reset("UserCreateForm:grid");
    }
    
    public void doRediret() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("listUser.xhtml");
        } catch (IOException ex) {
            System.out.println("com.alodiga.primefaces.ultima.controller.User1View.doRediret()");
        }
    }
}
