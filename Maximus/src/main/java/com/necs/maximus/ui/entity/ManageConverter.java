package com.necs.maximus.ui.entity;

import com.necs.maximus.db.entity.Manage;
import com.necs.maximus.db.facade.ManageFacade;
import com.necs.maximus.ui.beans.util.JsfUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@FacesConverter(value = "manageConverter")
public class ManageConverter implements Converter {

    @Inject
    private ManageFacade ejbFacade;

    private static final String SEPARATOR = "#";
    private static final String SEPARATOR_ESCAPED = "\\#";

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0 || JsfUtil.isDummySelectItem(component, value)) {
            return null;
        }
        return this.ejbFacade.find(getKey(value));
    }

    com.necs.maximus.db.entity.ManagePK getKey(String value) {
        com.necs.maximus.db.entity.ManagePK key;
        String values[] = value.split(SEPARATOR_ESCAPED);
        key = new com.necs.maximus.db.entity.ManagePK();
        key.setIdQuote(Integer.parseInt(values[0]));
        key.setIdAgent(values[1]);
        key.setNotes(values[2]);
        return key;
    }

    String getStringKey(com.necs.maximus.db.entity.ManagePK value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value.getIdQuote());
        sb.append(SEPARATOR);
        sb.append(value.getIdAgent());
        sb.append(SEPARATOR);
        sb.append(value.getNotes());
        return sb.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null
                || (object instanceof String && ((String) object).length() == 0)) {
            return null;
        }
        if (object instanceof Manage) {
            Manage o = (Manage) object;
            return ""; //getStringKey(o.getManagePK())
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Manage.class.getName()});
            return null;
        }
    }

}
