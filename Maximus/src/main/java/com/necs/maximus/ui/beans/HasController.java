package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Has;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "hasController")
@ViewScoped
public class HasController extends AbstractController<Has> {

    @Inject
    private ProductController productController;
    @Inject
    private QuoteController quoteController;
    @Inject
    private MobilePageController mobilePageController;

    public HasController() {
        // Inform the Abstract parent controller of the concrete Has Entity
        super(Has.class);
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        productController.setSelected(null);
        quoteController.setSelected(null);
    }

    /**
     * Sets the "selected" attribute of the Product controller in order to
     * display its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareProduct(ActionEvent event) {
        if (this.getSelected() != null && productController.getSelected() == null) {
            productController.setSelected(this.getSelected().getProduct());
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
            quoteController.setSelected(this.getSelected().getQuote());
        }
    }
}
