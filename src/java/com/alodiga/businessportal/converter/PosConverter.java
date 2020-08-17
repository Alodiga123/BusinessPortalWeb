package com.alodiga.businessportal.converter;

import com.portal.business.commons.data.PosData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.generic.WsRequest;
import com.portal.business.commons.models.Pos;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("posConverter")
public class PosConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        Pos pos = null;
        try {
            PosData posData = new PosData();
            WsRequest request = new WsRequest();
            request.setParam(Long.parseLong(submittedValue));
            pos = posData.loadPos(request);

        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(PosConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pos;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            if (value instanceof Pos) {
                return ((Pos) value).getId().toString();
            } else {
                return value.toString();
            }

        }
    }
}
