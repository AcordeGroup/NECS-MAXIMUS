package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Manage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "manageController")
@ViewScoped
public class ManageController extends AbstractController<Manage> {

    @Inject
    private AgentController agentController;
    @Inject
    private QuoteController quoteController;
    @Inject
    private MobilePageController mobilePageController;

    public ManageController() {
        // Inform the Abstract parent controller of the concrete Manage Entity
        super(Manage.class);
    }

//    @Override
//    protected void setEmbeddableKeys() {
//        this.getSelected().getManagePK().setIdQuote(this.getSelected().getQuote().getIdQuote());
//        this.getSelected().getManagePK().setIdAgent(this.getSelected().getAgent().getIdAgent());
//    }
//
//    @Override
//    protected void initializeEmbeddableKey() {
//        this.getSelected().setManagePK(new com.necs.maximus.db.entity.ManagePK());
//    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        agentController.setSelected(null);
        quoteController.setSelected(null);
    }

    /**
     * Sets the "selected" attribute of the Agent controller in order to display
     * its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareAgent(ActionEvent event) {
        if (this.getSelected() != null && agentController.getSelected() == null) {
            agentController.setSelected(this.getSelected().getIdAgent());
        }
    }

    /**
     * Sets the "selected" attribute of the Quote controller in order to display
     * its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareQuote(ActionEvent event) {
        if (this.getSelected() != null && quoteController.getSelected() == null) {
            quoteController.setSelected(this.getSelected().getIdQuote());
        }
    }
}
