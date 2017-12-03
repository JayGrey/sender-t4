package com.darkbytes.sender;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MailSender {

    private static MailSender instance;
    private static Logger logger = Logger.getLogger(Main.class.getName());
    private Properties senderProperties;
    private Session session;

    MailSender(Properties properties) {
        senderProperties = properties;
        Properties SMTPProps = new Properties();
        SMTPProps.put("mail.smtp.host",
                (String) senderProperties.get("smtp.host"));
        SMTPProps.put("mail.smtp.port",
                (Integer) senderProperties.get("smtp.port"));
        session = Session.getInstance(SMTPProps, null);
        session.setDebug(true);
    }

    void send(Task task) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom((String) senderProperties.get("smtp.from"));

            InternetAddress[] addresses =
                    new InternetAddress[task.emails.size()];

            for (int i = 0; i < task.emails.size(); i++) {
                addresses[i] = new InternetAddress(task.emails.get(i));
            }

            message.setRecipients(Message.RecipientType.TO, addresses);

            message.setSubject(task.subject);
            Multipart multipart = new MimeMultipart();
            for (File file : task.files) {
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.attachFile(file);
                bodyPart.setFileName(file.getName());
                multipart.addBodyPart(bodyPart);
            }

            message.setContent(multipart);
            message.setSentDate(new Date());

            Transport.send(message);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "mail exception", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO exception", e);
        }
    }

    public void processTasks(List<Task> tasks) {

        for (Task task : tasks) {
            send(task);
        }
    }
}
