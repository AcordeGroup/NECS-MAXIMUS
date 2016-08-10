package com.necs.maximus.ui.beans;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.Manage;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.db.facade.AgentFacade;
import com.necs.maximus.db.facade.ManageFacade;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.enums.OperationType;
import com.necs.maximus.enums.StatusType;
import com.necs.maximus.ui.beans.util.MailBean;
import com.necs.maximus.ui.beans.util.MailUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "quoteController")
@ViewScoped
public class QuoteController extends AbstractController<Quote> {

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

    /**
     * File export to Pdf
     */
    private StreamedContent filePdf;

    private static final String PATH_IMAGE = "check.png";

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
                status.add(StatusType.READY_AND_SENT.getName());
                quoteClose.addAll(quoteFacade.findQuoteByListStatus(status));
                //quoteClose.addAll(quoteFacade.findAllQuoteByStatus(StatusType.SENT.getName()));
                quoteClose.addAll(quoteFacade.findAllQuoteByStatus(StatusType.CLOSE.getName()));
//                quoteClose.addAll(quoteStatusFacade.findQuoteStatusByStatusAndAgent(StatusType.SENT.getName(), getUserManagedBean().getAgentId()));
//                quoteClose.addAll(quoteStatusFacade.findQuoteStatusByStatusAndAgent(StatusType.READY.getName(), getUserManagedBean().getAgentId()));
                break;

            case Sales:
                quoteOpen.addAll(quoteFacade.findQuoteByIdAgent(getUserManagedBean().getAgentId()));
//                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.READY_AND_SENT.getName(), agent));
//quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.SENT.getName(), agent));
                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.CLOSE.getName(), agent));
                break;
            case Purchasing:
                status = new ArrayList<>();
                status.add(StatusType.OPEN.getName());
                quoteOpen.addAll(quoteFacade.findQuoteByListStatus(status));
                quoteOpen.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.IN_PROGRESS.getName(), agent));
                //quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.SENT.getName(), agent));
                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.READY_AND_SENT.getName(), agent));
                quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.CLOSE.getName(), agent));
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
                statusPurchasing.append(bundle.getString("waiting_for_pricing"));
                break;
            case READY_AND_SENT:
                //statusPurchasing.append(bundle.getString("Done"));
                statusPurchasing.append(bundle.getString("waiting_for_customer"));
                break;
//            case SENT:
//                statusPurchasing.append(bundle.getString("waiting_for_customer"));

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
            case READY_AND_SENT:
                color.append("#34A852");
                break;
            case CLOSE:
                color.append("#34A852");
                break;
