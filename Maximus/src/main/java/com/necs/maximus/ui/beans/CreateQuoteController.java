/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.db.entity.Customer;
import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.HasPK;
import com.necs.maximus.db.entity.Product;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteNote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.db.facade.AgentFacade;
import com.necs.maximus.db.facade.CustomerFacade;
import com.necs.maximus.db.facade.HasFacade;
import com.necs.maximus.db.facade.ProductFacade;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteNoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.StatusType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author luis
 */
@Named(value = "createQuoteController")
@ViewScoped
public class CreateQuoteController extends AbstractController<Quote> {

    @EJB
    private QuoteNoteFacade quoteNoteFacade;
    @EJB
    private QuoteStatusFacade quoteStatusFacade;
    @EJB
    private QuoteFacade quoteFacade;
    @EJB
    private CustomerFacade customerFacade;
    @EJB
    private ProductFacade productFacade;
    @EJB
    private AgentFacade agentFacade;
    @EJB
    private HasFacade hasFacade;

    private Customer customerSelected;
    private Agent agent;

    private String shippingTo;
    private String note;
    private String includeShipping;
    private Integer totalPriceCot;
    private String condition;
    private String nroPart;
    private String typePart;
    private String manufacturePart;
    private String descriptionPart;
    private int qty;
    private Double targetPrice;

    private List<Customer> customerList;
    private List<Has> partListHas;
    private List<Product> partList;
    private List<Product> selectedPart;

    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    public CreateQuoteController() {
        // Inform the Abstract parent controller of the concrete Quote Entity
        super(Quote.class);
        // cust = (List<Quote>) getItems();
    }

    @PostConstruct
    public void init() {
        customerList = (List<Customer>) customerFacade.findAll();
    }

    public void createNewRequest() {

        try {
            if (validateField()) {
                HashMap param = new HashMap();
                param.put("idAgent", getUserManagedBean().getAgentId());
                agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);

                // create quote entity
                Quote newQuote = new Quote();
                newQuote.setCreationDate(new Date());
                newQuote.setIdAgent(agent);
                newQuote.setIdCustomer(customerSelected);
                newQuote.setShipping_cost(totalPriceCot == null ? 0 : totalPriceCot);
                newQuote.setShipping_to(shippingTo);
                newQuote.setContact(customerSelected.getPrimaryContact());
                newQuote.setEmail(customerSelected.getPrimaryEmail());

                quoteFacade.create(newQuote);

                // create nota entity
                QuoteNote nota = new QuoteNote();
                nota.setCreationDate(new Date());
                nota.setIdQuote(newQuote);
                nota.setNote(note);

                quoteNoteFacade.create(nota);

                // create quote status entity
                QuoteStatus status = new QuoteStatus();
                status.setIdQuote(newQuote);
                status.setInitDate(new Date());
                status.setStatus(StatusType.OPEN.getName());

                quoteStatusFacade.create(status);

                for (Has h : partListHas) {
                    h.setHasPK(new HasPK(newQuote.getIdQuote(), h.getProduct().getPartNumber()));
                    h.setQuote(newQuote);
                    h.setCustomerTargetPrice(h.getProduct().getPrice());
                    //h.setSuggestedSalesPrice(BigDecimal.ZERO);
                    //h.setQtyFound(BigDecimal.ZERO);

                    hasFacade.create(h);
                }

                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("save_success_quote"), ""));

            }
        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("error_save"), ""));
        }

    }

    public boolean validateField() {
        if (customerSelected == null || customerSelected.equals(bundle.getString("SelectOneMessage"))) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("message_customer"), ""));
            return false;
        }
        if (shippingTo == null || shippingTo.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_address_not_null"), ""));
            return false;
        }
        return true;
    }

    public void searchPart() {
        HashMap<String, String> parametros = new HashMap<>();

        if (nroPart != null && !nroPart.isEmpty()) {
            parametros.put("partNumber", nroPart);
        }

        if (typePart != null && !typePart.isEmpty()) {
            parametros.put("type", typePart);
        }

        if (manufacturePart != null && !manufacturePart.isEmpty()) {
            parametros.put("manufacture", manufacturePart);
        }

        if (descriptionPart != null && !descriptionPart.isEmpty()) {
            parametros.put("description", descriptionPart);
        }

        partList = productFacade.findAllByFilter(parametros);
        RequestContext.getCurrentInstance().execute("document.getElementById('form:dialogPart').width='100%';");
        RequestContext.getCurrentInstance().execute("document.getElementById('form:dialogPart').height='100%';");
    }

    public void addPart() {
        partListHas = new ArrayList<>();
        if (selectedPart != null && !selectedPart.isEmpty()) {
            for (Product pro : selectedPart) {
                Has object = new Has();
                object.setProduct(pro);
                partListHas.add(object);
            }
        }
    }

    public void reset() {
        partList.clear();
        typePart = null;
        descriptionPart = null;
        nroPart = null;
        manufacturePart = null;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public Customer getCustomerSelected() {
        return customerSelected;
    }

    public void setCustomerSelected(Customer customerSelected) {
        this.customerSelected = customerSelected;
    }

    public String getShippingTo() {
        return shippingTo;
    }

    public void setShippingTo(String shippingTo) {
        this.shippingTo = shippingTo;
    }

    public List<Has> getPartListHas() {
        return partListHas;
    }

    public void setPartListHas(List<Has> partListHas) {
        this.partListHas = partListHas;
    }

    public String getIncludeShipping() {
        return includeShipping;
    }

    public void setIncludeShipping(String includeShipping) {
        this.includeShipping = includeShipping;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getTotalPriceCot() {
        return totalPriceCot;
    }

    public void setTotalPriceCot(Integer totalPriceCot) {
        this.totalPriceCot = totalPriceCot;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNroPart() {
        return nroPart;
    }

    public void setNroPart(String nroPart) {
        this.nroPart = nroPart;
    }

    public String getTypePart() {
        return typePart;
    }

    public void setTypePart(String typePart) {
        this.typePart = typePart;
    }

    public String getManufacturePart() {
        return manufacturePart;
    }

    public void setManufacturePart(String manufacturePart) {
        this.manufacturePart = manufacturePart;
    }

    public String getDescriptionPart() {
        return descriptionPart;
    }

    public void setDescriptionPart(String descriptionPart) {
        this.descriptionPart = descriptionPart;
    }

    public List<Product> getPartList() {
        return partList;
    }

    public void setPartList(List<Product> partList) {
        this.partList = partList;
    }

    public List<Product> getSelectedPart() {
        return selectedPart;
    }

    public void setSelectedPart(List<Product> selectedPart) {
        this.selectedPart = selectedPart;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

}
