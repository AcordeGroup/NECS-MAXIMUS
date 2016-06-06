package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Agent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "agentController")
@ViewScoped
public class AgentController extends AbstractController<Agent> {

    @Inject
    private MobilePageController mobilePageController;

    public AgentController() {
        // Inform the Abstract parent controller of the concrete Agent Entity
        super(Agent.class);
    }

    /**
     * Sets the "items" attribute with a collection of Quote entities that are
     * retrieved from Agent?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Quote page
     */
    public String navigateQuoteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Quote_items", this.getSelected().getQuoteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quote/index";
    }

    /**
     * Sets the "items" attribute with a collection of Manage entities that are
     * retrieved from Agent?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Manage page
     */
    public String navigateManageList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Manage_items", this.getSelected().getManageList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/manage/index";
    }

}
