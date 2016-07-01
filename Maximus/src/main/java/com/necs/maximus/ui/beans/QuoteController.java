package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.db.entity.Manage;
import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.db.facade.AgentFacade;
import com.necs.maximus.db.facade.ManageFacade;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.enums.StatusType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
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
    @EJB
    private QuoteFacade quoteFacade;
    @EJB
    private AgentFacade agentFacade;
    @EJB
    private ManageFacade manageFacade;

    private Agent agent;

    List<Quote> quoteOpen;
    List<Quote> quoteClose;

    private List<Quote> filteredQuote;

    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    public QuoteController() {
        // Inform the Abstract parent controller of the concrete Quote Entity
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        HashMap param = new HashMap();
        param.put("idAgent", getUserManagedBean().getAgentId());
        agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
        quoteClose = new ArrayList<>();
        quoteOpen = new ArrayList<>();
        inicializedListByStatus();

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

    public void inicializedListByStatus() {
        List<String> status;
        if (agent == null) {
            HashMap param = new HashMap();
            param.put("idAgent", getUserManagedBean().getAgentId());
            agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
        }

        switch (AgentType.valueOf(getUserManagedBean().getType())) {

            case Administrator:
                status = new ArrayList<>();
                status.add(StatusType.OPEN.getName());
                status.add(StatusType.IN_PROGRESS.getName());
                quoteOpen.addAll(quoteFacade.findQuoteByListStatus(status));
                status.clear();
                status.add(StatusType.READY.getName());
                quoteClose.addAll(quoteFacade.findQuoteByListStatus(status));
                quoteClose.addAll(quoteFacade.findAllQuoteByStatus(StatusType.SENT.getName()));
//                quoteClose.addAll(quoteStatusFacade.findQuoteStatusByStatusAndAgent(StatusType.SENT.getName(), getUserManagedBean().getAgentId()));
//                quoteClose.addAll(quoteStatusFacade.findQuoteStatusByStatusAndAgent(StatusType.READY.getName(), getUserManagedBean().getAgentId()));
                break;

            case Sales:
                quoteOpen.addAll(quoteFacade.findQuoteByIdAgent(getUserManagedBean().getAgentId()));
                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.SENT.getName(), agent));
                break;
            case Purchasing:
                status = new ArrayList<>();
                status.add(StatusType.OPEN.getName());
                quoteOpen.addAll(quoteFacade.findQuoteByListStatus(status));
                quoteOpen.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.IN_PROGRESS.getName(), agent));
                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.SENT.getName(), agent));
                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.READY.getName(), agent));
                break;

        }

    }

    public String getStatusPurchasing(String status) {
        StringBuilder statusPurchasing = new StringBuilder("");
        switch (StatusType.getStatusByName(status)) {
            case OPEN:
                statusPurchasing.append(status);
                break;
            case IN_PROGRESS:
                statusPurchasing.append("Waiting for Pricing");
                break;
            case READY:
                statusPurchasing.append("Done");
                break;
            case SENT:
                statusPurchasing.append("Waiting for Customer");

            default:
                break;
        }
        return statusPurchasing.toString();
    }

    public String getColorDiv(String status, Date dateCreation) {

        StringBuilder color = new StringBuilder("");
        Calendar actual = Calendar.getInstance();

        Calendar dateCreaOneDays = Calendar.getInstance();
        dateCreaOneDays.setTime(dateCreation);

        Long dif = ((actual.getTime().getTime() - dateCreaOneDays.getTime().getTime()) / 3600000);

        switch (StatusType.getStatusByName(status)) {
            case OPEN:
                if (dif.intValue() > 24 && dif.intValue() < 48) {
                    color.append("#FEFD00");
                } else if (dif.intValue() > 48) {
                    color.append("#FE0000");
                } else if (dif.intValue() < 24) {
                    color.append("#FFF");
                }
                break;
            case IN_PROGRESS:
                if (dif.intValue() > 24 && dif.intValue() < 48) {
                    color.append("#FEFD00");
                } else if (dif.intValue() > 48) {
                    color.append("#FE0000");
                } else if (dif.intValue() < 24) {
                    color.append("#FFF");
                }
                break;
            case READY:
                color.append("#34A852");
                break;
            case SENT:
                color.append("#34A852");
                break;
            default:
                break;
        }
        return color.toString();

    }

    public void reopenQuote(Quote quote) {
        try {

            if (agent == null) {
                HashMap param = new HashMap();
                param.put("idAgent", getUserManagedBean().getAgentId());
                agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
            }
            if (quote != null) {
                QuoteStatus qs = quote.getQuoteStatusList().get(0);
                qs.setEndDate(new Date());
                quoteStatusFacade.edit(qs);

                QuoteStatus statusNew = new QuoteStatus();
                statusNew.setIdQuote(quote);
                statusNew.setInitDate(new Date());
                statusNew.setStatus(StatusType.OPEN.getName());
                quoteStatusFacade.create(statusNew);

                Manage ma = quote.getManageList().get(0);
                ma.setDeallocationDate(new Date());
                manageFacade.edit(ma);
                init();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        }
    }

    public boolean isQuoteAdmin(Quote quote) {
        if (agent == null) {
            HashMap param = new HashMap();
            param.put("idAgent", getUserManagedBean().getAgentId());
            agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
        }
        List<Quote> list = quoteFacade.findQuoteByStatusAndAgent(StatusType.IN_PROGRESS.getName(), agent);
        for (Quote q : list) {
            if (q.getIdQuote().equals(quote.getIdQuote())) {
                return true;
            }
        }
        return false;
    }

    public List<Quote> getFilteredQuote() {
        return filteredQuote;
    }

    public void setFilteredQuote(List<Quote> filteredQuote) {
        this.filteredQuote = filteredQuote;
    }

    public List<Quote> getQuoteOpen() {
        return quoteOpen;
    }

    public void setQuoteOpen(List<Quote> quoteOpen) {
        this.quoteOpen = quoteOpen;
    }

    public List<Quote> getQuoteClose() {
        return quoteClose;
    }

    public void setQuoteClose(List<Quote> quoteClose) {
        this.quoteClose = quoteClose;
    }

}
