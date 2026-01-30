package com.example.emails;

import java.util.Date;
import java.util.Objects;

/**
 * Represents an immutable email message with sender, subject, body content and the received date.
 *
 * <p>Instances of this class are immutable: the textual fields are {@code final} and the
 * {@link Date} field is defensively copied on construction and access to avoid exposing
 * internal mutable state.</p>
 *
 * @since 1.0
 */
public class EmailMessage {
    /**
     * Sender of the email (may be {@code null}).
     */
    private final String from;

    /**
     * Subject of the email (may be {@code null}).
     */
    private final String subject;

    /**
     * Body content of the email (may be {@code null}).
     */
    private final String content;

    /**
     * Date/time when the message was received. May be {@code null}.
     * Accessors return defensive copies to preserve immutability.
     */
    private final Date receivedDate;

    /**
     * Constructs an EmailMessage.
     *
     * @param from         sender identifier or address, may be {@code null}
     * @param subject      subject line, may be {@code null}
     * @param content      message body, may be {@code null}
     * @param receivedDate date/time when the message was received; a defensive copy is created
     *                     (if {@code null} no copy is made)
     */
    public EmailMessage(String from, String subject, String content, Date receivedDate) {
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.receivedDate = (receivedDate == null) ? null : new Date(receivedDate.getTime());
    }

    /**
     * Returns the sender of the email.
     *
     * @return the sender, may be {@code null}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the subject of the email.
     *
     * @return the subject, may be {@code null}
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the content (body) of the email.
     *
     * @return the content, may be {@code null}
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns a defensive copy of the received date.
     *
     * @return a new {@link Date} instance equal to the original received date, or {@code null}
     *         if the original date was {@code null}
     */
    public Date getReceivedDate() {
        return (receivedDate == null) ? null : new Date(receivedDate.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailMessage)) return false;
        EmailMessage that = (EmailMessage) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(content, that.content) &&
                Objects.equals(receivedDate, that.receivedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, subject, content, receivedDate);
    }

    @Override
    public String toString() {
        return "EmailMessage{" +
                "from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", receivedDate=" + receivedDate +
                '}';
    }
}