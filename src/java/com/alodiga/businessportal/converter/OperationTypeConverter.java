package com.alodiga.businessportal.converter;

import com.portal.business.commons.enumeration.OperationType;
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
@FacesConverter("operationTypeConverter")
public class OperationTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        return OperationType.valueOf(submittedValue);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            if (value instanceof OperationType) {
                return ((OperationType) value).name();
            } else {
                return value.toString();
            }

        }
    }

}
