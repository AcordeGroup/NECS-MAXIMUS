/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.entity;

import com.necs.maximus.db.entity.Vendor;
import com.necs.maximus.db.facade.VendorFacade;
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
 * @author luis
 */
@FacesConverter(value = "vendorConverter")
public class VendorConverter implements Converter {

    @Inject
    private VendorFacade vendorFacade;

    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.length() == 0 || JsfUtil.isDummySelectItem(component, value)) {
            return null;
        }
        if (value.equals(bundle.getString("SelectOneMessage"))) {
            return null;
        }
        return this.vendorFacade.find(getKey(value));
    }

    java.lang.Integer getKey(String value) {
        java.lang.Integer key;
        key = Integer.valueOf(value);
        return key;
    }

    String getStringKey(java.lang.Integer value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        return sb.toString();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null
                || (value instanceof String && ((String) value).length() == 0)) {
            return null;
        }
        if (value instanceof Vendor) {
            Vendor o = (Vendor) value;
            return getStringKey(o.getIdVendor());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{value, value.getClass().getName(), Vendor.class.getName()});
            return null;
        }
    }

}
