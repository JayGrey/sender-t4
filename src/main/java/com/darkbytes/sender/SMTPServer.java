package com.darkbytes.sender;

import javax.mail.*;
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
            return Collections.emptyList();
        }

        try {
            int maxAttachments = Integer.valueOf(
                    senderProps.getProperty("email.max_attachments"));

            int from = 0;
            int to = Math.min(maxAttachments, task.files.size());

            while (from < task.files.size()) {
                MimeMessage message = new MimeMessage(session);
                message.setContent(
                        formAttachments(task.files.subList(from, to)));
                from = to;
                to = Math.min(task.files.size(), to + maxAttachments);

                message.setFrom((String) senderProps.get("smtp.from"));

                StringBuilder emails = new StringBuilder();
                task.emails.forEach(e -> emails.append(e).append(","));

                message.setRecipients(Message.RecipientType.TO, emails
                        .toString());
                message.setSubject(task.subject);
                message.setSentDate(new Date());

                Transport.send(message);
            }

            for (File file : task.files) {
                logger.log(Level.INFO, "file {0} sent to {1}",
                        new Object[]{file, task.emails});
            }

            return task.files;

        } catch (MessagingException | IOException e) {
            logger.log(Level.SEVERE, "exception", e);
            return Collections.emptyList();
        }
    }

    private Multipart formAttachments(List<File> files)
            throws IOException, MessagingException {

        Multipart multipart = new MimeMultipart();
        for (File file : files) {
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.attachFile(file);
            bodyPart.setFileName(file.getName());
            multipart.addBodyPart(bodyPart);
        }

        return multipart;
    }

    private boolean checkArg(Task task) {
        return task != null && task.emails != null && task.files != null &&
                task.emails.size() != 0 && task.files.size() != 0;
    }
}
