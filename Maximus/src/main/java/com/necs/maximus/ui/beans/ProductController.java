package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Product;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "productController")
@ViewScoped
public class ProductController extends AbstractController<Product> {

    @Inject
    private MobilePageController mobilePageController;

    private boolean createPart;
    private boolean createPartSustitute;

    public ProductController() {
        // Inform the Abstract parent controller of the concrete Product Entity
            super(Product.class);
    }

    /**
     * Sets the "items" attribute with a collection of IsSubstitute entities
     * that are retrieved from Product?cap_first and returns the navigation
     * outcome.
     *
     * @return navigation outcome for IsSubstitute page
     */
    public String navigateIsSubstituteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("IsSubstitute_items", this.getSelected().getIsSubstituteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/isSubstitute/index";
    }

    /**
     * Sets the "items" attribute with a collection of Has entities that are
     * retrieved from Product?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Has page
     */
    public String navigateHasList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Has_items", this.getSelected().getHasList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/has/index";
    }

    /**
     * Sets the "items" attribute with a collection of ProductCost entities that
     * are retrieved from Product?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for ProductCost page
     */
    public String navigateProductCostList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("ProductCost_items", this.getSelected().getProductCostList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/productCost/index";
    }

    public void createPartTrue() {
        createPart = true;
    }
    
    public void createPartSustituteTrue() {
        createPartSustitute = true;
    }

    public MobilePageController getMobilePageController() {
        return mobilePageController;
    }

    public void setMobilePageController(MobilePageController mobilePageController) {
        this.mobilePageController = mobilePageController;
    }

    public boolean isCreatePart() {
        return createPart;
    }

    public void setCreatePart(boolean createPart) {
        this.createPart = createPart;
    }

    public boolean isCreatePartSustitute() {
        return createPartSustitute;
    }

    public void setCreatePartSustitute(boolean createPartSustitute) {
        this.createPartSustitute = createPartSustitute;
    }

}
