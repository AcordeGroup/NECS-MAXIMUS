/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.db.entity.Contact;
import com.necs.maximus.db.entity.Customer;
import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.HasPK;
import com.necs.maximus.db.entity.IsSubstitute;
import com.necs.maximus.db.entity.Manage;
import com.necs.maximus.db.entity.Product;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteNote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.db.entity.Vendor;
import com.necs.maximus.db.facade.AgentFacade;
import com.necs.maximus.db.facade.ContactFacade;
import com.necs.maximus.db.facade.CustomerFacade;
import com.necs.maximus.db.facade.HasFacade;
import com.necs.maximus.db.facade.IsSubstituteFacade;
import com.necs.maximus.db.facade.ManageFacade;
import com.necs.maximus.db.facade.ProductFacade;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteNoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.db.facade.VendorFacade;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.enums.OperationType;
import com.necs.maximus.enums.ShippingCostType;
import com.necs.maximus.enums.StatusType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author luis
 */
@Named(value = "editQuoteController")
@ViewScoped
public class EditQuoteController extends AbstractController<Quote> {

    @Inject
    private ProductController proController;
    @Inject
    private QuoteController quoteController;
    @EJB
    private QuoteNoteFacade quoteNoteFacade;
    @EJB
    private QuoteStatusFacade quoteStatusFacade;
    @EJB
    private QuoteFacade quoteFacade;
    @EJB
    private CustomerFacade customerFacade;
    @EJB
    private ContactFacade contactFacade;
    @EJB
    private VendorFacade vendorFacade;
    @EJB
    private ProductFacade productFacade;
    @EJB
    private HasFacade hasFacade;
    @EJB
    private AgentFacade agentFacade;
    @EJB
    private ManageFacade manageFacade;
    @EJB
    private IsSubstituteFacade isSubstituteFacade;

    private Agent agent;
    private Product nroPart;
    private Product selectedPartSubtitute;
    private Has productGeneric;
    private Has productReplace;
    private Product productCreado;
    private Quote quote;
    private Vendor vendorSelected;
    private Contact contactSelected;
    private Customer customerSelected;

    private String note;
    private String includeShipping;
    private Integer totalPriceCot;
    private String typePart;
    private String manufacturePart;
    private String descriptionPart;
    private int qty;
    private Double targetPrice;
    private Double shippingCost;
    private Double suggestedSalesPrice;
    private String selectionSustitute;
    private boolean makeSubstitute;

    private List<Customer> customerList;
    private List<Contact> contactList;
    private List<Has> partListHas;
    private List<Product> partList;
    private List<Product> selectedPart;
    private List<QuoteNote> quoteListNote;
    private List<Vendor> vendorList;

    private static final String PRODUCT_GENERIC = "GENERIC";
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    public EditQuoteController() {
        // Inform the Abstract parent controller of the concrete Quote Entity
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        HashMap param = new HashMap();
        param.put("idAgent", getUserManagedBean().getAgentId());
        partListHas = new ArrayList<>();
        customerList = (List<Customer>) customerFacade.findAll();
        Collections.sort(customerList, new Comparator<Customer>() {

            @Override
            public int compare(Customer t, Customer t1) {
                return t.getCompanyName().compareTo(t1.getCompanyName());
            }
        });
        vendorList = (List<Vendor>) vendorFacade.findAll();
        agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);

