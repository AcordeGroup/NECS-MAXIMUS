package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Company;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "companyController")
@ViewScoped
public class CompanyController extends AbstractController<Company> {

    @Inject
    private MobilePageController mobilePageController;

    public CompanyController() {
        // Inform the Abstract parent controller of the concrete Company Entity
        super(Company.class);
    }

    /**
     * Sets the "items" attribute with a collection of Customer entities that
     * are retrieved from Company?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Customer page
     */
    public String navigateCustomerList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Customer_items", this.getSelected().getCustomerList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/customer/index";
    }

}
