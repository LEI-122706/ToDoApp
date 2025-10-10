package com.example.QRcodes.ui;



import com.example.base.ui.component.ViewToolbar;
import com.example.QRcodes.QRcode;
import com.example.QRcodes.QRcodeService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("QRcode")
@PageTitle("QRcode")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "QRcode")
class QRcodeView extends Main {

    private final QRcodeService qrcodeService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<QRcode> qrcodeGrid;

    QRcodeView(QRcodeService QRcodeService) {
        this.qrcodeService = QRcodeService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("QRcode description");
        description.setMaxLength(QRcode.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createQRcode());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        qrcodeGrid = new Grid<>();
        qrcodeGrid.setItems(query -> qrcodeService.list(toSpringPageRequest(query)).stream());
        qrcodeGrid.addColumn(QRcode::getDescription).setHeader("Description");
        qrcodeGrid.addColumn(QRcode -> Optional.ofNullable(QRcode.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        qrcodeGrid.addColumn(QRcode -> dateTimeFormatter.format(QRcode.getCreationDate())).setHeader("Creation Date");
        qrcodeGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new ViewToolbar("QRcode List", ViewToolbar.group(description, dueDate, createBtn)));
        add(qrcodeGrid);
    }

    private void createQRcode() {
        qrcodeService.createQRcode(description.getValue(), dueDate.getValue());
        qrcodeGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("QRcode added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

}

