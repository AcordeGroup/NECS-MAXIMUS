package com.necs.maximus.ui.beans;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.Manage;
import com.necs.maximus.db.entity.Product;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteNote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.db.facade.AgentFacade;
import com.necs.maximus.db.facade.ManageFacade;
import com.necs.maximus.db.facade.QuoteFacade;
import com.necs.maximus.db.facade.QuoteStatusFacade;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.enums.OperationType;
import com.necs.maximus.enums.StatusType;
import com.necs.maximus.ui.beans.util.Constantes;
import com.necs.maximus.ui.beans.util.MailBean;
import com.necs.maximus.ui.beans.util.MailUtil;
import com.necs.maximus.ui.beans.util.MobilePageController;
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
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "quoteController")
@ViewScoped
public class QuoteController extends AbstractController<Quote> {

    @Inject
    private ContactController idContactController;

    @Inject
    private AgentController idAgentController;
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

    private List<Quote> quoteOpen;
    private List<Quote> quoteClose;
    private List<Quote> filteredQuote;

    /**
     * File export to Pdf
     */
    private StreamedContent filePdf;

    private static final String PATH_IMAGE = "check.png";
    private static final String PATH_IMAGE_LOGO = "logoNecs.png";

    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);

    private final static Logger logger = Logger.getLogger(QuoteController.class.getName());

    /**
     * Constructor
     */
    public QuoteController() {
        // Inform the Abstract parent controller of the concrete Quote Entity
        super(Quote.class);
    }

    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "Start init()");

        HashMap param = new HashMap();
        param.put("idAgent", getUserManagedBean().getAgentId());
        agent = agentFacade.listUniqueNamedQuery(Agent.class, "Agent.findByIdAgent", param);
        quoteClose = new ArrayList<>();
        quoteOpen = new ArrayList<>();
        inicializedListByStatus();

        logger.log(Level.INFO, "End init()");
    }

    /**
     * Metodo encargado de inicializar las listas del dashboard segun el tipo de
     * usuario logueddo
     */
    public void inicializedListByStatus() {
        logger.log(Level.INFO, "Start inicializedListByStatus()");

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
//              quoteClose.addAll(quoteStatusFacade.findQuoteStatusByStatusAndAgent(StatusType.SENT.getName(), getUserManagedBean().getAgentId()));
//              quoteClose.addAll(quoteStatusFacade.findQuoteStatusByStatusAndAgent(StatusType.READY.getName(), getUserManagedBean().getAgentId()));
                break;

            case Sales:
                quoteOpen.addAll(quoteFacade.findQuoteByIdAgent(getUserManagedBean().getAgentId()));
//              quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.READY_AND_SENT.getName(), agent));
//              quoteClose.addAll(quoteFacade.findQuoteByStatusAndAgent(StatusType.SENT.getName(), agent));
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

        logger.log(Level.INFO, "End inicializedListByStatus()");

    }

    /**
     * Retorna el estatus del vendedor segun estatus de la cotizacion
     *
     * @param status
     * @return
     */
    public String getStatusPurchasing(String status) {
        logger.log(Level.INFO, "Start getStatusPurchasing()");

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
        logger.log(Level.INFO, "End getStatusPurchasing()");

        return statusPurchasing.toString();
    }

    /**
     * obtiene un String con las primeras 5 numeros de partes asociadas a una
     * cotización
     *
     * @param item
     * @return
     */
    public List<Product> getNumParts(Quote item) {
        logger.log(Level.INFO, "Start getNumParts()");
        List<Product> listPartNumber = new ArrayList<>();

        int numPart = item.getHasList().size() < 4 ? item.getHasList().size() : 4;
        for (int i = 0; i < numPart; i++) {
            listPartNumber.add(item.getHasList().get(i).getProduct());
        }
        logger.log(Level.INFO, "End getNumParts()");
        return listPartNumber;
    }

    /**
     * Retorna el color del div que denota el estado de la cotizacion segun sus
     * dias de creacion
     *
     * @param status
     * @param dateCreation
     * @return
     */
    public String getColorDiv(String status, Date dateCreation) {
        logger.log(Level.INFO, "Start getColorDiv()");
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

        logger.log(Level.INFO, "End getColorDiv()");
        return color.toString();

    }

    /**
     * Metodo encargado de cambiar el estatus de una cotizaciona a OPEN, envia
     * notificacion a vendedor de dicho evento
     *
     * @param quote
     */
    public void reopenQuote(Quote quote) {
        logger.log(Level.INFO, "Start reopenQuote()");
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

                sentNotificationStatusQuote(quote);
                RequestContext.getCurrentInstance().execute("PF('dialogSuccess').show();");
                init();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        }
        logger.log(Level.INFO, "End reopenQuote()");
    }

    /**
     * Envia cotizacion al vendedor via email con el documento pdf adjunto
     *
     * @param quote
     */
    public void sendQuote(Quote quote) {
        logger.log(Level.INFO, "Start sendQuote()");
        try {
            if (quote != null) {
                // aqui envio quote.....
                exportPdf(quote, OperationType.SEND.getOperationName());
                RequestContext.getCurrentInstance().execute("PF('dialogSuccess').show();");
            }

        } catch (MessagingException e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

        }
        logger.log(Level.INFO, "End sendQuote()");
    }

    /**
     * Metodo encargado de cambiar el estatus de una cotizaciona a CLOSE
     *
     * @param quote
     */
    public void closeQuote(Quote quote) {
        logger.log(Level.INFO, "Start closeQuote()");

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
        logger.log(Level.INFO, "End closeQuote()");
    }

    /**
     * verifica si una cotizacion esta siendo tratada por un agente particular
     *
     * @param quote
     * @return
     */
    public boolean isQuoteAdmin(Quote quote) {
        logger.log(Level.INFO, "Start isQuoteAdmin()");
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
        logger.log(Level.INFO, "End isQuoteAdmin()");
        return false;
    }

    /**
     * Metodo encargado de armar las propiedades del mail y enviarlo.
     *
     * @param mailBean
     * @throws MessagingException
     */
    public void sendMail(MailBean mailBean) throws MessagingException {
        logger.log(Level.INFO, "Start sendMail()");
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            properties.put("mail.smtp.auth", "true");
            //           properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.debug", "true");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.user", "pruebasemailmaximus@gmail.com");
            properties.put("mail.smtp.password", "maximus1234_");
            properties.put("mail.smtp.ssl.enable", "true");
//            properties.put("mail.smtp.socketFactory.fallback", "false");
//            properties.put("mail.transport.protocol", "smtps");

            MailUtil mail = new MailUtil(properties);

            mail.sendHTMLEmail(mailBean);
        } catch (MessagingException ex) {
            Logger.getLogger(QuoteController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        logger.log(Level.INFO, "End sendMail()");
    }

    /**
     * Metodo encargado del armar el pdf de la cotizacion, enviarlo al sales o
     * exportarlo a la maquina cliente depende de la operacion
     *
     * @param quote
     * @param operation
     * @throws MessagingException
     */
    public void exportPdf(Quote quote, String operation) throws MessagingException {
        BigDecimal total = new BigDecimal(0);
        logger.log(Level.INFO, "Start exportPdf()");

        logger.log(Level.INFO, "quoteId=".concat(quote.getIdQuote().toString()));

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
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setInitialLeading(20);

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

            Font fontFooter = FontFactory.getFont("Times New Roman", // fuente
                    7, // tamaño
                    Font.NORMAL, // estilo
                    BaseColor.LIGHT_GRAY);

            BaseColor baseColor = new BaseColor(223, 232, 254);

            Float defaultPadding = 5f;
            Float defaultSpacing = 15f;

            //section footer........
            PdfPTable tablaFooter = new PdfPTable(1);
            tablaFooter.setTotalWidth(523);
            tablaFooter.setHorizontalAlignment(Element.ALIGN_CENTER);

            tablaFooter.addCell(createCell(bundle.getString("footer"), null, null, fontFooter, null, Element.ALIGN_CENTER, null, PdfPCell.NO_BORDER, null));

            FooterTable event = new FooterTable(tablaFooter);
            writer.setPageEvent(event);

            //se apertura el documento..
            document.open();

            //section title........
            PdfPTable tablaTitle = new PdfPTable(2);
            tablaTitle.setWidths(new int[]{30, 70});
            tablaTitle.setWidthPercentage(100);
            tablaTitle.setHorizontalAlignment(Element.ALIGN_CENTER);

            //sumna title company 
//            tablaTitle.addCell(createCell(bundle.getString("titleCompany"), null, 2, fontHeader, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));
            tablaTitle.addCell(createCellImage(PATH_IMAGE_LOGO));

            tablaTitle.addCell(createCell(bundle.getString("sale_quote"), null, null, fontHeaderBig, null, Element.ALIGN_RIGHT, defaultPadding, PdfPCell.NO_BORDER, null));

            //section Sales quote Interna........
            PdfPTable tablaSalesQuoteInter = new PdfPTable(2);
            tablaSalesQuoteInter.setWidths(new int[]{50, 50});
            tablaSalesQuoteInter.setWidthPercentage(60);
            tablaSalesQuoteInter.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaSalesQuoteInter.setSpacingBefore(defaultSpacing);

            tablaSalesQuoteInter.addCell(createCell(bundle.getString("sale_quote").concat(" N°"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaSalesQuoteInter.addCell(createCell(quote.getIdQuote().toString(), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaSalesQuoteInter.addCell(createCell(bundle.getString("CustomerHeading").concat(" N°"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaSalesQuoteInter.addCell(createCell(quote.getIdContact().getCompanyName().getIdCustomer(), null, null, fontDefaultBlue, null, Element.ALIGN_CENTER, defaultPadding, null, null));

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
            //sumna information customerName..
            tablaBillTo.addCell(createCell(quote.getIdContact().getCompanyName().getCompanyName(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));

            //sumna information bill_to..
            tablaBillTo.addCell(createCell(quote.getShipping_to(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));

            PdfPTable tablaShippingTo = new PdfPTable(1);
            tablaShippingTo.setWidths(new int[]{1});
            tablaShippingTo.setWidthPercentage(98);
            tablaShippingTo.setHorizontalAlignment(Element.ALIGN_RIGHT);

            //sumna encabezado shipping_to..
            tablaShippingTo.addCell(createCell(bundle.getString("shipping_to"), 2, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            //sumna information customerName..
            tablaShippingTo.addCell(createCell(quote.getIdContact().getCompanyName().getCompanyName(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));
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
            tablePart1.addCell(createCell("", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell("", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell("", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart1.addCell(createCell("", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

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
            tablePart2.addCell(createCell("", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart2.addCell(createCell("", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            PdfPTable tablePart3 = new PdfPTable(5);
            tablePart3.setWidths(new int[]{14, 14, 36, 18, 18});
            tablePart3.setWidthPercentage(100);
            //suma tercer encabezado 
            tablePart3.addCell(createCell(bundle.getString("order_quantity"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("approve_quantity"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
//            tablePart3.addCell(createCell(bundle.getString("tax"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("item_description"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("unit_price"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablePart3.addCell(createCell(bundle.getString("extended_price"), null, null, fontHeader, baseColor, Element.ALIGN_CENTER, defaultPadding, null, null));

            //Suma la información del quote
            for (Has has : quote.getHasList()) {
                tablePart3.addCell(createCell(has.getQtyRequested() != null ? has.getQtyRequested().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
                tablePart3.addCell(createCell(has.getQtyFound() != null ? has.getQtyFound().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
//                tablePart3.addCell(createCell("Y", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));

                //section table Interna que maneja informacion del description........
                PdfPTable tablaInter = new PdfPTable(2);
                tablaInter.setWidths(new int[]{65, 35});
                tablaInter.setWidthPercentage(100);
                tablaInter.setHorizontalAlignment(Element.ALIGN_CENTER);

                tablaInter.addCell(createCell(has.getProduct() != null ? has.getProduct().getPartNumber() : "", null, null, fontDefaultBlue, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));
                tablaInter.addCell(createCell("U of M : Pieces", null, null, fontDefault, null, Element.ALIGN_RIGHT, defaultPadding, PdfPCell.NO_BORDER, null));
                tablaInter.addCell(createCell(has.getDescription() != null ? has.getDescription() : "", 2, null, fontDefaultBold, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER, null));

                //suma text sales quote
                PdfPCell cellInterDescription = new PdfPCell();
                cellInterDescription.addElement(tablaInter);
                //cellInterDescription.setBorder(PdfPCell.NO_BORDER);
                cellInterDescription.setHorizontalAlignment(Element.ALIGN_CENTER);

                tablePart3.addCell(cellInterDescription);

                tablePart3.addCell(createCell(has.getSuggestedSalesPrice() != null ? bundle.getString("usd").concat("$ ").concat(has.getSuggestedSalesPrice().toString()) : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));
                tablePart3.addCell(createCell(has.getExtended() != null ? bundle.getString("usd").concat("$ ").concat(has.getExtended().toString()) : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null, null));

                total = total.add(has.getExtended() == null ? BigDecimal.ZERO : has.getExtended());
            }
            PdfPCell cell = createCell(bundle.getString("approved_by").concat(" :_____________________________________"), 6, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, 40f);

            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablePart3.addCell(cell);

            //section totales.......
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidths(new int[]{1, 1});
            tablaTotales.setWidthPercentage(100);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaTotales.setSpacingBefore(defaultSpacing);

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
            tablaFooterInterRigth.addCell(createCell(bundle.getString("usd").concat("$ ").concat(total.toString()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("freight"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell("0", null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("sales_tax"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("usd").concat("$ ").concat((total.multiply(new BigDecimal(16)).divide(new BigDecimal(100))).toString()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("order_total"), null, null, fontDefaultBold, baseColor, Element.ALIGN_RIGHT, defaultPadding, null, null));
            tablaFooterInterRigth.addCell(createCell(bundle.getString("usd").concat("$ ").concat((total.multiply(new BigDecimal(16)).divide(new BigDecimal(100)).add(total)).toString()), null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null, null));

            //suma text sales quote
            PdfPCell cellFooterInterRigth = new PdfPCell();
            cellFooterInterRigth.addElement(tablaFooterInterRigth);
            cellFooterInterRigth.setBorder(PdfPCell.NO_BORDER);
            cellFooterInterRigth.setHorizontalAlignment(Element.ALIGN_RIGHT);

            //sumna title totales 
            tablaTotales.addCell(cellFooterInterLeft);
            tablaTotales.addCell(cellFooterInterRigth);

            document.add(tablaTitle);
            document.add(tablaContent);
            document.add(tablePart1);
            document.add(tablePart2);
            document.add(tablePart3);
            document.add(tablaTotales);
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
                            mail.setFrom(bundle.getString("email_remitent"));
                            mail.setTo(to);
                            mail.setNameFlie(filename);
                            mail.setSubject(bundle.getString("processed_quote"));
                            mail.setBody(constructBodyMail(quote, bundle.getString("body_email")));
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

            logger.log(Level.INFO, "file writed");

        } catch (DocumentException | IOException ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage("error_export"));
            logger.log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("error_send")));
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        }
        logger.log(Level.INFO, "End exportPdf()");
    }

    /**
     * Utilitario para crear una celda tipo imagen del pfd
     *
     * @param path
     * @return
     */
    private PdfPCell createCellImage(String path) {
        logger.log(Level.INFO, "Start createCellImage()");
        PdfPCell cell = null;
        try {
            Image img = Image.getInstance(path);
            //img.scaleAbsolute(5, 5);
            cell = new PdfPCell(img, true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setRowspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);

        } catch (BadElementException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.log(Level.INFO, "End createCellImage()");
        return cell;
    }

    /**
     * Utilitario para crear una celda del pfd
     *
     * @param text
     * @param span
     * @param rSpan
     * @param font
     * @param backColor
     * @param halign
     * @param padding
     * @param border
     * @param height
     * @return
     */
    private PdfPCell createCell(String text, Integer span, Integer rSpan, Font font, BaseColor backColor, Integer halign, Float padding, Integer border, Float height) {
        logger.log(Level.INFO, "Start createCell()");
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
        logger.log(Level.INFO, "End createCell()");
        return cell;
    }

    /**
     * Retorna el monto total de una quota, basado en la cantidad de propudtos
     * asociados a la misma
     *
     * @param quote
     * @return
     */
    public BigDecimal amountTotalQuote(Quote quote) {
        logger.log(Level.INFO, "Start amountTotalQuote()");
        BigDecimal total = new BigDecimal(0);
        if (quote != null) {
            for (Has h : quote.getHasList()) {
                total = total.add(h.getExtended() == null ? new BigDecimal(0) : h.getExtended());
            }
        }
        logger.log(Level.INFO, "End amountTotalQuote()");
        return total;
    }

    /**
     * Utilitario para convertir Data to String
     *
     * @param dateCreation
     * @return
     */
    private String convertDateToString(Date dateCreation) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        String date = sdf.format(dateCreation);
        return date;
    }

    /**
     * Clase utilitaria que representa el footer del pdf
     */
    public class FooterTable extends PdfPageEventHelper {

        protected PdfPTable footer;

        public FooterTable(PdfPTable footer) {
            this.footer = footer;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            footer.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());
        }
    }

    /**
     * Metodo encargado de enviar notificaion cuando cambia el estatus al
     * usuario sales en una de sus cotizaciones
     *
     * @param quote
     */
    public void sentNotificationStatusQuote(Quote quote) {
        logger.log(Level.INFO, "Start sentNotificationStatusQuote()");
        if (quote.getIdAgent() != null && null != quote.getIdAgent().getEmail()) {

            if (emailValidator(quote.getIdAgent().getEmail())) {

                try {
                    List<String> to = new ArrayList<>();
                    to.add(quote.getIdAgent().getEmail());

                    MailBean mail = new MailBean();
                    mail.setFrom(bundle.getString("email_remitent"));
                    mail.setTo(to);
                    mail.setNameFlie(null);
                    mail.setSubject(bundle.getString("change_status_quote_subject").replace("{xxx}", quote.getIdQuote().toString()));
                    mail.setBody(constructBodyMail(quote, bundle.getString("change_status_quote_body")));
                    mail.setFile(null);

                    sendMail(mail);
                } catch (MessagingException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("messageErrorEmailInvalid")));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("messageErrorEmailNull")));
        }

        logger.log(Level.INFO, "End sentNotificationStatusQuote()");

    }

    /**
     * Metodo encargado de construir dinamicamente el body del correo con la
     * cotizacion
     *
     * @param quote
     * @return
     */
    public String constructBodyMail(Quote quote, String body) {
        String bodyInfoProduct = new String();
        StringBuilder bodyDescription = new StringBuilder();
        StringBuilder bodyNotes = new StringBuilder();
        //BigDecimal total = new BigDecimal(0);

        bodyInfoProduct = body
                .replace("{xxx}", String.valueOf(quote.getIdQuote()))
                .replace(Constantes.SALES_QUOTE_NUMBER, String.valueOf(quote.getIdQuote()))
                .replace(Constantes.CUSTOMER_QUOTE, quote.getIdContact().getCompanyName().getCompanyName())
                .replace(Constantes.BILL_TO, quote.getShipping_to())
                .replace(Constantes.SHIP_TO, quote.getShipping_to())
                .replace(Constantes.QUOTE_DATE, convertDateToString(quote.getCreationDate()))
                // se comenta intencionalmente a peticion del cliente, de momento no se mostrara esta info
                //.replace(Constantes.SHIP_VIA, "")
                //.replace(Constantes.FOB, "")
                //.replace(Constantes.CUSTOMER_PO_NUMBER, "")
                //.replace(Constantes.PAYMENT_METHOD, "")
                //.replace(Constantes.ENTERED_BY, quote.getIdAgent().getName().concat(" ").concat(quote.getIdAgent().getLastName()))
                .replace(Constantes.SALES_PERSON, quote.getIdAgent().getName().concat(" ").concat(quote.getIdAgent().getLastName()));
        //.replace(Constantes.ORDERED_BY, "")
        //.replace(Constantes.RESALE_NUMBER, "");

        // adicono informacion de los productos cotizados
        for (Has h : quote.getHasList()) {
            String bodyDes = bundle.getString("bodyDescription")
                    .replace(Constantes.QUANTITY_FOUND, String.valueOf(h.getQtyFound()))
                    //.replace(Constantes.ORDER_QUANTITY, String.valueOf(h.getQtyRequested()))
                    //.replace(Constantes.APPROVE_QUANTITY, String.valueOf(h.getQtyFound()))
                    .replace(Constantes.DESCRIPTION, h.getProduct().getPartNumber() + " " + h.getProduct().getDescription())
                    .replace(Constantes.UNIT_PRICE, String.valueOf(h.getSuggestedSalesPrice()))
                    .replace(Constantes.EXTENDED_PRICE, String.valueOf(h.getExtended()));
            bodyDescription.append(bodyDes);

            // total = total.add(h.getExtended() == null ? BigDecimal.ZERO : h.getExtended());
        }
        // adiciono informacion de los comentarios asociados a la quote.
        for (QuoteNote note : quote.getQuoteNoteList()) {
            String bodyN = bundle.getString("bodyNotes")
                    .replace(Constantes.ID_AGENT, String.valueOf(note.getIdAgent().getName() + " " + note.getIdAgent().getLastName()))
                    //.replace(Constantes.ORDER_QUANTITY, String.valueOf(h.getQtyRequested()))
                    //.replace(Constantes.APPROVE_QUANTITY, String.valueOf(h.getQtyFound()))
                    .replace(Constantes.CREATION_DATE, convertDateToString(note.getCreationDate()))
                    .replace(Constantes.NOTE, note.getNote());

            bodyNotes.append(bodyN);

            // total = total.add(h.getExtended() == null ? BigDecimal.ZERO : h.getExtended());
        }

        bodyInfoProduct = bodyInfoProduct.replace(Constantes.BODY_DESCRIPTION, bodyDescription.toString())
                .replace(Constantes.BODY_NOTES, bodyNotes.toString());

        // se comenta intencionalmente a peticion del cliente, de momento no se mostrara esta info
        //  .replace(Constantes.PRINT_DATE, convertDateToString(new Date()).substring(0, 10))
        //  .replace(Constantes.PRINT_TIME, convertDateToString(new Date()).substring(10))
        //  .replace(Constantes.SUB_TOTAL, total.toString())
        //  .replace(Constantes.FREIGHT, "0")
        //  .replace(Constantes.SALES_TAX, (total.multiply(new BigDecimal(16)).divide(new BigDecimal(100))).toString())
        //  .replace(Constantes.ORDER_TOTAL, bundle.getString("usd").concat(" ").concat("$").concat((total.multiply(new BigDecimal(16)).divide(new BigDecimal(100)).add(total)).toString()));
        return bodyInfoProduct;
    }

    public void prepareIdCustomer(ActionEvent event) {
        if (this.getSelected() != null && idContactController.getSelected() == null) {
            idContactController.setSelected(this.getSelected().getIdContact());
        }
    }

    public void prepareIdAgent(ActionEvent event) {
        if (this.getSelected() != null && idContactController.getSelected() == null) {
            idAgentController.setSelected(this.getSelected().getIdAgent());
        }
    }

    /**
     * Sets the "items" attribute with a collection of Has entities that are
     * retrieved from Product?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Has page
     */
    public String navigateHasList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Has_items", this.getSelected().getHasList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/has/index";
    }

    public String navigateQuoteNoteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Quote_Note_items", this.getSelected().getQuoteNoteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quoteNote/index";
    }

    public String navigateQuoteStatusList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Quote_Status_items", this.getSelected().getQuoteStatusList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quoteStatus/index";
    }

    public String navigateManageList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Manage_items", this.getSelected().getManageList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/manage/index";
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
