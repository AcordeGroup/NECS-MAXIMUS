package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.ProductCost;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "productCostController")
@ViewScoped
public class ProductCostController extends AbstractController<ProductCost> {

    @Inject
    private ProductController partNumberController;
    @Inject
    private MobilePageController mobilePageController;

    public ProductCostController() {
        // Inform the Abstract parent controller of the concrete ProductCost Entity
        super(ProductCost.class);
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        partNumberController.setSelected(null);
    }

    /**
     * Sets the "selected" attribute of the Product controller in order to
     * display its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void preparePartNumber(ActionEvent event) {
        if (this.getSelected() != null && partNumberController.getSelected() == null) {
            partNumberController.setSelected(this.getSelected().getPartNumber());
        }
    }
}