//            case SENT:
//                color.append("#34A852");
//                break;
            default:
                break;
        }
        return color.toString();

    }

    public void reopenQuote(Quote quote) {
        try {
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

    public void sendQuote(Quote quote) {
        try {
            if (quote != null) {
//                QuoteStatus qs = quote.getQuoteStatusList().get(0);
//                if (qs.getStatus().equals(StatusType.IN_PROGRESS.getName())) {
//                    qs.setEndDate(new Date());
//                    quoteStatusFacade.edit(qs);
//                }
//                QuoteStatus statusNew = new QuoteStatus();
//                statusNew.setIdQuote(quote);
//                statusNew.setInitDate(new Date());
//                statusNew.setEndDate(new Date());
//                statusNew.setStatus(StatusType.READY_AND_SENT.getName());
//                quoteStatusFacade.create(statusNew);
//                init();

                // aqui envio quote..... conversar con Carlos para conocer el flujo
                exportPdf(quote, OperationType.SEND.getOperationName());
            }

            RequestContext.getCurrentInstance().execute("PF('dialogSuccess').show();");
        } catch (MessagingException e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        }
    }

    public void closeQuote(Quote quote) {

        if (quote != null) {
            QuoteStatus qs = quote.getQuoteStatusList().get(0);
            qs.setEndDate(new Date());
            quoteStatusFacade.edit(qs);

            QuoteStatus statusNew = new QuoteStatus();
            statusNew.setIdQuote(quote);
            statusNew.setInitDate(new Date());
            statusNew.setEndDate(new Date());
            statusNew.setStatus(StatusType.CLOSE.getName());
            quoteStatusFacade.create(statusNew);
            init();
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

    public void sendMail(MailBean mailBean) throws MessagingException {
        Logger.getLogger(QuoteController.class.getName()).log(Level.INFO, "QuoteController.sendMail");
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.user", "pruebasemailmaximus@gmail.com");
            properties.put("mail.password", "maximus1234_");

            MailUtil mail = new MailUtil(properties);

            mail.sendHTMLEmail(mailBean);
        } catch (MessagingException ex) {
            Logger.getLogger(QuoteController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public void exportPdf(Quote quote, String operation) throws MessagingException {
        BigDecimal total = new BigDecimal(0);
        Logger.getLogger(ViewQuoteController.class.getName()).log(Level.INFO, "start ViewQuoteController.exportPdf");

        //Integer quoteId = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("quoteId"));
        //Quote quote = quoteFacade.find(quoteId);
        Logger.getLogger(ViewQuoteController.class.getName()).log(Level.INFO, "quoteId=".concat(quote.getIdQuote().toString()));

        try {
            // Se crea el documento
            Document document = new Document();
            OutputStream out;
            String filename = "quote_".concat(quote.getIdQuote().toString()).concat(".pdf");
            // Se crea el OutputStream para el fichero donde queremos dejar el pdf.
            if (operation.equals(OperationType.SEND.getOperationName())) {
                out = new ByteArrayOutputStream();
            } else {
                out = new FileOutputStream(filename);
            }

            // Se asocia el documento al OutputStream y se indica que el espaciado entre
            // lineas sera de 20. Esta llamada debe hacerse antes de abrir el documento
            PdfWriter.getInstance(document, out).setInitialLeading(20);

            //se apertura el documento..
            document.open();

            //  *****  creacion de fuentes asociadas al documentos *******
            Font fontHeader = FontFactory.getFont("Times New Roman", // fuente
                    9, // tamaño
                    Font.BOLD, // estilo
                    BaseColor.BLACK);
            Font fontDefault = FontFactory.getFont("Times New Roman", // fuente
                    7, // tamaño
                    Font.NORMAL, // estilo
                    BaseColor.BLACK);
            Font fontDefaultBlue = FontFactory.getFont("Times New Roman", // fuente
                    7, // tamaño
                    Font.NORMAL, // estilo
                    new BaseColor(118, 25, 255));

            Font fontDefaultBold = FontFactory.getFont("Times New Roman", // fuente
                    7, // tamaño
                    Font.BOLD, // estilo
                    BaseColor.BLACK);
            Font fontHeaderBig = FontFactory.getFont("Times New Roman", // fuente
                    13, // tamaño
                    Font.BOLD, // estilo
                    BaseColor.BLACK);

            BaseColor baseColor = new BaseColor(223, 232, 254);

            Float defaultPadding = 5f;
            Float defaultSpacing = 15f;
             //  *****  --------------------  *******

            //section title........
            PdfPTable tablaTitle = new PdfPTable(2);
            tablaTitle.setWidths(new int[]{50, 50});
            tablaTitle.setWidthPercentage(100);
            tablaTitle.setHorizontalAlignment(Element.ALIGN_CENTER);

            //sumna title company 
            tablaTitle.addCell(createCell(bundle.getString("titleCompany"), null, 2, fontHeader, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));

//            //suma image company
//            tablaTitle.addCell(createCellImage(PATH_IMAGE));
            tablaTitle.addCell(createCell(bundle.getString("sale_quote"), null, null, fontHeaderBig, null, Element.ALIGN_RIGHT, defaultPadding, PdfPCell.NO_BORDER, null));

            //section Sales quote Interna........
            PdfPTable tablaSalesQuoteInter = new PdfPTable(2);
            tablaSalesQuoteInter.setWidths(new int[]{50, 50});
            tablaSalesQuoteInter.setWidthPercentage(70);
            tablaSalesQuoteInter.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaSalesQuoteInter.setSpacingBefore(defaultSpacing);

            tablaSalesQuoteInter.addCell(createCell(bundle.getString("sale_quote").concat(" N°"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaSalesQuoteInter.addCell(createCell(quote.getIdQuote().toString(), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaSalesQuoteInter.addCell(createCell(bundle.getString("CustomerHeading").concat(" N°"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaSalesQuoteInter.addCell(createCell(quote.getIdCustomer().getCompanyName().getCompanyName(), null, null, fontDefaultBlue, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            //suma text sales quote
            PdfPCell cellSalesQuote = new PdfPCell();
            cellSalesQuote.addElement(tablaSalesQuoteInter);
            cellSalesQuote.setBorder(PdfPCell.NO_BORDER);
            cellSalesQuote.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tablaTitle.addCell(cellSalesQuote);

            // fin section title........
            //section billTo and Shippping To
            PdfPTable tablaBillTo = new PdfPTable(1);
            tablaBillTo.setWidths(new int[]{1});
            tablaBillTo.setWidthPercentage(98);
            tablaBillTo.setHorizontalAlignment(Element.ALIGN_LEFT);

            //sumna encabezado bill_to..
            tablaBillTo.addCell(createCell(bundle.getString("bill_to"), 2, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));

            //sumna information bill_to..
            tablaBillTo.addCell(createCell(quote.getShipping_to(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));

            PdfPTable tablaShippingTo = new PdfPTable(1);
            tablaShippingTo.setWidths(new int[]{1});
            tablaShippingTo.setWidthPercentage(98);
            tablaShippingTo.setHorizontalAlignment(Element.ALIGN_RIGHT);

            //sumna encabezado shipping_to..
            tablaShippingTo.addCell(createCell(bundle.getString("shipping_to"), 2, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));

            //Suma la información shipping_to
            tablaShippingTo.addCell(createCell(quote.getShipping_to(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));

            PdfPTable tablaContent = new PdfPTable(2);
            tablaContent.setWidths(new int[]{1, 1});
            tablaContent.setWidthPercentage(100);
            tablaContent.setSpacingBefore(30f);

            PdfPCell cellBillTo = new PdfPCell();
            cellBillTo.addElement(tablaBillTo);
            cellBillTo.setBorder(PdfPCell.NO_BORDER);
            PdfPCell cellShippingTo = new PdfPCell();
            cellShippingTo.addElement(tablaShippingTo);
            cellShippingTo.setBorder(PdfPCell.NO_BORDER);

            tablaContent.addCell(cellBillTo);
            tablaContent.addCell(cellShippingTo);
            // fin section billTo and Shippping To

            PdfPTable tablePart1 = new PdfPTable(5);
            tablePart1.setWidths(new int[]{15, 25, 20, 20, 20});
            tablePart1.setWidthPercentage(100);
            tablePart1.setSpacingBefore(defaultSpacing);

            //suma primer encabezado 
            tablePart1.addCell(createCell(bundle.getString("quote_date"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("ship_via"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("f_o_v"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("customer_po_number"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("payment_method"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            //suma valores primer encabezado 
            tablePart1.addCell(createCell(convertDateToString(quote.getCreationDate()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("ship_via"), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("f_o_v"), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(quote.getIdCustomer().getPrimaryPhoneNumber(), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell(bundle.getString("payment_method"), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            PdfPTable tablePart2 = new PdfPTable(4);
            tablePart2.setWidths(new int[]{25, 25, 25, 25});
            tablePart2.setWidthPercentage(100);
            //suma segundo encabezado 
            tablePart2.addCell(createCell(bundle.getString("entered_by"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell(bundle.getString("sales_person"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell(bundle.getString("ordered_by"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell(bundle.getString("resale_number"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            //suma valores segundo encabezado 
            tablePart2.addCell(createCell(quote.getIdAgent().getName().concat(" ").concat(quote.getIdAgent().getLastName()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell(quote.getIdAgent().getName().concat(" ").concat(quote.getIdAgent().getLastName()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell(bundle.getString("ordered_by"), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell(bundle.getString("resale_number"), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            PdfPTable tablePart3 = new PdfPTable(6);
            tablePart3.setWidths(new int[]{13, 13, 5, 35, 17, 17});
            tablePart3.setWidthPercentage(100);
            //suma tercer encabezado 
            tablePart3.addCell(createCell(bundle.getString("order_quantity"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("approve_quantity"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("tax"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("item_description"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("unit_price"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("extended_price"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));

            //Suma la información del quote
            for (Has has : quote.getHasList()) {
                tablePart3.addCell(createCell(has.getQtyRequested() != null ? has.getQtyRequested().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
                tablePart3.addCell(createCell(has.getQtyFound() != null ? has.getQtyFound().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
                tablePart3.addCell(createCell("TAX", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
                tablePart3.addCell(createCellTextColor(has.getProduct() != null ? has.getProduct().getPartNumber() : "", "                       ".concat("U of M : Pieces\n").concat(has.getProduct().getDescription()), null, null, fontDefault, fontDefaultBlue, null, Element.ALIGN_LEFT, defaultPadding, null, null));

                //tablePart.addCell(createCell(has.getCustomerTargetPrice() != null ? has.getCustomerTargetPrice().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
                tablePart3.addCell(createCell(has.getSuggestedSalesPrice() != null ? has.getSuggestedSalesPrice().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
                tablePart3.addCell(createCell(has.getExtended() != null ? has.getExtended().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));

                total = total.add(has.getExtended());
            }
            PdfPCell cell = createCell(bundle.getString("approved_by").concat(" :_____________________________________"), 6, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, 40f);

            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablePart3.addCell(cell);
//            PdfPCell ce = createCellImage(PATH_IMAGE);
//            ce.setColspan(6);
//            ce.setHorizontalAlignment(Element.ALIGN_CENTER);
//            tablePart3.addCell(ce);

            //section footer........
            PdfPTable tablaFooter = new PdfPTable(2);
            tablaFooter.setWidths(new int[]{1, 1});
            tablaFooter.setWidthPercentage(100);
            tablaFooter.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaFooter.setSpacingBefore(defaultSpacing);

            //section Sales footer Interna left........
            PdfPTable tablaFooterInterLeft = new PdfPTable(2);
            tablaFooterInterLeft.setWidths(new int[]{1, 1});
            tablaFooterInterLeft.setWidthPercentage(50);
            tablaFooterInterLeft.setHorizontalAlignment(Element.ALIGN_LEFT);

            tablaFooterInterLeft.addCell(createCell(bundle.getString("print_date"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterLeft.addCell(createCell(convertDateToString(new Date()).substring(0, 10), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterLeft.addCell(createCell(bundle.getString("print_time"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterLeft.addCell(createCell(convertDateToString(new Date()).substring(10), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterLeft.addCell(createCell(bundle.getString("page_number"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterLeft.addCell(createCell(String.valueOf(document.getPageNumber()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            //suma text sales quote
            PdfPCell cellFooterInterLeft = new PdfPCell();
            cellFooterInterLeft.addElement(tablaFooterInterLeft);
            cellFooterInterLeft.setBorder(PdfPCell.NO_BORDER);
            cellFooterInterLeft.setHorizontalAlignment(Element.ALIGN_LEFT);

            //section Sales footer Interna rigth........
            PdfPTable tablaFooterInterRigth = new PdfPTable(2);
            tablaFooterInterRigth.setWidths(new int[]{1, 1});
            tablaFooterInterRigth.setWidthPercentage(60);
            tablaFooterInterRigth.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tablaFooterInterRigth.addCell(createCell(bundle.getString("subtotal"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(total.toString(), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("freight"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell("0", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("sales_tax"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell((total.multiply(new BigDecimal(16)).divide(new BigDecimal(100))).toString(), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("order_total"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("usd").concat(" ").concat((total.multiply(new BigDecimal(16)).divide(new BigDecimal(100)).add(total)).toString()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            //suma text sales quote
            PdfPCell cellFooterInterRigth = new PdfPCell();
            cellFooterInterRigth.addElement(tablaFooterInterRigth);
            cellFooterInterRigth.setBorder(PdfPCell.NO_BORDER);
            cellFooterInterRigth.setHorizontalAlignment(Element.ALIGN_RIGHT);

            //sumna title Footer 
            tablaFooter.addCell(cellFooterInterLeft);
            tablaFooter.addCell(cellFooterInterRigth);

            document.add(tablaTitle);
            document.add(tablaContent);
            document.add(tablePart1);
            document.add(tablePart2);
            document.add(tablePart3);
            document.add(tablaFooter);
            document.close();
            out.flush();
            out.close();

            //File file = new File(filename);
            switch (OperationType.getOperationByName(operation)) {
                case SEND:
                    byte[] fileA = null;
                    if (out instanceof ByteArrayOutputStream) {
                        fileA = ((ByteArrayOutputStream) out).toByteArray();
                    }

                    if (quote.getIdAgent() != null && null != quote.getIdAgent().getEmail()) {

                        if (emailValidator(quote.getIdAgent().getEmail())) {

                            List<String> to = new ArrayList<>();
                            to.add(quote.getIdAgent().getEmail());

                            MailBean mail = new MailBean();
                            mail.setFrom( bundle.getString("email_remitent"));
                            mail.setTo(to);
                            mail.setNameFlie(filename);
                            mail.setSubject(bundle.getString("processed_quote"));
                            mail.setBody(bundle.getString("body_email"));
                            mail.setFile(fileA);

                            sendMail(mail);

                        } else {
                            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("messageErrorEmailInvalid")));
                        }

                    } else {
                        FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("messageErrorEmailNull")));
                    }

                    break;

                case EXPORT:
                    File file = new File(filename);
                    InputStream stream = new FileInputStream(file);
                    setFilePdf(new DefaultStreamedContent(stream, "application/pdf", filename));
                    break;
                default:
                    break;

            }

            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.INFO, "file writed");

            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.INFO, "end ViewQuoteController.exportPdf");
        } catch (DocumentException | IOException ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage("error_export"));
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("error_send")));
            Logger.getLogger(QuoteController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private PdfPCell createCellImage(String path) {

        PdfPCell cell = null;
        try {
            Image img = Image.getInstance(path);
            //img.setWidthPercentage(2);
            img.scaleAbsolute(5, 5);
            cell = new PdfPCell(img, true);
            cell.setBorder(PdfPCell.NO_BORDER);

        } catch (BadElementException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cell;
    }

    private PdfPCell createCell(String text, Integer span, Integer rSpan, Font font, BaseColor backColor, Integer halign, Float padding, Integer border, Float height) {

        Phrase phrase = (font != null) ? new Phrase(text, font) : new Phrase(text);
        PdfPCell cell = new PdfPCell(phrase);
        if (span != null) {
            cell.setColspan(span);
        }

        if (rSpan != null) {
            cell.setRowspan(rSpan);
        }
        if (backColor != null) {
            cell.setBackgroundColor(backColor);
        }
        if (halign != null) {
            cell.setHorizontalAlignment(halign);
        }
        if (padding != null) {
            cell.setPadding(padding);
        }
        if (border != null) {
            cell.setBorder(PdfPCell.NO_BORDER);

        }
        if (height != null) {
            cell.setFixedHeight(height);

        }
        return cell;
    }

    private PdfPCell createCellTextColor(String text, String textColor, Integer span, Integer rSpan, Font font, Font fontTextColor, BaseColor backColor, Integer halign, Float padding, Integer border, Float height) {

        Chunk product = new Chunk(text, font);
        Chunk description = new Chunk(textColor, fontTextColor);
        Phrase phrase = new Phrase(product);
        phrase.add(description);
        PdfPCell cell = new PdfPCell(phrase);

        if (span != null) {
            cell.setColspan(span);
        }

        if (rSpan != null) {
            cell.setRowspan(rSpan);
        }
        if (backColor != null) {
            cell.setBackgroundColor(backColor);
        }
        if (halign != null) {
            cell.setHorizontalAlignment(halign);
        }
        if (padding != null) {
            cell.setPadding(padding);
        }
        if (border != null) {
            cell.setBorder(PdfPCell.NO_BORDER);

        }
        if (height != null) {
            cell.setFixedHeight(height);

        }
        return cell;
    }

    public BigDecimal amountTotalQuote(Quote quote) {
        BigDecimal total = new BigDecimal(0);
        if (quote != null) {
            for (Has h : quote.getHasList()) {
                total = total.add(h.getExtended() == null ? new BigDecimal(0) : h.getExtended());
            }
        }
        return total;
    }

    private String convertDateToString(Date dateCreation) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        String date = sdf.format(dateCreation);
        return date;
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

    public StreamedContent getFilePdf() {
        return filePdf;
    }

    public void setFilePdf(StreamedContent filePdf) {
        this.filePdf = filePdf;
    }

}
