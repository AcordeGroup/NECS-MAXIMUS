package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Vendor;
import com.necs.maximus.ui.beans.util.MobilePageController;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named("vendorController")
@SessionScoped
public class VendorController extends AbstractController<Vendor> {

    private boolean createVendor = false;

    @Inject
    private MobilePageController mobilePageController;

    public VendorController() {
        // Inform the Abstract parent controller of the concrete Vendor Entity
        super(Vendor.class);
    }

    /**
     * Sets the "items" attribute with a collection of Has entities that are
     * retrieved from vendor?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Has page
     */
    public String navigateHasList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Has_items", this.getSelected().getHasCollection());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/has/index";
    }

    public void createVendorTrue() {
        createVendor = true;
    }

    public boolean isCreateVendor() {
        return createVendor;
    }

    public void setCreateVendor(boolean createVendor) {
        this.createVendor = createVendor;
    }

}
