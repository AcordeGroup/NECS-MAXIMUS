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

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0 || JsfUtil.isDummySelectItem(component, value)) {
            return null;
        }
        return this.ejbFacade.find(getKey(value));
    }

    Integer getKey(String value) {
        Integer key;
        key = Integer.valueOf(value);
        return key;
    }

    String getStringKey(Integer value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value);
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
            return getStringKey(o.getIdSubstitute());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), IsSubstitute.class.getName()});
            return null;
        }
    }

}
