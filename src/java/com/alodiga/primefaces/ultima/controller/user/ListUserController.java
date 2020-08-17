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

import com.alodiga.primefaces.ultima.data.model.LazyUserDataModel;
import com.portal.business.commons.data.AccessControlData;
import com.portal.business.commons.data.UserData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.generic.WsRequest;
import com.portal.business.commons.models.Profile;
import com.portal.business.commons.models.User;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

@ManagedBean(name = "dtLazyUserView")
@ViewScoped
public class ListUserController implements Serializable {

    private LazyDataModel<User> lazyModel;

    private User selectedUser;
    private UserData userData = null;
    private DualListModel<Profile> profiles;
    private AccessControlData accessControlData;
    private String icon;

    @PostConstruct
    public void init() {
        try {
            userData = new UserData();
            WsRequest request = new WsRequest();
            lazyModel = new LazyUserDataModel(userData.getUsers(request));
        } catch (EmptyListException ex) {
            Logger.getLogger(ListUserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(ListUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public AccessControlData getAccessControlData() {
        return accessControlData;
    }

    public void setAccessControlData(AccessControlData accessControlData) {
        this.accessControlData = accessControlData;
    }

    public LazyDataModel<User> getLazyModel() {
        return lazyModel;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public DualListModel<Profile> getProfiles() {
        try {
            accessControlData = new AccessControlData();
            userData = new UserData();
            WsRequest request = new WsRequest();
            List<Profile> list = accessControlData.getProfiles(request);
            List<Profile> profilesSource = new ArrayList<Profile>();
            List<Profile> profilesTarget = new ArrayList<Profile>();
            if (selectedUser != null) {

                profilesSource.add(selectedUser.getProfile());

                for (Profile profile : list) {
                    if (!profilesSource.contains(profile)) {
                        profilesTarget.add(profile);
                    }

                }
            } else {
                profilesSource = list;
            }

            profiles = new DualListModel<Profile>(profilesSource, profilesTarget);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
        } catch (NullParameterException ex) {
        }
        return profiles;
    }

    public void setProfiles(DualListModel<Profile> profiles) {
        this.profiles = profiles;
    }

    public void save() {
        try {
            WsRequest request = new WsRequest();

            /*List<UserHasProfile> userHasProfiles = new ArrayList<UserHasProfile>();
            for (Profile profile : profiles.getSource()) {
                UserHasProfile userHasProfile = new UserHasProfile();
                WsRequest request1 = new WsRequest();
                request1.setParam(profile.getId());
                try {
                    profile = accessControlData.loadProfile(request1);
                } catch (RegisterNotFoundException ex) {
                    System.out.println("profile no encontrados");
                }
                userHasProfile.setProfile(profile);
                userHasProfile.setUser(selectedUser);
                Date now = new Date((new java.util.Date()).getTime());
                Timestamp creationDate = new Timestamp(now.getTime());
                userHasProfile.setBeginningDate(creationDate);
                userHasProfiles.add(userHasProfile);
            }
            selectedUser.setUserHasProfile(userHasProfiles);*/
            request.setParam(selectedUser);
            userData.saveUser(request);
        } catch (NullParameterException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Faltan parametros"));
        } catch (GeneralException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Error General"));
        }
    }

    public void handleReturnDialog(SelectEvent event) {
        if (event != null && event.getObject() != null) {
        }
    }

    public void doRediret() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("userView.xhtml");
        } catch (IOException ex) {
            System.out.println("com.alodiga.primefaces.ultima.controller.LazyUserView.doRediret()");
        }
    }

    public void doChanceStatus() {
        try {
            WsRequest request = new WsRequest();
            selectedUser.setEnabled(!selectedUser.getEnabled());
            request.setParam(selectedUser);
            userData.saveUser(request);
            System.out.println("com.alodiga.primefaces.ultima.controller.LazyUserView.doChanceStatus()");
        } catch (NullParameterException ex) {
            Logger.getLogger(ListUserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(ListUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String icon(User user) {
        if (user.getEnabled()) {
            return "ui-icon-check";
        } else {
            return "ui-icon-block";
        }
    }

}
