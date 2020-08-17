package com.alodiga.businessportal.converter;

import com.portal.business.commons.cms.CmsDocumentPersonType;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author hvarona
 */
@FacesConverter("cmsDocumentTypeConverter")
public class CmsDocumentTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.equals("")) {
            return "";
        }
        try {
            int idDocumentType = Integer.parseInt(submittedValue);
            CmsDocumentPersonType documentType = new CmsDocumentPersonType();
            documentType.setId(idDocumentType);
            return documentType;
        } catch (NumberFormatException ex) {
            Logger.getLogger(PosConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            if (value instanceof CmsDocumentPersonType) {
                return Integer.toString(((CmsDocumentPersonType) value).getId());
            } else {
                return value.toString();
            }

        }
    }

}
