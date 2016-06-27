package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Customer;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "customerController")
@ViewScoped
public class CustomerController extends AbstractController<Customer> {

    @Inject
    private CompanyController companyNameController;
    @Inject
    private MobilePageController mobilePageController;

    private boolean createSales = false;

    public CustomerController() {
        // Inform the Abstract parent controller of the concrete Customer Entity
        super(Customer.class);
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        companyNameController.setSelected(null);
    }

    /**
     * Sets the "items" attribute with a collection of Quote entities that are
     * retrieved from Customer?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Quote page
     */
    public String navigateQuoteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Quote_items", this.getSelected().getQuoteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quote/index";
    }

    /**
     * Sets the "selected" attribute of the Company controller in order to
     * display its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareCompanyName(ActionEvent event) {
        if (this.getSelected() != null && companyNameController.getSelected() == null) {
            companyNameController.setSelected(this.getSelected().getCompanyName());
        }
    }
    
    public void createSalesTrue(){
        createSales = true;
    }

    public boolean isCreateSales() {
        return createSales;
    }

    public void setCreateSales(boolean createSales) {
        this.createSales = createSales;
    }

}
