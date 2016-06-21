package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.db.facade.LazyEntityDataModel;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.StatusType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named(value = "quoteController")
@ViewScoped
public class QuoteController extends AbstractController<Quote> {

    @Inject
    private AgentController idAgentController;
    @Inject
    private CustomerController idCustomerController;
    @Inject
    private MobilePageController mobilePageController;

    @EJB
    private QuoteStatusFacade quoteStatusFacade;

    List<QuoteStatus> quoteOpen;
    List<QuoteStatus> quoteClose;

    private List<Quote> filteredQuote;

    public QuoteController() {
        // Inform the Abstract parent controller of the concrete Quote Entity
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        quoteClose = new ArrayList<>();
        quoteOpen = new ArrayList<>();
        inicializedListByStatus(quoteStatusFacade.findQuoteByStatusActual());

    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        idAgentController.setSelected(null);
        idCustomerController.setSelected(null);
    }

    /**
     * Sets the "selected" attribute of the Agent controller in order to display
     * its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareIdAgent(ActionEvent event) {
        if (this.getSelected() != null && idAgentController.getSelected() == null) {
            idAgentController.setSelected(this.getSelected().getIdAgent());
        }
    }

    /**
     * Sets the "selected" attribute of the Customer controller in order to
     * display its data in its View dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareIdCustomer(ActionEvent event) {
        if (this.getSelected() != null && idCustomerController.getSelected() == null) {
            idCustomerController.setSelected(this.getSelected().getIdCustomer());
        }
    }

    /**
     * Sets the "items" attribute with a collection of Has entities that are
     * retrieved from Quote?cap_first and returns the navigation outcome.
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
     * Sets the "items" attribute with a collection of QuoteNote entities that
     * are retrieved from Quote?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for QuoteNote page
     */
    public String navigateQuoteNoteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("QuoteNote_items", this.getSelected().getQuoteNoteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quoteNote/index";
    }

    /**
     * Sets the "items" attribute with a collection of QuoteStatus entities that
     * are retrieved from Quote?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for QuoteStatus page
     */
    public String navigateQuoteStatusList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("QuoteStatus_items", this.getSelected().getQuoteStatusList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quoteStatus/index";
    }

    /**
     * Sets the "items" attribute with a collection of Manage entities that are
     * retrieved from Quote?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Manage page
     */
    public String navigateManageList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Manage_items", this.getSelected().getManageList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/manage/index";
    }

    public void inicializedListByStatus(List<QuoteStatus> list) {

        for (QuoteStatus quoteStatus : list) {
            if (quoteStatus.getStatus().equals(StatusType.SENT.getName())) {
                quoteClose.add(quoteStatus);
            } else {
                quoteOpen.add(quoteStatus);
            }
        }
    }

    public List<Quote> getFilteredQuote() {
        return filteredQuote;
    }

    public void setFilteredQuote(List<Quote> filteredQuote) {
        this.filteredQuote = filteredQuote;
    }

    public List<QuoteStatus> getQuoteOpen() {
        return quoteOpen;
    }

    public void setQuoteOpen(List<QuoteStatus> quoteOpen) {
        this.quoteOpen = quoteOpen;
    }

    public List<QuoteStatus> getQuoteClose() {
        return quoteClose;
    }

    public void setQuoteClose(List<QuoteStatus> quoteClose) {
        this.quoteClose = quoteClose;
    }
    
    

}
