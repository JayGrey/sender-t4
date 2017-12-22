package com.darkbytes.sender;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SMTPServer extends Server {

    private static Logger logger = Logger.getLogger(Main.class.getName());
    private Properties senderProps;
    private Session session;

    SMTPServer(Properties properties) {
        senderProps = properties;
        Properties SMTPProps = new Properties();
        SMTPProps.put("mail.smtp.host", senderProps.get("smtp.host"));
        SMTPProps.put("mail.smtp.port",
                Integer.valueOf((String) senderProps.get("smtp.port")));
        session = Session.getInstance(SMTPProps, null);
        if (senderProps.getProperty("email.debug").equalsIgnoreCase("true")) {
            session.setDebug(true);
        }
    }

    @Override
    public List<File> send(Task task) {
        if (!checkArg(task)) {
            logger.log(Level.WARNING, "error in args");
            return null;
        }

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom((String) senderProps.get("smtp.from"));

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
            for (File file : task.files) {
                logger.log(Level.INFO, "file {0} sent", file);
            }

            return task.files;
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "mail exception", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO exception", e);
        }
        return Collections.emptyList();
    }

    private boolean checkArg(Task task) {
        return task != null && task.emails != null && task.files != null &&
                task.emails.size() != 0 && task.files.size() != 0;
    }
}
