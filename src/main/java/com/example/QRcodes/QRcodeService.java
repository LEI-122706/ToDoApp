package com.example.QRcodes;


import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class QRcodeService {

    private final QRcodeRepository qrcodeRepository;

    QRcodeService(QRcodeRepository qrcodeRepository) {
        this.qrcodeRepository = qrcodeRepository;
    }

    @Transactional
    public void createQRcode(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var QRcode = new QRcode(description, Instant.now());
        QRcode.setDueDate(dueDate);
        qrcodeRepository.saveAndFlush(QRcode);
    }

    @Transactional(readOnly = true)
    public List<QRcode> list(Pageable pageable) {
        return qrcodeRepository.findAllBy(pageable).getContent();
    }

    public String generateQRCodeForTask(QRcode task) {
        try {
            String content = "Task: " + task.getDescription() + "\nDue: " + task.getDueDate();
            return QRcodeGenerator.generateBase64QRCode(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
