package com.alodiga.businessportal.converter;

import com.alodiga.remittance.beans.LanguajeBean;
import com.portal.business.commons.data.UserData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.models.Permission;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("permissionConverter")
public class PermissionConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        try {
            UserData userData = new UserData();
            Long profileId = Long.parseLong(submittedValue);
            return userData.loadPermission(profileId);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(PermissionConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            if (value instanceof Permission) {
                return ((Permission) value).getId().toString();
            }
            return String.valueOf(value);
        }
    }

}
