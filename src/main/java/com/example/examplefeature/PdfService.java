package com.example.examplefeature;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;

@Service
public class PdfService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    public byte[] generateTaskListPdf(List<Task> tasks) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add title
            Paragraph title = new Paragraph("Task List Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Add generation date
            Paragraph date = new Paragraph("Generated on: " + 
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(
                    java.time.LocalDateTime.now()), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            if (tasks.isEmpty()) {
                Paragraph noTasks = new Paragraph("No tasks found.", NORMAL_FONT);
                noTasks.setAlignment(Element.ALIGN_CENTER);
                document.add(noTasks);
            } else {
                // Create table
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{4, 2, 2});
                
                // Add headers
                addTableHeader(table, "Description");
                addTableHeader(table, "Due Date");
                addTableHeader(table, "Creation Date");
                
                // Add task data
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                
                for (Task task : tasks) {
                    addTableCell(table, task.getDescription());
                    addTableCell(table, Optional.ofNullable(task.getDueDate())
                        .map(dateFormatter::format).orElse("No due date"));
                    addTableCell(table, dateTimeFormatter.format(
                        task.getCreationDate().atZone(java.time.ZoneId.systemDefault())));
                }
                
                document.add(table);
                
                // Add summary
                Paragraph summary = new Paragraph("\nTotal tasks: " + tasks.size(), HEADER_FONT);
                summary.setSpacingBefore(20);
                document.add(summary);
            }
            
        } finally {
            document.close();
        }
        
        return baos.toByteArray();
    }
    
    private void addTableHeader(PdfPTable table, String headerText) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerText, HEADER_FONT));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(8);
        table.addCell(header);
    }
    
    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(text, NORMAL_FONT));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
