package com.darkbytes.sender;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class MailSender {

    private static MailSender instance;
    private static Logger logger = Logger.getLogger(Client.class.getName());
    private Properties senderProperties;
    private Session session;

    private MailSender(Properties properties) {
        senderProperties = properties;
        Properties SMTPProps = new Properties();
        SMTPProps.put("mail.smtp.host", (String) senderProperties.get("smtp.host"));
        SMTPProps.put("mail.smtp.port", (Integer) senderProperties.get("smtp.port"));
        session = Session.getInstance(SMTPProps, null);
        session.setDebug(true);
    }

    static MailSender getInstance(Properties properties) {
        if (instance == null) {
            instance = new MailSender(properties);
        }

        return instance;
    }

    boolean send(String subject, String[] email, File[] files) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom((String)senderProperties.get("smtp.from"));
            InternetAddress[] addresses = new InternetAddress[email.length];
            for (int i = 0; i < email.length; i++) {
                addresses[i] = new InternetAddress(email[i]);
            }

            message.setRecipients(Message.RecipientType.TO, addresses);

            message.setSubject(subject);
            Multipart multipart = new MimeMultipart();
            for (File file : files) {
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.attachFile(file);
                bodyPart.setFileName(file.getName());
                multipart.addBodyPart(bodyPart);
            }

            message.setContent(multipart);
            message.setSentDate(new Date());

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "mail exception", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO exception", e);
        }
        return false;
    }

    void close() {
        // release resources
    }
}
