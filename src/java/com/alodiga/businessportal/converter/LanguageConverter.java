package com.alodiga.businessportal.converter;

import com.portal.business.commons.data.UtilsData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.generic.WsRequest;
import com.portal.business.commons.models.Language;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("languageConverter")
public class LanguageConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        Language language = null;
        try {
            UtilsData utilsData = new UtilsData();
            WsRequest request = new WsRequest();
            request.setParam(Long.parseLong(submittedValue));
            language = utilsData.loadLanguage(request);

        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(LanguageConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(LanguageConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(LanguageConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return language;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            if (value instanceof Language) {
                return ((Language) value).getDescription();
            }
            return String.valueOf(value);
        }
    }
}
