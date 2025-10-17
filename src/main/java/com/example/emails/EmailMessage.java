package com.example.emails;

import java.util.Date;

public class EmailMessage {
    private final String from;
    private final String subject;
    private final String content;
    private final Date receivedDate;

    public EmailMessage(String from, String subject, String content, Date receivedDate) {
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.receivedDate = receivedDate;
    }

    public String getFrom() { return from; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public Date getReceivedDate() { return receivedDate; }
}
