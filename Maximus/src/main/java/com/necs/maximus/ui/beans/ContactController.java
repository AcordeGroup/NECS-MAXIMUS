package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Contact;
import com.necs.maximus.enums.ContactType;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "contactController")
@ViewScoped
public class ContactController extends AbstractController<Contact> {

    @Inject
    private CustomerController companyNameController;
    @Inject
    private MobilePageController mobilePageController;

    private List<String> types;

    private boolean createContact = false;

    public ContactController() {
        // Inform the Abstract parent controller of the concrete Contact Entity
        super(Contact.class);
    }

    @PostConstruct
    public void init() {
        types = ContactType.getListValues();
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        companyNameController.setSelected(null);
    }

    /**
     * Sets the "items" attribute with a collection of Quote entities that are
     * retrieved from Contact?cap_first and returns the navigation outcome.
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
     * Sets the "selected" attribute of the Customer controller in order to
     * display its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareCustomerName(ActionEvent event) {
        if (this.getSelected() != null && companyNameController.getSelected() == null) {
            companyNameController.setSelected(this.getSelected().getCompanyName());
        }
    }

    public void createContactTrue() {
        createContact = true;
    }

    public boolean isCreateContact() {
        return createContact;
    }

    public void setCreateContact(boolean createContact) {
        this.createContact = createContact;
    }

   

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
