package com.odisha.floodrelief.util;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class PdfUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Path getUploadRoot() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String generateDonationReceipt(String donationId, String donorName, String amount, String date) {
        try {
            Path dir = getUploadRoot().resolve("receipts");
            Files.createDirectories(dir);
            String safeId = donationId.replaceAll("[^a-zA-Z0-9\\-_]", "_");
            String fileName = "receipt_" + safeId + ".pdf";
            Path filePath = dir.resolve(fileName);

            byte[] pdfBytes = buildDonationReceiptBytes(donationId, donorName, amount, date);
            Files.write(filePath, pdfBytes);

            return "/uploads/receipts/" + fileName;
        } catch (Exception e) {
            log.error("Failed to generate PDF receipt for {}", donationId, e);
            return null;
        }
    }

    public byte[] buildDonationReceiptBytes(String donationId, String donorName, String amount, String date) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        document.add(new Paragraph("Odisha Flood Relief Foundation", titleFont));
        document.add(new Paragraph("Donation Receipt", titleFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Donation ID: " + donationId, normalFont));
        document.add(new Paragraph("Donor: " + donorName, normalFont));
        document.add(new Paragraph("Amount: Rs. " + amount, normalFont));
        document.add(new Paragraph("Date: " + date, normalFont));
        document.add(new Paragraph("Status: SUCCESS", normalFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Thank you for your generous contribution towards flood relief in Odisha!", normalFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("This is a computer-generated receipt.", normalFont));

        document.close();
        return baos.toByteArray();
    }

    public String generateMembershipCard(String membershipId, String memberName, String validUntil) {
        try {
            Path dir = getUploadRoot().resolve("membership-cards");
            Files.createDirectories(dir);
            String safeId = membershipId.replaceAll("[^a-zA-Z0-9\\-_]", "_");
            String fileName = "card_" + safeId + ".pdf";
            Path filePath = dir.resolve(fileName);

            Document document = new Document(PageSize.A6);
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph("Odisha Flood Relief Foundation", titleFont));
            document.add(new Paragraph("Membership Card", titleFont));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Member: " + memberName, normalFont));
            document.add(new Paragraph("Membership ID: " + membershipId, normalFont));
            document.add(new Paragraph("Valid Until: " + validUntil, normalFont));

            document.close();
            return "/uploads/membership-cards/" + fileName;
        } catch (Exception e) {
            log.error("Failed to generate membership card PDF", e);
            return null;
        }
    }

    public byte[] readFileBytes(String relativeUploadPath) throws Exception {
        if (relativeUploadPath == null || relativeUploadPath.trim().isEmpty()) {
            return null;
        }
        String relative = relativeUploadPath.startsWith("/uploads/")
                ? relativeUploadPath.substring("/uploads/".length())
                : relativeUploadPath.replaceFirst("^/+", "");
        Path file = getUploadRoot().resolve(relative).normalize();
        if (!file.startsWith(getUploadRoot()) || !Files.exists(file)) {
            return null;
        }
        return Files.readAllBytes(file);
    }
}
