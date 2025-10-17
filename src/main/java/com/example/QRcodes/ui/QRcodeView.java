package com.example.QRcodes.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.QRcodes.QRcode;
import com.example.QRcodes.QRcodeService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
@Menu(order = 0, icon = "vaadin:qrcode", title = "QRcode")
public class QRcodeView extends Main {

    private final QRcodeService qrcodeService;

    final Grid<QRcode> qrcodeGrid;
    final Button qrBtn = new Button("Gerar QR Code");
    final Image qrImage = new Image();
    final Anchor downloadLink = new Anchor(); // opcional: download do PNG

    public QRcodeView(QRcodeService QRcodeService) {
        this.qrcodeService = QRcodeService;

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getLocale()).withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        qrcodeGrid = new Grid<>();
        qrcodeGrid.setItems(query -> qrcodeService.list(toSpringPageRequest(query)).stream());
        qrcodeGrid.addColumn(QRcode::getDescription).setHeader("Description");
        qrcodeGrid.addColumn(qr -> Optional.ofNullable(qr.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        qrcodeGrid.addColumn(qr -> dateTimeFormatter.format(qr.getCreationDate())).setHeader("Creation Date");
        qrcodeGrid.setSizeFull();

        qrImage.setWidth("200px");
        qrImage.setHeight("200px");
        qrImage.setAlt("QR code da tarefa selecionada");

        // Botão desativado até haver seleção
        qrBtn.setEnabled(false);
        qrcodeGrid.asSingleSelect().addValueChangeListener(e -> qrBtn.setEnabled(e.getValue() != null));

        qrBtn.addClickListener(event -> {
            var selected = qrcodeGrid.asSingleSelect().getValue();
            if (selected != null) {
                String base64 = qrcodeService.generateQRCodeForTask(selected);
                String dataUrl = "data:image/png;base64," + base64;
                qrImage.setSrc(dataUrl);

                // Link para download (opcional)
                downloadLink.setHref(dataUrl);
                downloadLink.getElement().setAttribute("download", "task-" + selected.getId() + ".png");
                downloadLink.setText("Download QR (PNG)");

                Notification.show("QR code gerado", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Selecione uma tarefa", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL
        );

        add(new ViewToolbar("QRcode", ViewToolbar.group(qrBtn, downloadLink)));
        add(qrcodeGrid, qrImage);
    }
}
