package com.alodiga.businessportal.converter;

import com.portal.business.commons.data.PreferenceData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.models.Preference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("preferenceConverter")
public class PreferenceConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {

        try {
            PreferenceData preferenceData = new PreferenceData();
            Long preferenceId = Long.parseLong(submittedValue);
            return (Preference) preferenceData.getPreference(preferenceId);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(PreferenceConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            
            if (value instanceof Preference) {
                return ((Preference) value).getId().toString();
            }
            return String.valueOf(value);
        }
    }
}