        String quoteId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idQuote");
        if (quoteId != null) {
            quote = quoteFacade.findQuoteByIdQuoteAndStatusActual(Integer.parseInt(quoteId));
            if (null != quote) {
                contactSelected = quote.getIdContact();
                customerSelected = quote.getIdContact().getCompanyName();
                contactList = quote.getIdContact().getCompanyName().getContactList();//contactFacade.findContactsByCompanyName(quote.getIdContact().getCompanyName().getCompanyName());
                quoteListNote = quoteNoteFacade.findQuoteNoteByIdQuote(quote);
                includeShipping = ShippingCostType.getEnumByIdType(quote.getIncludeShippingCost()).getType();
                partListHas.addAll(quote.getHasList());
                if (!getUserManagedBean().getType().equals(AgentType.Sales.getType())) {
                    changeStatusQuote(quote);
                }
            }
            quote = quoteFacade.findQuoteByIdQuoteAndStatusActual(Integer.parseInt(quoteId));
        }
    }

    public String editRequest(String operation) {
        try {
            if (validateField()) {
                if (agent == null) {
                    HashMap param = new HashMap();
                    param.put("idAgent", getUserManagedBean().getAgentId());
                    agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
                }

                //remove list has existent
                for (Has h : quote.getHasList()) {
                    hasFacade.remove(h);
                }
                //remove list has existent in entity
                quote.getHasList().clear();

                //add new list has in the quote
                for (Has hasNew : partListHas) {
                    hasNew.setHasPK(new HasPK(quote.getIdQuote(), hasNew.getProduct().getPartNumber()));
                    hasNew.setQuote(quote);
                    //hasNew.setCustomerTargetPrice(hasNew.getProduct().getPrice());
                    //hasNew.setSuggestedSalesPrice(hasNew.getSuggestedSalesPrice());
                    if (hasNew.getIdVendor() == null) {
                        hasNew.setIdVendor(null);
                    }
                    //h.setQtyFound(BigDecimal.ZERO);

                    hasFacade.create(hasNew);
                    quote.getHasList().add(hasNew);
                }
                quote.setIncludeShippingCost(ShippingCostType.getEnumByType(includeShipping).getIdType());
                quote.setContact(contactSelected.getPrimaryContact());
                quote.setEmail(contactSelected.getPrimaryEmail());
                quote.setIdContact(contactSelected);
                quoteFacade.edit(quote);

                if (operation.equals(OperationType.DONE.getOperationName())) {
                    QuoteStatus qs = quote.getQuoteStatusList().get(0);
                    qs.setEndDate(new Date());
                    quoteStatusFacade.edit(qs);

                    QuoteStatus statusNew = new QuoteStatus();
                    statusNew.setIdQuote(quote);
                    statusNew.setInitDate(new Date());

                    statusNew.setStatus(StatusType.READY_AND_SENT.getName());
                    quoteStatusFacade.create(statusNew);

                    // envio notificacion al sales
                    quoteController.sendQuote(quote);
                    RequestContext.getCurrentInstance().execute("PF('dialogSuccess').show();");
                }

                if (note != null && !note.equals("")) {
                    // create nota entity
                    QuoteNote nota = new QuoteNote();
                    nota.setCreationDate(new Date());
                    nota.setIdQuote(quote);
                    nota.setNote(note);
                    nota.setIdAgent(agent);
                    quoteNoteFacade.create(nota);
                }

                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("update_success_quote"), ""));
                if (!operation.equals(OperationType.DONE.getOperationName())) {
                    return getUserManagedBean().getType();
                } else {
                    return "";
                }

            }
        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("error_update"), ""));
            return "";
        }
        return "";
    }

    public boolean validateField() {
        if (customerSelected == null || customerSelected.getCompanyName().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("message_customer"), ""));
            return false;
        }
        if (contactSelected == null || contactSelected.equals(bundle.getString("SelectOneMessage"))) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("message_contact")));
            return false;
        }
        if (contactSelected.getPrimaryContact() == null || contactSelected.getPrimaryContact().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_contact_not_null"), ""));
            return false;
        }
        if (contactSelected.getPrimaryEmail() == null || contactSelected.getPrimaryEmail().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_email_not_null"), ""));
            return false;
        }
        if (quote.getShipping_to() == null || quote.getShipping_to().equals("")) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_address_not_null"), ""));
            return false;
        }

