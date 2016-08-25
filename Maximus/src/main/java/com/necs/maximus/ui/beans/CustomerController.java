package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Customer;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "customerController")
@ViewScoped
public class CustomerController extends AbstractController<Customer> {

    @Inject
    private MobilePageController mobilePageController;

    public CustomerController() {
        // Inform the Abstract parent controller of the concrete Customer Entity
        super(Customer.class);
    }

    /**
     * Sets the "items" attribute with a collection of Contact entities that
 are retrieved from Customer?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Contact page
     */
    public String navigateContactList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Contact_items", this.getSelected().getContactList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/contact/index";
    }

}
