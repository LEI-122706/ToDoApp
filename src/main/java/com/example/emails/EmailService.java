package com.example.emails;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.vaadin.flow.server.VaadinSession;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Service
public class EmailService {


    private final String imapHost;
    private final String imapPort;
    private final boolean imapSsl;

    public EmailService(JavaMailSender mailSender) {
        this.imapHost = "imap.gmail.com"; // or use @Value if you want to keep host/port in properties
        this.imapPort = "993";
        this.imapSsl = true;
    }

    public JavaMailSenderImpl createMailSender(String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }

    public void sendEmail(String from, String to, String subject, String body) throws MessagingException {
        JavaMailSenderImpl sender = createMailSender(getUsername(), getPassword());
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, false);
        sender.send(message);
    }


    public List<EmailMessage> fetchInbox() throws MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", imapHost);
        props.put("mail.imap.port", imapPort);
        props.put("mail.imap.ssl.enable", String.valueOf(imapSsl));

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect(imapHost, Integer.parseInt(imapPort), getUsername(), getPassword());

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();
        List<EmailMessage> results = new ArrayList<>();
        for (Message msg : messages) {
            String from = msg.getFrom() != null && msg.getFrom().length > 0 ? msg.getFrom()[0].toString() : "";
            String subject = msg.getSubject() != null ? msg.getSubject() : "";
            String content = extractText(msg);
            results.add(new EmailMessage(from, subject, content, msg.getReceivedDate()));
        }

        inbox.close(false);
        store.close();
        return results;
    }

    private String extractText(Part p) throws MessagingException, IOException {
        Object content = p.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            MimeMultipart mp = (MimeMultipart) content;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                Object partContent = bp.getContent();
                if (partContent instanceof String) {
                    sb.append((String) partContent);
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static String getUsername() {
        return (String) VaadinSession.getCurrent().getAttribute("email");
    }

    public static String getPassword() {
        return (String) VaadinSession.getCurrent().getAttribute("password");
    }
}