//        if (quote.getContact() == null || quote.getContact().equals("")) {
//            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_contact_not_null"), ""));
//            return false;
//        }
//
//        if (quote.getEmail() == null || quote.getEmail().equals("")) {
//            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("shipping_email_not_null"), ""));
//            return false;
//        }
        if (partListHas == null || partListHas.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("add_part_quote"), ""));
            return false;
        } else {
            for (Has h : partListHas) {
                if (h.getProduct().getType().toUpperCase().equals(PRODUCT_GENERIC) && !typeAgent().equals("Sales")) {
                    FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("change_generic_part"), ""));
                    return false;
                }
            }

        }

        return true;
    }

    public void searchPart() {
        HashMap<String, String> parametros = new HashMap<>();

        if (nroPart != null) {
            parametros.put("partNumber", nroPart.getPartNumber());
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
        boolean genericSelected = false;

        if (partListHas == null) {
            partListHas = new ArrayList<>();
        }

        if (selectedPart == null || selectedPart.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("formDialog:messagesDialog", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("add_part_quote_error")));
        } else {
            for (Product pro : selectedPart) {
                if (pro.getType().toUpperCase().equals(PRODUCT_GENERIC)) {
                    genericSelected = true;
                    break;
                }
            }

            if (genericSelected) {
                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("generic_product_select")));

            } else {

                List<Has> auxPartHas = new ArrayList<>();
                Collection<Product> listWithoutDuplicates = new HashSet<>(selectedPart);
                for (Product pro : listWithoutDuplicates) {
                    boolean productExist = false;
                    if (partListHas != null && !partListHas.isEmpty()) {
                        for (Has h : partListHas) {
                            if (h.getProduct().getPartNumber().equals(pro.getPartNumber())) {
                                productExist = true;
                                break;
                            }
                        }
                        if (!productExist) {
                            Has object = new Has();
                            object.setProduct(pro);
                            auxPartHas.add(object);
                        }
                    } else {
                        Has object = new Has();
                        object.setProduct(pro);
                        auxPartHas.add(object);
                    }
                }
                partListHas.addAll(auxPartHas);

                RequestContext.getCurrentInstance().update("form:datalistProduct");
                RequestContext.getCurrentInstance().execute("PF('dialogPart').hide();");
                reset();

            }

        }
    }

    public void replacePart() {
        boolean genericSelected = false;

        if (partListHas == null) {
            partListHas = new ArrayList<>();
        }

        if (selectedPartSubtitute == null) {
            FacesContext.getCurrentInstance().addMessage("formDialog:messagesDialog", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("replace_part_quote_error")));
        } else {

            if (selectedPartSubtitute.getType().toUpperCase().equals(PRODUCT_GENERIC)) {
                genericSelected = true;

            }

            if (genericSelected) {
                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("generic_product_select")));

            } else {

                boolean productExist = false;
                if (partListHas != null && !partListHas.isEmpty()) {
                    for (Has h : partListHas) {
                        if (h.getProduct().getPartNumber().equals(selectedPartSubtitute.getPartNumber())) {
                            productExist = true;
                            break;
                        }
                    }
                    if (productExist) {
                        RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("product_exist")));
                    } else {
                        if (productGeneric != null) {
                            RequestContext.getCurrentInstance().execute("PF('dialogPartConfirm').show();");
                        } else if (productReplace != null) {
                            RequestContext.getCurrentInstance().execute("PF('dialogPartReplaceConfirm').show();");
                        }

                    }
                } else {
                    if (productGeneric != null) {
                        RequestContext.getCurrentInstance().execute("PF('dialogPartConfirm').show();");
                    } else if (productReplace != null) {
                        RequestContext.getCurrentInstance().execute("PF('dialogPartReplaceConfirm').show();");
                    }

                }
            }
        }
    }

    public void AddAsNotePart() {
        boolean warning = false;
        if (partListHas == null) {
            partListHas = new ArrayList<>();
        }

        if (selectedPartSubtitute == null) {
            FacesContext.getCurrentInstance().addMessage("formDialog:messagesDialog", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("add_part_quote_error_note")));
        } else {

            StringBuffer nota = new StringBuffer();

            nota.append("It was found the below substitute for the product : ").append(productReplace != null ? productReplace.getProduct().getPartNumber() : "").append("\n\n\n").
                    append("Part : ").append(selectedPartSubtitute.getPartNumber()).append("\n").
                    append("Descaription : ").append(selectedPartSubtitute.getDescription()).append("\n").
                    append("Type: ").append(selectedPartSubtitute.getType()).append("\n").
                    append("Price: ").append(selectedPartSubtitute.getPrice());

            note = nota.toString();

            if (makeSubstitute) {
                IsSubstitute sustitute;
                if (null != selectedPartSubtitute) {
                    sustitute = isSubstituteFacade.findIsSubstituteByProductBaseAndSubstitute(productReplace.getProduct(), selectedPartSubtitute);
                    if (sustitute != null) {
                        RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "the selected product is already substitute " + productReplace.getProduct().getPartNumber()));
                        warning = true;
                    } else {
                        sustitute = new IsSubstitute();
                        sustitute.setPartNumberBase(productReplace.getProduct());
                        sustitute.setPartNumberSubstitute(selectedPartSubtitute);
                        isSubstituteFacade.create(sustitute);
                    }
                }
            }

            if (!warning) {
                RequestContext.getCurrentInstance().execute("PF('dialogSubstitute').hide();");
                RequestContext.getCurrentInstance().update("form:panelTextArea");

                showTextArea();
                inicializedObject();
            }
        }

    }

    public void sustituirProduct(String operation) {
        Has object = new Has();
        if (partListHas != null && !partListHas.isEmpty()) {

            if (productGeneric != null) {
                object.setCondition(productGeneric.getCondition());
                object.setCustomerTargetPrice(productGeneric.getCustomerTargetPrice());
                object.setSuggestedSalesPrice(productGeneric.getSuggestedSalesPrice());
                object.setQtyFound(productGeneric.getQtyFound());
                object.setShipping_cost(productGeneric.getShipping_cost());
                object.setExtended(productGeneric.getExtended());
                object.setQtyRequested(productGeneric.getQtyRequested());
                for (Has h : partListHas) {
                    if (h.getProduct().getType().toUpperCase().equals(PRODUCT_GENERIC)) {
                        partListHas.remove(h);
                        break;
                    }
                }
            } else if (productReplace != null) {
                object.setCondition(productReplace.getCondition());
                object.setCustomerTargetPrice(productReplace.getCustomerTargetPrice());
                object.setSuggestedSalesPrice(productReplace.getSuggestedSalesPrice());
                object.setQtyFound(productReplace.getQtyFound());
                object.setShipping_cost(productReplace.getShipping_cost());
                object.setExtended(productReplace.getExtended());
                object.setQtyRequested(productReplace.getQtyRequested());
                for (Has h : partListHas) {
                    if (h.getProduct().getPartNumber().equals(productReplace.getProduct().getPartNumber())) {
                        partListHas.remove(h);
                        break;
                    }
                }
            }

            if (operation.equals(OperationType.SUSTITUIR.getOperationName())) {
                if (selectedPartSubtitute != null) {
                    object.setProduct(selectedPartSubtitute);
                    partListHas.add(object);
                }
            }
            if (operation.equals(OperationType.CREAR.getOperationName())) {
                if (productCreado != null) {
                    object.setProduct(productCreado);
                    partListHas.add(object);
                }

                RequestContext.getCurrentInstance().execute("PF('dialogPartConfirmCreate').hide();");
                RequestContext.getCurrentInstance().execute("PF('ProductCreateDialog').hide();");
                RequestContext.getCurrentInstance().execute("PF('dialogProccessGeneric').hide();");

            }

            if (makeSubstitute) {
                if (null != selectedPartSubtitute) {
                    IsSubstitute sustitute = new IsSubstitute();
                    sustitute.setPartNumberBase(productReplace.getProduct());
                    sustitute.setPartNumberSubstitute(selectedPartSubtitute);
                    isSubstituteFacade.create(sustitute);
                }
            }
        }
        inicializedObject();
    }

    public void reset() {
        partList = null;
        typePart = "";
        descriptionPart = "";
        nroPart = null;
        manufacturePart = "";
        selectedPart = null;
    }

    public void removePart(Has parte) {
        for (Has h : partListHas) {
            if (h.equals(parte)) {
                partListHas.remove(parte);
                break;
            }
        }
    }

    public void addExtended(Has item) {
        if (item.getQtyRequested() != null && item.getCustomerTargetPrice() != null) {
            item.setExtended(item.getCustomerTargetPrice().multiply(new BigDecimal(item.getQtyRequested())));
        }
    }

    public void changeStatusQuote(Quote quote) {
        try {

            if (agent == null) {
                HashMap param = new HashMap();
                param.put("idAgent", getUserManagedBean().getAgentId());
                agent
                        = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
            }

            for (QuoteStatus qs : quote.getQuoteStatusList()) {
                if ((qs.getEndDate() == null || qs.getEndDate().equals("")) && qs.getStatus().equals(StatusType.OPEN.getName())) {
                    qs.setEndDate(new Date());
                    quoteStatusFacade.edit(qs);

                    QuoteStatus statusNew = new QuoteStatus();
                    statusNew.setIdQuote(quote);
                    statusNew.setInitDate(new Date());
                    statusNew.setStatus(StatusType.IN_PROGRESS.getName());
                    quoteStatusFacade.create(statusNew);

                    Manage manege = new Manage();
                    manege.setIdAgent(agent);
                    manege.setAssignmentDate(new Date());
                    manege.setDeallocationDate(null);
                    manege.setIdQuote(quote);

                    manageFacade.create(manege);
                    break;
                }
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        }

    }

    public List<Product> complete(String query) {
        List<Product> productList;
        productList = (List<Product>) productFacade.findProductByNumberProduct(query);
        if (productList == null || productList.isEmpty()) {
            productList = (List<Product>) productFacade.findAll();
        }
        return productList;
    }

    public void fillPartGeneric(Has product) {
        if (product != null) {
            setProductGeneric(product);
        }
    }

    public void fillPartReplace(Has product) {
        if (product != null) {
            setProductReplace(product);
        }
    }

    public void crearProductSustitute(boolean isGeneric) {
        try {

            if (proController.getSelected() != null) {
                Product product = productFacade.findByNumberProduct(proController.getSelected().getPartNumber());
                if (product != null) {
                    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("message_product_exist")));
                } else {

                    productFacade.create(proController.getSelected());
                    setProductCreado(proController.getSelected());
                    if (isGeneric) {
                        RequestContext.getCurrentInstance().execute("PF('dialogPartConfirmCreate').show();");
                    } else {
                        RequestContext.getCurrentInstance().execute("PF('dialogPartConfirmCreateSubstitute').show();");
                    }
                }
            }

        } catch (Exception e) {
        }

    }

    public void fillListSustitute() {
        List<Product> sustirutePartList = new ArrayList<>();
        if (productReplace != null) {
            switch (selectionSustitute) {
                case "All":
                    partList = null;
                    break;
                case "Substitute":

                    for (IsSubstitute sustitute : productReplace.getProduct().getIsSubstituteList()) {
                        sustirutePartList.add(sustitute.getPartNumberSubstitute());
                    }
                    if (!sustirutePartList.isEmpty()) {
                        if (partList != null) {
                            partList.clear();
                        } else {
                            partList = new ArrayList<>();
                        }
                        partList.addAll(sustirutePartList);
//
//                        RequestContext.getCurrentInstance().update("formDialogSubstitute:panelButtonReplace");
                    } else {
                        RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("message_not_found_sustitute")));
                    }
                    break;
            }

        }
    }

    public void inicializedObject() {
        nroPart = null;
        partList = null;
        selectedPart = null;
        productGeneric = null;
        productReplace = null;
        productCreado = null;
        selectionSustitute = null;
        selectedPartSubtitute = null;
    }

    public void fillDescriptionGeneric() {
        proController.prepareCreate(null);
        proController.getSelected().setDescription(productGeneric.getObservation());
    }

    public void showTextArea() {
        RequestContext.getCurrentInstance().execute("document.getElementById('form:panelTextArea').style.display='block';");
    }

    public void onCustomerChange() {
        if (customerSelected != null && !customerSelected.getCompanyName().equals("")) {
            inicializedContact();
            contactList = customerSelected.getContactList();
        } else {
            contactList = new ArrayList<>();
        }
    }

    public void inicializedContact() {
        if (contactSelected != null) {
            contactSelected = null;
        }
    }

    public List<Customer> getCustomerList() {
        customerList = (List<Customer>) customerFacade.findAll();
         Collections.sort(customerList, new Comparator<Customer>() {
            @Override
            public int compare(Customer t, Customer t1) {
                return t.getCompanyName().compareTo(t1.getCompanyName());
            }
        });
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

    public Product getNroPart() {
        return nroPart;
    }

    public void setNroPart(Product nroPart) {
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

    public Has getProductGeneric() {
        return productGeneric;
    }

    public void setProductGeneric(Has productGeneric) {
        this.productGeneric = productGeneric;
    }

    public ProductController getProController() {
        return proController;
    }

    public void setProController(ProductController proController) {
        this.proController = proController;
    }

    public Product getProductCreado() {
        return productCreado;
    }

    public void setProductCreado(Product productCreado) {
        this.productCreado = productCreado;
    }

    public String getSelectionSustitute() {
        return selectionSustitute;
    }

    public void setSelectionSustitute(String selectionSustitute) {
        this.selectionSustitute = selectionSustitute;
    }

    public Has getProductReplace() {
        return productReplace;
    }

    public void setProductReplace(Has productReplace) {
        this.productReplace = productReplace;
    }

    public boolean isMakeSubstitute() {
        return makeSubstitute;
    }

    public void setMakeSubstitute(boolean makeSubstitute) {
        this.makeSubstitute = makeSubstitute;
    }

    public Product getSelectedPartSubtitute() {
        return selectedPartSubtitute;
    }

    public void setSelectedPartSubtitute(Product selectedPartSubtitute) {
        this.selectedPartSubtitute = selectedPartSubtitute;
    }

    public List<Vendor> getVendorList() {
        return vendorList;
    }

    public void setVendorList(List<Vendor> vendorList) {
        this.vendorList = vendorList;
    }

    public Vendor getVendorSelected() {
        return vendorSelected;
    }

    public void setVendorSelected(Vendor vendorSelected) {
        this.vendorSelected = vendorSelected;
    }

    public List<Contact> getContactList() {
        if (customerSelected != null && !customerSelected.getCompanyName().equals("")) {
            contactList = contactFacade.findContactsByCompanyName(customerSelected.getCompanyName());
        }
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public Contact getContactSelected() {
        return contactSelected;
    }

    public void setContactSelected(Contact contactSelected) {
        this.contactSelected = contactSelected;
    }

    public Customer getCustomerSelected() {
        return customerSelected;
    }

    public void setCustomerSelected(Customer customerSelected) {
        this.customerSelected = customerSelected;
    }

}
