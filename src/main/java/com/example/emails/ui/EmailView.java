package com.example.emails.ui;

import java.util.List;

import com.example.emails.EmailMessage;
import com.example.emails.EmailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("email")
@PageTitle("Email")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Email")
class EmailView extends Main {

    private final EmailService emailService;
    private final Grid<EmailMessage> grid = new Grid<>(EmailMessage.class);

    @Autowired
    public EmailView(EmailService emailService) {
        this.emailService = emailService;

        TextField toField = new TextField("To");
        TextField subjectField = new TextField("Subject");
        TextArea bodyArea = new TextArea("Body");
        bodyArea.setHeight("200px");
        bodyArea.setWidthFull();

        Button sendBtn = new Button("Send", e -> {
            emailService.sendSimpleEmail(toField.getValue(), subjectField.getValue(), bodyArea.getValue());
            toField.clear(); subjectField.clear(); bodyArea.clear();
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
        grid.addColumn(EmailMessage::getFrom).setHeader("From");
        grid.addColumn(EmailMessage::getSubject).setHeader("Subject");
        grid.addColumn(EmailMessage::getReceivedDate).setHeader("Received");
        grid.addColumn(EmailMessage::getContent).setHeader("Content").setAutoWidth(true);

        VerticalLayout mainLayout = new VerticalLayout(emailForm, grid);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);

        add(mainLayout);
        loadInbox();
    }

    private void loadInbox() {
        try {
            List<EmailMessage> messages = emailService.fetchInbox();
            grid.setItems(messages);
        } catch (Exception ex) {
            grid.setItems();
        }
    }
}