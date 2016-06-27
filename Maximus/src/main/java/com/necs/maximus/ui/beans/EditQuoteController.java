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
import com.necs.maximus.db.facade.CustomerFacade;
import com.necs.maximus.db.facade.HasFacade;
import com.necs.maximus.db.facade.ProductFacade;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteNoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.ShippingCostType;
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
@Named(value = "editQuoteController")
@ViewScoped
public class EditQuoteController extends AbstractController<Quote> {

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
    private HasFacade hasFacade;

    private Agent agent;

    private String note;
    private String includeShipping;
    private Integer totalPriceCot;
    private String nroPart;
    private String typePart;
    private String manufacturePart;
    private String descriptionPart;
    private int qty;
    private Double targetPrice;
    private Double shippingCost;
    private Double suggestedSalesPrice;

    private List<Customer> customerList;
    private List<Has> partListHas;
    private List<Product> partList;
    private List<Product> selectedPart;
    private List<QuoteNote> quoteListNote;

    private Quote quote;

    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    public EditQuoteController() {
        // Inform the Abstract parent controller of the concrete Quote Entity
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        partListHas = new ArrayList<>();
        customerList = (List<Customer>) customerFacade.findAll();
        String quoteId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idQuote");
        if (quoteId != null) {
            quote = quoteFacade.findQuoteByIdQuoteAndStatusActual(Integer.parseInt(quoteId));
            if (null != quote) {
                quoteListNote = quoteNoteFacade.findQuoteNoteByIdQuote(quote);
                includeShipping = ShippingCostType.getEnumByIdType(quote.getInclude_shipping_cost()).getType();
                partListHas.addAll(quote.getHasList());
            }
        }
    }

    public String editRequest() {
        try {
            if (validateField()) {
               
                if(note !=null && !note.equals("")){
                      // create nota entity
                    QuoteNote nota = new QuoteNote();
                    nota.setCreationDate(new Date());
                    nota.setIdQuote(quote);
                    nota.setNote(note);
                    quoteNoteFacade.create(nota);
                }
                
                //remove list has existent
                for (Has h : quote.getHasList()) {
                    hasFacade.remove(h);
                }
                
                //add new list has in the quote
                  for (Has hasNew : partListHas) {
                    hasNew.setHasPK(new HasPK(quote.getIdQuote(), hasNew.getProduct().getPartNumber()));
                    hasNew.setQuote(quote);
                    hasNew.setCustomerTargetPrice(hasNew.getProduct().getPrice());
                    hasNew.setSuggestedSalesPrice(hasNew.getSuggestedSalesPrice());
                    //h.setQtyFound(BigDecimal.ZERO);

                    hasFacade.create(hasNew);
                }
                  
                 quoteFacade.edit(quote);

                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("update_success_quote"), ""));
                return "index";
            }
        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("error_update"), ""));
            return "";
        }
        return "";
    }

    public boolean validateField() {
        if (quote.getIdCustomer() == null || quote.getIdCustomer().equals(bundle.getString("SelectOneMessage"))) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("message_customer"), ""));
            return false;
        }
        if (quote.getShipping_to() == null || quote.getShipping_to().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_address_not_null"), ""));
            return false;
        }

        if (quote.getContact() == null || quote.getContact().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_contact_not_null"), ""));
            return false;
        }

        if (quote.getEmail() == null || quote.getEmail().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_email_not_null"), ""));
            return false;
        }

        if (partListHas == null || partListHas.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("add_part_quote"), ""));
            return false;
        }

        return true;
    }

    public void searchPart() {
        HashMap<String, String> parametros = new HashMap<>();

        if (nroPart != null && !nroPart.equals("")) {
            parametros.put("partNumber", nroPart);
        }

        if (typePart != null && !typePart.equals("")) {
            parametros.put("type", typePart);
        }

        if (manufacturePart != null && !manufacturePart.equals("")) {
            parametros.put("manufacture", manufacturePart);
        }

        if (descriptionPart != null && !descriptionPart.equals("")) {
            parametros.put("description", descriptionPart);
        }

        partList = productFacade.findAllByFilter(parametros);
    }

    public void addPart() {
        if (partListHas == null) {
            partListHas = new ArrayList<>();
        }

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
        typePart = "";
        descriptionPart = "";
        nroPart = "";
        manufacturePart = "";
    }
    
      public void removePart(Has parte) {
        for (Has h : partListHas) {
            if (h.equals(parte)) {
                partListHas.remove(parte);
                break;
            }
        }

    }

    public void showTextArea() {
        RequestContext.getCurrentInstance().execute("document.getElementById('form:panelTextArea').style.display='block';");
    }
    
     public String getTypeAgent() {
        return getUserManagedBean().getType();
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
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

    public Double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Double getSuggestedSalesPrice() {
        return suggestedSalesPrice;
    }

    public void setSuggestedSalesPrice(Double suggestedSalesPrice) {
        this.suggestedSalesPrice = suggestedSalesPrice;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public List<QuoteNote> getQuoteListNote() {
        return quoteListNote;
    }

    public void setQuoteListNote(List<QuoteNote> quoteListNote) {
        this.quoteListNote = quoteListNote;
    }

}
