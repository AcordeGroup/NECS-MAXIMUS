/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteNote;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteNoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.ShippingCostType;
import com.necs.maximus.enums.StatusType;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author luis
 */
@Named(value = "viewQuoteController")
@ViewScoped
public class ViewQuoteController extends AbstractController<Quote> {

    @EJB
    private QuoteFacade quoteFacade;
    @EJB
    private QuoteStatusFacade quoteStatusFacade;
    @EJB
    private QuoteNoteFacade quoteNoteFacade;

    private Quote quote;
    private List<QuoteNote> quoteListNote;

    private boolean mostrarQtyFound;
    private boolean mostrarShippingCostPerItem;
    private boolean mostrarSuggestedSalesPrice;
    private int nroColumnVariable;

    public ViewQuoteController() {
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        String quoteId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idQuote");
        if (quoteId != null) {
            String status = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusQuote");
            if (status != null && status.equals(StatusType.SENT.getName())) {
                HashMap param = new HashMap();
                param.put("idQuote", Integer.parseInt(quoteId));
                quote = quoteFacade.listUniqueNamedQuery(Quote.class, "Quote.findByIdQuote", param);
            } else {
                quote = quoteFacade.findQuoteByIdQuoteAndStatusActual(Integer.parseInt(quoteId));
            }
            if (null != quote) {
                quoteListNote = quoteNoteFacade.findQuoteNoteByIdQuote(quote);
                evaluateQtyFound(quote.getHasList());
            }

        }

    }

    public void evaluateQtyFound(List<Has> hasList) {
        if (null != hasList && !hasList.isEmpty()) {
            for (Has h : hasList) {
                if (h.getQtyFound() != null && h.getQtyFound().intValue() != 0) {
                    mostrarQtyFound = true;
                }
                if (h.getShipping_cost() != null && h.getShipping_cost().intValue() != 0) {
                    mostrarShippingCostPerItem = true;
                }
                if (h.getSuggestedSalesPrice() != null && h.getSuggestedSalesPrice().intValue() != 0) {
                    mostrarSuggestedSalesPrice = true;
                }
            }
            if (mostrarQtyFound) {
                nroColumnVariable = nroColumnVariable + 1;
            }
            if (mostrarShippingCostPerItem) {
                nroColumnVariable = nroColumnVariable + 1;
            }
            if (mostrarSuggestedSalesPrice) {
                nroColumnVariable = nroColumnVariable + 1;
            }
        }

    }

    public String getTypeAgent() {
        return getUserManagedBean().getType();
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

    public String includeShipping(Integer include) {
        return ShippingCostType.getEnumByIdType(include).getType();
    }

    public boolean isMostrarQtyFound() {
        return mostrarQtyFound;
    }

    public void setMostrarQtyFound(boolean mostrarQtyFound) {
        this.mostrarQtyFound = mostrarQtyFound;
    }

    public boolean isMostrarShippingCostPerItem() {
        return mostrarShippingCostPerItem;
    }

    public void setMostrarShippingCostPerItem(boolean mostrarShippingCostPerItem) {
        this.mostrarShippingCostPerItem = mostrarShippingCostPerItem;
    }

    public boolean isMostrarSuggestedSalesPrice() {
        return mostrarSuggestedSalesPrice;
    }

    public void setMostrarSuggestedSalesPrice(boolean mostrarSuggestedSalesPrice) {
        this.mostrarSuggestedSalesPrice = mostrarSuggestedSalesPrice;
    }

    public int getNroColumnVariable() {
        return nroColumnVariable;
    }

    public void setNroColumnVariable(int nroColumnVariable) {
        this.nroColumnVariable = nroColumnVariable;
    }

}
