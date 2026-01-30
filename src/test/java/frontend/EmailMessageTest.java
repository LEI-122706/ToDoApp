// src/test/java/frontend/EmailMessageTest.java
package frontend;

import com.example.emails.EmailMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Barge's test class (LEI-122676)")
public class EmailMessageTest {

    private static EmailMessage emailMessage;
    private static Date receivedDate;

    @BeforeAll
    public static void createEmail() {
        receivedDate = new Date();
        emailMessage = new EmailMessage("tiago", "ISCTE", "content body", receivedDate);
    }

    @Test
    public void testGetFrom() {
        assertEquals("tiago", emailMessage.getFrom());
    }

    @Test
    public void testGetSubject() {
        assertEquals("ISCTE", emailMessage.getSubject());
    }

    @Test
    public void testGetContent() {
        assertEquals("content body", emailMessage.getContent());
    }

    @Test
    public void testGetReceivedDate() {
        assertEquals(receivedDate, emailMessage.getReceivedDate());
    }
}
