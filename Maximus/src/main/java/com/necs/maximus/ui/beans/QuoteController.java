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

    private static final String PATH_IMAGE = "logoNecsPdf.png";

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
                QuoteStatus qs = quote.getQuoteStatusList().get(0);
                if (qs.getStatus().equals(StatusType.READY.getName())) {
                    qs.setEndDate(new Date());
                    quoteStatusFacade.edit(qs);
                }
                QuoteStatus statusNew = new QuoteStatus();
                statusNew.setIdQuote(quote);
                statusNew.setInitDate(new Date());
                statusNew.setEndDate(new Date());
                statusNew.setStatus(StatusType.SENT.getName());
                quoteStatusFacade.create(statusNew);
                init();
            }

            // aqui envio quote..... conversar con Carlos para conocer el flujo
            exportPdf(quote, OperationType.SEND.getOperationName());

            RequestContext.getCurrentInstance().execute("PF('dialogSuccess').show();");
        } catch (MessagingException e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));

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
            Font fontHeader = FontFactory.getFont("arial", // fuente
                    12, // tamaño
                    Font.BOLD, // estilo
                    BaseColor.BLACK);
            Font fontDefault = FontFactory.getFont("arial", // fuente
                    10, // tamaño
                    Font.NORMAL, // estilo
                    BaseColor.BLACK);

            Float defaultPadding = 5f;
            Float defaultSpacing = 15f;
             //  *****  --------------------  *******

            //section title........
            PdfPTable tablaTitle = new PdfPTable(3);
            tablaTitle.setWidths(new int[]{40, 35, 25});
            tablaTitle.setWidthPercentage(100);
            tablaTitle.setHorizontalAlignment(Element.ALIGN_CENTER);

            //sumna title company 
            tablaTitle.addCell(createCell(bundle.getString("titleCompany"), null, null, fontHeader, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER));

            //suma image company
            tablaTitle.addCell(createCellImage(PATH_IMAGE));

            //suma text sales quote
            tablaTitle.addCell(createCell(bundle.getString("sale_quote").concat("N° ").concat(quote.getIdQuote().toString()), null, null, fontHeader, null, Element.ALIGN_RIGHT, defaultPadding, PdfPCell.NO_BORDER));
              // fin section title........

            //section billTo and Shippping To
            PdfPTable tablaBillTo = new PdfPTable(1);
            tablaBillTo.setWidths(new int[]{1});
            tablaBillTo.setWidthPercentage(98);
            tablaBillTo.setHorizontalAlignment(Element.ALIGN_LEFT);

            //sumna encabezado bill_to..
            tablaBillTo.addCell(createCell(bundle.getString("bill_to"), 2, null, fontHeader, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, defaultPadding, null));

            //sumna information bill_to..
            tablaBillTo.addCell(createCell(quote.getShipping_to(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER));

            PdfPTable tablaShippingTo = new PdfPTable(1);
            tablaShippingTo.setWidths(new int[]{1});
            tablaShippingTo.setWidthPercentage(98);
            tablaShippingTo.setHorizontalAlignment(Element.ALIGN_RIGHT);

            //sumna encabezado shipping_to..
            tablaShippingTo.addCell(createCell(bundle.getString("shipping_to"), 2, null, fontHeader, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, defaultPadding, null));

            //Suma la información shipping_to
            tablaShippingTo.addCell(createCell(quote.getShipping_to(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, PdfPCell.NO_BORDER));

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

            // inicio section Customer
            PdfPTable tablaCustomer = new PdfPTable(2);
            tablaCustomer.setWidths(new int[]{28, 72});
            tablaCustomer.setWidthPercentage(100);
            tablaCustomer.setSpacingBefore(defaultSpacing);

            //sumna encabezado table customer..
            tablaCustomer.addCell(createCell(bundle.getString("customer_information"), 2, null, fontHeader, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, defaultPadding, null));

            //Suma la información del customer
            tablaCustomer.addCell(createCell(bundle.getString("name"), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaCustomer.addCell(createCell(quote.getIdCustomer().getPrimaryContact(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaCustomer.addCell(createCell(bundle.getString("email"), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaCustomer.addCell(createCell(quote.getIdCustomer().getPrimaryEmail(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaCustomer.addCell(createCell(bundle.getString("phone"), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaCustomer.addCell(createCell(quote.getIdCustomer().getPrimaryPhoneNumber(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
             // fin  section Customer

            // inicio section Sales
            PdfPTable tablaSales = new PdfPTable(2);
            tablaSales.setWidths(new int[]{28, 72});
            tablaSales.setWidthPercentage(100);
            tablaSales.setSpacingBefore(defaultSpacing);

            //sumna encabezado table sales..
            tablaSales.addCell(createCell(bundle.getString("sales_information"), 2, null, fontHeader, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, defaultPadding, null));

            //Suma la información del sales
            tablaSales.addCell(createCell(bundle.getString("name"), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaSales.addCell(createCell(quote.getIdAgent().getName(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaSales.addCell(createCell(bundle.getString("email"), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            tablaSales.addCell(createCell(quote.getIdAgent().getEmail(), null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            // fin section Sales

            PdfPTable tablePart = new PdfPTable(6);
            tablePart.setWidths(new int[]{1, 1, 1, 1, 1, 1});
            tablePart.setWidthPercentage(100);
            tablePart.setSpacingBefore(defaultSpacing);
            String[] quoteHeaders = {
                bundle.getString("part_number"),
                bundle.getString("requested"),
                bundle.getString("found"),
                bundle.getString("target_price"),
                bundle.getString("suggested_sales"),
                bundle.getString("extended")
            };

            //suma encabezado
            tablePart.addCell(createCell(quoteHeaders[0], null, 2, fontHeader, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, defaultPadding, null));
            tablePart.addCell(createCell(bundle.getString("quantity"), 2, null, fontHeader, null, Element.ALIGN_CENTER, defaultPadding, null));
            tablePart.addCell(createCell(bundle.getString("prices"), 3, null, fontHeader, null, Element.ALIGN_CENTER, defaultPadding, null));

            for (String header : quoteHeaders) {
                if (!header.equals(bundle.getString("part_number"))) {
                    tablePart.addCell(createCell(header, null, null, fontDefault, null, Element.ALIGN_CENTER, defaultPadding, null));
                }
            }

            //Suma la información del quote
            for (Has has : quote.getHasList()) {
                tablePart.addCell(createCell(has.getProduct() != null ? has.getProduct().getPartNumber() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
                tablePart.addCell(createCell(has.getQtyRequested() != null ? has.getQtyRequested().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
                tablePart.addCell(createCell(has.getQtyFound() != null ? has.getQtyFound().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
                tablePart.addCell(createCell(has.getCustomerTargetPrice() != null ? has.getCustomerTargetPrice().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
                tablePart.addCell(createCell(has.getSuggestedSalesPrice() != null ? has.getSuggestedSalesPrice().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
                tablePart.addCell(createCell(has.getExtended() != null ? has.getExtended().toString() : "", null, null, fontDefault, null, Element.ALIGN_LEFT, defaultPadding, null));
            }
            document.add(tablaTitle);
            document.add(tablaContent);
            document.add(tablaCustomer);
            document.add(tablaSales);
            document.add(tablePart);
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

                    List<String> to = new ArrayList<>();
                    to.add("ing.castaneda.luis@gmail.com");

                    MailBean mail = new MailBean();
                    mail.setFrom("pruebasemailmaximus@gmail.com");
                    mail.setTo(to);
                    mail.setNameFlie(filename);
                    mail.setSubject("Subject");
                    mail.setBody("<table width=\'400\' cellspacing=\'0\' cellpadding=\'0\' border=\'0\' align=\'center\' style=\'width:400px;margin:0 auto\'><tr><td valign=\'top\'><h2 style=\'font-family:sans-serif;font-weight:normal;margin:0 0 24px 0;text-align:center\'>Bienvenido a NECS</h2><p style=\'font-family:sans-serif;font-size:14px;font-weight:normal;margin:0 0 24px 0;text-align:center\'>Dear customer, Please see the attached quote request.<b>" + quote.getIdQuote() + "</b></p> </td></tr><tr><td width=\'100%\' height=\'100%\' cellspacing=\'0\' cellpadding=\'0\' border=\'0\'><p style=\'font-family:sans-serif;font-weight:normal;margin:0;text-align:center;color:#8a9ba8;font-size:11px;line-height:13px;width:400px;\'>This is an automated email .<br/> If you received this email in error , ignore it.</p></td></tr></table>");
                    mail.setFile(fileA);

                    sendMail(mail);
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
            cell = new PdfPCell(img, true);
            cell.setBorder(PdfPCell.NO_BORDER);

        } catch (BadElementException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cell;
    }

    private PdfPCell createCell(String text, Integer span, Integer rSpan, Font font, BaseColor backColor, Integer halign, Float padding, Integer border) {

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

        return cell;
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
