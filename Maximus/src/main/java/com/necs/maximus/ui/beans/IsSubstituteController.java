package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.IsSubstitute;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "isSubstituteController")
@ViewScoped
public class IsSubstituteController extends AbstractController<IsSubstitute> {

    @Inject
    private ProductController productController;
    @Inject
    private MobilePageController mobilePageController;

    public IsSubstituteController() {
        // Inform the Abstract parent controller of the concrete IsSubstitute Entity
        super(IsSubstitute.class);
    }

    @Override
    protected void setEmbeddableKeys() {
        this.getSelected().getIsSubstitutePK().setPartNumber1(this.getSelected().getProduct().getPartNumber());
    }

    @Override
    protected void initializeEmbeddableKey() {
        this.getSelected().setIsSubstitutePK(new com.necs.maximus.db.entity.IsSubstitutePK());
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        productController.setSelected(null);
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
}
