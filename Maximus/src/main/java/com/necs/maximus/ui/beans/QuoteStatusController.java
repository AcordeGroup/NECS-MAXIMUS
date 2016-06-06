package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.QuoteStatus;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "quoteStatusController")
@ViewScoped
public class QuoteStatusController extends AbstractController<QuoteStatus> {

    @Inject
    private QuoteController idQuoteController;
    @Inject
    private MobilePageController mobilePageController;

    public QuoteStatusController() {
        // Inform the Abstract parent controller of the concrete QuoteStatus Entity
        super(QuoteStatus.class);
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        idQuoteController.setSelected(null);
    }

    /**
     * Sets the "selected" attribute of the Quote controller in order to display
     * its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareIdQuote(ActionEvent event) {
        if (this.getSelected() != null && idQuoteController.getSelected() == null) {
            idQuoteController.setSelected(this.getSelected().getIdQuote());
        }
    }
}
