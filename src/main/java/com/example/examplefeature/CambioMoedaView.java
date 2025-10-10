package com.example.examplefeature;

import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;

@Route(value = "converter", layout = MainLayout.class)
public class CambioMoedaView extends VerticalLayout {

    public CambioMoedaView() {
        List<String> currencies = Arrays.asList("USD", "EUR", "GBP", "JPY", "BRL", "AUD", "CAD");

        ComboBox<String> fromCurrency = new ComboBox<>("From Currency");
        fromCurrency.setItems(currencies);

        ComboBox<String> toCurrency = new ComboBox<>("To Currency");
        toCurrency.setItems(currencies);

        NumberField amountField = new NumberField("Amount");
        NumberField rateField = new NumberField("Exchange Rate");

        Button convertButton = new Button("Convert", event -> {
            String from = fromCurrency.getValue();
            String to = toCurrency.getValue();
            Double amount = amountField.getValue();
            Double rate = rateField.getValue();

            if (from == null || to == null || amount == null || rate == null) {
                Notification.show("Please fill all fields.");
                return;
            }

            double result = amount * rate;
            Notification.show("Converted " + amount + " " + from + " to " + result + " " + to);
        });

        add(fromCurrency, toCurrency, amountField, rateField, convertButton);
    }
}
