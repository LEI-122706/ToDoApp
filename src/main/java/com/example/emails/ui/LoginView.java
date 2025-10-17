package com.example.emails.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Anchor;

@Route("email_login")
@PageTitle("Email login")
public class LoginView extends Main {

    public LoginView() {
        TextField emailField = new TextField("Email");
        emailField.setWidthFull();
        PasswordField passwordField = new PasswordField("Password (use a generated app password)");
        passwordField.setWidthFull();
        Button loginBtn = new Button("Login");

        Anchor appPasswordLink = new Anchor(
                "https://myaccount.google.com/apppasswords",
                "Generate an App Password (Google Account)"
        );
        appPasswordLink.setTarget("_blank"); // opens in new tab


        loginBtn.addClickListener(e -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();

            if (email == null || !email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
                Notification.show("Please enter a valid email address.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (password == null || password.isEmpty()) {
                Notification.show("Password cannot be empty.", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Store credentials in session
            VaadinSession.getCurrent().setAttribute("email", email);
            VaadinSession.getCurrent().setAttribute("password", password);

            // Navigate to EmailView
            getUI().ifPresent(ui -> ui.navigate("email"));
        });

        VerticalLayout layout = new VerticalLayout(emailField, passwordField, appPasswordLink, loginBtn);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setWidthFull();
        add(layout);
    }
}
