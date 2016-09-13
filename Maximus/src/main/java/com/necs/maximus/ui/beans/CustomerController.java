package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Customer;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;

@Named(value = "customerController")
@ViewScoped
public class CustomerController extends AbstractController<Customer> {

    @Inject
    private MobilePageController mobilePageController;
    @Inject
    private ContactController contactController;

    private boolean createCustomer = false;

    private String formUpdated;

    private List<Customer> customerList;

    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    public CustomerController() {
        // Inform the Abstract parent controller of the concrete Customer Entity
        super(Customer.class);
    }

    /**
     * Sets the "items" attribute with a collection of Contact entities that are
     * retrieved from Customer?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Contact page
     */
    public String navigateContactList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Contact_items", this.getSelected().getContactList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/contact/index";
    }

    public void createCustomerTrue(String formUpdateParam) {
        formUpdated = formUpdateParam;
        createCustomer = true;
    }

    public boolean isCreateCustomer() {
        return createCustomer;
    }

    public void setCreateCustomer(boolean createCustomer) {
        this.createCustomer = createCustomer;
    }

    public void prepareCreateCustomerContact() {
        contactController.prepareCreate(null);
        this.prepareCreate(null);
    }

    public void saveCustomerContact() {
        try {
            this.saveNew(null);
            if (contactController.getSelected() != null) {
                contactController.getSelected().setCompanyName(this.getSelected());
                contactController.saveNew(null);
            }
        } catch (Exception e) {
            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("error_persist")));
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean checkMatches(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }

        String carName = value.toString().toUpperCase();
        filterText = filterText.toUpperCase();

        if (carName.contains(filterText)) {
            return true;
        } else {
            return false;
        }
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public String getFormUpdated() {
        return formUpdated;
    }

    public void setFormUpdated(String formUpdated) {
        this.formUpdated = formUpdated;
    }

}
