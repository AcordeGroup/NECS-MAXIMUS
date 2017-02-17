package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.IsSubstitute;
import com.necs.maximus.db.entity.Product;
import com.necs.maximus.db.facade.ProductFacade;
import java.util.HashMap;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;

@Named(value = "isSubstituteController")
@ViewScoped
public class IsSubstituteController extends AbstractController<IsSubstitute> {

    @Inject
    private ProductController productController;
    @Inject
    private MobilePageController mobilePageController;
    
    @Inject
    private ProductFacade ejbFacade;

    public IsSubstituteController() {
        // Inform the Abstract parent controller of the concrete IsSubstitute Entity
        super(IsSubstitute.class);
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
            productController.setSelected(this.getSelected().getPartNumberBase());
        }
    }
    
    /**
     * Metodo encargado de validar si el partnumber introducido ya se encuentra
     * en BD
     *
     * @param query
     * @return
     */
    public void validProductExist() {
        String part = getSelected().getPartNumberSubstitute().getPartNumber();
        Product partNumber;
        HashMap param = new HashMap();
        param.put("partNumber", part);
        partNumber = ejbFacade.listUniqueNamedQuery(Product.class, "Product.findByPartNumber", param);
        if (partNumber != null) {
            RequestContext.getCurrentInstance().execute("document.getElementById('ProductCreateForm:messageExisting').style.display='block';");
        }
    }
}
