package com.necs.maximus.ui.entity;

import com.necs.maximus.db.entity.IsSubstitute;
import com.necs.maximus.db.facade.IsSubstituteFacade;
import com.necs.maximus.ui.beans.util.JsfUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@FacesConverter(value = "isSubstituteConverter")
public class IsSubstituteConverter implements Converter {

    @Inject
    private IsSubstituteFacade ejbFacade;

    private static final String SEPARATOR = "#";
    private static final String SEPARATOR_ESCAPED = "\\#";

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0 || JsfUtil.isDummySelectItem(component, value)) {
            return null;
        }
        return this.ejbFacade.find(getKey(value));
    }

    com.necs.maximus.db.entity.IsSubstitutePK getKey(String value) {
        com.necs.maximus.db.entity.IsSubstitutePK key;
        String values[] = value.split(SEPARATOR_ESCAPED);
        key = new com.necs.maximus.db.entity.IsSubstitutePK();
        key.setPartNumber1(values[0]);
        key.setPartNumber2(values[1]);
        return key;
    }

    String getStringKey(com.necs.maximus.db.entity.IsSubstitutePK value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value.getPartNumber1());
        sb.append(SEPARATOR);
        sb.append(value.getPartNumber2());
        return sb.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null
                || (object instanceof String && ((String) object).length() == 0)) {
            return null;
        }
        if (object instanceof IsSubstitute) {
            IsSubstitute o = (IsSubstitute) object;
            return getStringKey(o.getIsSubstitutePK());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), IsSubstitute.class.getName()});
            return null;
        }
    }

}
