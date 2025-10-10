package com.example.emails;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.imap.host}")
    private String imapHost;

    @Value("${mail.imap.port}")
    private String imapPort;

    @Value("${mail.imap.user}")
    private String imapUser;

    @Value("${mail.imap.password}")
    private String imapPassword;

    @Value("${mail.imap.ssl.enable:true}")
    private boolean imapSsl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    public List<EmailMessage> fetchInbox() throws MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", imapHost);
        props.put("mail.imap.port", imapPort);
        props.put("mail.imap.ssl.enable", String.valueOf(imapSsl));

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect(imapHost, Integer.parseInt(imapPort), imapUser, imapPassword);

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
}

