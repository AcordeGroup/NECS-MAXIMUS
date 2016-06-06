package com.necs.maximus.ui.entity;

import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.facade.HasFacade;
import com.necs.maximus.ui.beans.util.JsfUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@FacesConverter(value = "hasConverter")
public class HasConverter implements Converter {

    @Inject
    private HasFacade ejbFacade;

    private static final String SEPARATOR = "#";
    private static final String SEPARATOR_ESCAPED = "\\#";

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0 || JsfUtil.isDummySelectItem(component, value)) {
            return null;
        }
        return this.ejbFacade.find(getKey(value));
    }

    com.necs.maximus.db.entity.HasPK getKey(String value) {
        com.necs.maximus.db.entity.HasPK key;
        String values[] = value.split(SEPARATOR_ESCAPED);
        key = new com.necs.maximus.db.entity.HasPK();
        key.setIdQuote(Integer.parseInt(values[0]));
        key.setPartNumber(values[1]);
        return key;
    }

    String getStringKey(com.necs.maximus.db.entity.HasPK value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value.getIdQuote());
        sb.append(SEPARATOR);
        sb.append(value.getPartNumber());
        return sb.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null
                || (object instanceof String && ((String) object).length() == 0)) {
            return null;
        }
        if (object instanceof Has) {
            Has o = (Has) object;
            return getStringKey(o.getHasPK());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Has.class.getName()});
            return null;
        }
    }

}
