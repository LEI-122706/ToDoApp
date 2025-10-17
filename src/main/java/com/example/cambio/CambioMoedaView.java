package com.example.cambio;

import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Route(value = "converter", layout = MainLayout.class)
public class CambioMoedaView extends VerticalLayout {

    private static final String API_KEY = "53e1a607c70fa2dc599162be"; //from ExchangeRate-API
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/";

    public CambioMoedaView() {
        List<String> currencies = Arrays.asList("USD", "EUR", "GBP", "JPY", "BRL", "AUD", "CAD", "MOP", "CNY");

        ComboBox<String> fromCurrency = new ComboBox<>("From Currency");
        fromCurrency.setItems(currencies);

        ComboBox<String> toCurrency = new ComboBox<>("To Currency");
        toCurrency.setItems(currencies);

        NumberField amountField = new NumberField("Amount");
        amountField.setValue(1.0);

        NumberField rateField = new NumberField("Exchange Rate");
        rateField.setReadOnly(true);

        TextField resultField = new TextField("Converted Amount");
        resultField.setReadOnly(true);

        Button convertButton = new Button("Convert");
        convertButton.setEnabled(false);

        // Auto-fetch rate when both currencies are selected
        fromCurrency.addValueChangeListener(event -> {
            if (fromCurrency.getValue() != null && toCurrency.getValue() != null) {
                fetchExchangeRate(fromCurrency.getValue(), toCurrency.getValue(), rateField, convertButton);
            }
        });

        toCurrency.addValueChangeListener(event -> {
            if (fromCurrency.getValue() != null && toCurrency.getValue() != null) {
                fetchExchangeRate(fromCurrency.getValue(), toCurrency.getValue(), rateField, convertButton);
            }
        });

        convertButton.addClickListener(event -> {
            String from = fromCurrency.getValue();
            String to = toCurrency.getValue();
            Double amount = amountField.getValue();
            Double rate = rateField.getValue();

            if (from == null || to == null || amount == null || rate == null) {
                Notification.show("Please fill all fields.", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (amount <= 0) {
                Notification.show("Amount must be positive.", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (from.equals(to)) {
                Notification.show("Please select different currencies.", 3000, Notification.Position.MIDDLE);
                return;
            }

            double result = amount * rate;
            resultField.setValue(String.format("%.2f %s", result, to));
            Notification.show(String.format("Converted %.2f %s to %.2f %s", amount, from, result, to));
        });

        add(fromCurrency, toCurrency, amountField, rateField, resultField, convertButton);
    }

    private void fetchExchangeRate(String from, String to, NumberField rateField, Button convertButton) {
        convertButton.setEnabled(false);
        rateField.clear();

        if (from.equals(to)) {
            rateField.setValue(1.0);
            convertButton.setEnabled(true);
            return;
        }

        // Fetch in a separate thread to avoid blocking UI
        new Thread(() -> {
            try {
                String urlString = API_URL + from + "/" + to;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Parse JSON response
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    double rate = jsonObject.get("conversion_rate").getAsDouble();

                    // Update UI in UI thread
                    getUI().ifPresent(ui -> ui.access(() -> {
                        rateField.setValue(rate);
                        convertButton.setEnabled(true);
                        Notification.show("Exchange rate updated!", 2000, Notification.Position.BOTTOM_START);
                    }));
                } else {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        Notification.show("Failed to fetch exchange rate. Please try again.", 3000, Notification.Position.MIDDLE);
                        convertButton.setEnabled(true);
                    }));
                }
            } catch (Exception e) {
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
                    convertButton.setEnabled(true);
                }));
            }
        }).start();
    }
}
