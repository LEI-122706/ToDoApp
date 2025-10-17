package com.example.emails.ui;

import java.util.List;

import com.example.emails.EmailMessage;
import com.example.emails.EmailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;

@Route("email")
@PageTitle("Email")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Email")
class EmailView extends Main implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String email = (String) VaadinSession.getCurrent().getAttribute("email");
        String password = (String) VaadinSession.getCurrent().getAttribute("password");

        if (email == null || password == null) {
            event.forwardTo("email_login"); // "" is the route for LoginView
        }
    }

    @Autowired
    private final EmailService emailService;
    private final Grid<EmailMessage> grid = new Grid<>(EmailMessage.class);

    private final String FIELD_WIDTH = "1000px";

    @Autowired
    public EmailView(EmailService emailService) {
        this.emailService = emailService;

        TextField toField = new TextField("To");
        toField.setWidth(FIELD_WIDTH);
        TextField subjectField = new TextField("Subject");
        subjectField.setWidth(FIELD_WIDTH);
        TextArea bodyArea = new TextArea("Body");
        bodyArea.setWidth(FIELD_WIDTH);

        bodyArea.setHeight("200px");
        bodyArea.setWidthFull();

        Button sendBtn = new Button("Send");
        sendBtn.addClickListener(e -> {
            String fromValue = EmailService.getUsername();
            String toValue = toField.getValue();
            String subjectValue = subjectField.getValue();
            String bodyValue = bodyArea.getValue();

            // Simple email validation
            if (toValue == null || !toValue.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
                Notification.show("Please enter a valid email address.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (subjectValue == null || subjectValue.isEmpty()) {
                Notification.show("Subject cannot be empty.", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                emailService.sendEmail(fromValue, toValue, subjectValue, bodyValue);
                Notification.show("Email sent successfully!", 3000, Notification.Position.MIDDLE);
                toField.clear(); subjectField.clear(); bodyArea.clear();
            } catch (Exception ex) {
                Notification.show("Failed to send email: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        Button refreshBtn = new Button("Refresh Inbox", e -> loadInbox());

        VerticalLayout emailForm = new VerticalLayout();
        emailForm.setPadding(true);
        emailForm.setSpacing(true);
        emailForm.setWidth("400px");
        emailForm.add(
            toField,
            subjectField,
            bodyArea,
            new HorizontalLayout(sendBtn, refreshBtn)
        );

        grid.removeAllColumns();
        grid.addColumn(EmailMessage::getFrom).setHeader("From").setFlexGrow(1);
        grid.addColumn(EmailMessage::getSubject).setHeader("Subject").setFlexGrow(2);
        grid.addColumn(EmailMessage::getReceivedDate).setHeader("Received").setFlexGrow(1);

        grid.setSizeFull();

        grid.addItemClickListener(event -> showEmailDialog(event.getItem()));

        VerticalLayout mainLayout = new VerticalLayout(emailForm, grid);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setPadding(true);
        mainLayout.setFlexGrow(1, grid);

        add(mainLayout);
        setSizeFull();

        loadInbox();
    }

    private void showEmailDialog(EmailMessage email) {
        Dialog dialog = new Dialog();
        dialog.setWidth("700px");
        dialog.setHeight("500px");

        Span from = new Span("From: " + email.getFrom());
        Span subject = new Span("Subject: " + email.getSubject());
        Span date = new Span("Received: " + email.getReceivedDate());



































        VerticalLayout header = new VerticalLayout(from, subject, date);
        header.setSpacing(false);
        header.setPadding(false);
        header.getStyle().set("font-size", "var(--lumo-font-size-s)");
        header.getStyle().set("color", "var(--lumo-secondary-text-color)");

        Paragraph content = new Paragraph(email.getContent());
        content.getStyle().set("white-space", "pre-wrap");
        content.setWidthFull();

        VerticalLayout layout = new VerticalLayout(header, content);
        layout.setSizeFull();

        dialog.add(layout);
        dialog.open();
    }



    private void loadInbox() {
        try {
            List<EmailMessage> messages = emailService.fetchInbox();
            grid.setItems(messages.reversed());
        } catch (Exception ex) {
            grid.setItems();
        }
    }
}