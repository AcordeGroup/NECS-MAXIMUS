/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteNote;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteNoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.AgentType;
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

    public ViewQuoteController() {
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        String quoteId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idQuote");
        if (quoteId != null) {

            quote = quoteFacade.findQuoteByIdQuoteAndStatusActual(Integer.parseInt(quoteId));
            if (null != quote) {
                quoteListNote = quoteNoteFacade.findQuoteNoteByIdQuote(quote);
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

}
