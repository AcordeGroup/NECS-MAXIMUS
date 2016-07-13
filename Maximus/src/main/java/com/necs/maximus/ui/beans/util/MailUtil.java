/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans.util;

import com.necs.maximus.ui.beans.ViewQuoteController;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author luis
 */
public class MailUtil {

    public static final String CONTENT_TYPE_TEXT_HTML = "text/html; charset=utf-8";
    public static final String PROPERTY_KEY_MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String PROPERTY_KEY_MAIL_USER = "mail.user";
    public static final String PROPERTY_KEY_MAIL_PASSWORD = "mail.password";
    private Properties properties;

    public MailUtil(Properties properties) {
        this.properties = properties;
    }

    private Session getSession() {
        if (properties.containsKey(PROPERTY_KEY_MAIL_SMTP_AUTH) && Boolean.valueOf(properties.getProperty(PROPERTY_KEY_MAIL_SMTP_AUTH))) {
            //return Session.getDefaultInstance(

            Authenticator aut = new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            properties.getProperty(PROPERTY_KEY_MAIL_USER),
                            properties.getProperty(PROPERTY_KEY_MAIL_PASSWORD));
                }
            };
            return Session.getInstance(properties, aut);
        }

        return Session.getDefaultInstance(properties);
    }

    public void sendHTMLEmail(MailBean mail) throws MessagingException {
        Address[] toAddresses;
        Address[] ccAddresses;
        Address[] bccAddresses;
        // Lists to array
        if (mail.getTo() != null) {
            toAddresses = new Address[mail.getTo().size()];
            for (int i = 0; i < mail.getTo().size(); i++) {
                toAddresses[i] = new InternetAddress(mail.getTo().get(i));
            }
        } else {
            toAddresses = new Address[0];
        }
        if (mail.getCc() != null) {
            ccAddresses = new Address[mail.getCc().size()];
            for (int i = 0; i < mail.getCc().size(); i++) {
                ccAddresses[i] = new InternetAddress(mail.getCc().get(i));
            }
        } else {
            ccAddresses = new Address[0];
        }
        if (mail.getBcc() != null) {
            bccAddresses = new Address[mail.getBcc().size()];
            for (int i = 0; i < mail.getBcc().size(); i++) {
                bccAddresses[i] = new InternetAddress(mail.getBcc().get(i));
            }
        } else {
            bccAddresses = new Address[0];
        }
        Session session = getSession();
        session.setDebug(true);

        this.sendHTMLEmail(
                session,
                mail.getFrom(),
                toAddresses,
                ccAddresses,
                bccAddresses,
                mail.getSubject(),
                mail.getBody(),
                mail.getNameFlie(),
                mail.getFile(),
                MailUtil.CONTENT_TYPE_TEXT_HTML);
    }

    private void sendHTMLEmail(
            Session session,
            String from,
            Address[] to,
            Address[] cc,
            Address[] bcc,
            String subject,
            String messageContent,
            String nameFile,
            byte[] file,
            String contentType) throws MessagingException {

        try {

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            Multipart multiPart = new MimeMultipart("alternative");

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipients(Message.RecipientType.TO, to);

            // Set CC: header field of the header.
            message.addRecipients(Message.RecipientType.CC, cc);

            // Set BCC: header field of the header.
            message.addRecipients(Message.RecipientType.BCC, bcc);

            // Set Subject: header field
            message.setSubject(subject);

            // Send the actual HTML message, as big as you like
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(messageContent, contentType);

//            if (nameFile != null && file != null) {
//                MimeBodyPart attachment = new MimeBodyPart();
//                InputStream attachmentDataStream = new ByteArrayInputStream(file);
//                attachment.setFileName(nameFile);
//                attachment.setHeader("Content-Type", "application/pdf");
//                attachment.setContent(attachmentDataStream, "application/pdf");
//                multiPart.addBodyPart(attachment);
//            }
//            MimeBodyPart attachment = new MimeBodyPart();
//            String payload = Base64.getEncoder().encodeToString(file);
//            attachment.setText(payload);
//            attachment.setHeader("Content-Type", "application/pdf"); // Use x-pdf instead for backward compatibility with old / legacy software
//            attachment.setHeader("Content-Transfer-Encoding", "base64");
//            multiPart.addBodyPart(attachment);
            //construct the pdf body part
            DataSource dataSource = new ByteArrayDataSource(file, "application/pdf");
            MimeBodyPart attachment = new MimeBodyPart();
            attachment.setDataHandler(new DataHandler(dataSource));
            attachment.setFileName(nameFile);

            multiPart.addBodyPart(attachment);
            multiPart.addBodyPart(htmlPart);

            message.setContent(multiPart);

//            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
//            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
//            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
//            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
//            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
//            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
//            CommandMap.setDefaultCommandMap(mc);
//            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            // Send message
            Transport.send(message);

        } catch (AddressException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.INFO, "", ex);
            throw ex;
        } catch (MessagingException ex) {
            Logger.getLogger(ViewQuoteController.class.getName()).log(Level.INFO, "", ex);
            throw ex;
        }

    }

}
