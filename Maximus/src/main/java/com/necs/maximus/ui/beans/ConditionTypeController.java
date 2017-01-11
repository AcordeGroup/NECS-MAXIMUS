package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.ConditionType;
import com.necs.maximus.ui.beans.AbstractController;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

@Named(value = "conditionTypeController")
@ViewScoped
public class ConditionTypeController extends AbstractController<ConditionType> {

    public ConditionTypeController() {
        // Inform the Abstract parent controller of the concrete ConditionType Entity
        super(ConditionType.class);
    }

    /**
     * Sets the "items" attribute with a collection of Has entities that are
     * retrieved from ConditionType?cap_first and returns the navigation
     * outcome.
     *
     * @return navigation outcome for Has page
     */
    public String navigateHasList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Has_items", this.getSelected().getHasList());
        }
        return "/admin/has/index";
    }

}
