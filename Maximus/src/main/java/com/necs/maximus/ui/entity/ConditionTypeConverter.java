/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.entity;

import com.necs.maximus.db.entity.ConditionType;
import com.necs.maximus.db.facade.ConditionTypeFacade;
import com.necs.maximus.ui.beans.util.JsfUtil;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author Luis Castañeda <luis.castaneda at acorde.com.ve>
 */
@FacesConverter(value = "conditionTypeConverter")
public class ConditionTypeConverter implements Converter {
    
    @Inject
    private ConditionTypeFacade facade;
    
    private final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.length() == 0 || JsfUtil.isDummySelectItem(component, value) || value.equals(bundle.getString("SelectOneMessage"))) {
            return null;
        }
        return this.facade.find(getKey(value));
    }
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object == null
                || (object instanceof String && ((String) object).length() == 0)) {
            return null;
        }
        if (object instanceof ConditionType) {
            ConditionType o = (ConditionType) object;
            return getStringKey(o.getIdConditionType());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ConditionType.class.getName()});
            return null;
        }
    }
    
    String getStringKey(java.lang.Integer value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        return sb.toString();
    }
    
    java.lang.Integer getKey(String value) {
        java.lang.Integer key;
        key = Integer.valueOf(value);
        return key;
    }
    
}
