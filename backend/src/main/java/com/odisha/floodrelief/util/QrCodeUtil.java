package com.odisha.floodrelief.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class QrCodeUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String generateQrCode(String content, String fileName) {
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("qrcodes");
            Files.createDirectories(dir);

            String safeName = fileName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);
            Path filePath = dir.resolve(safeName + ".png");
            MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);

            return "/uploads/qrcodes/" + safeName + ".png";
        } catch (Exception e) {
            log.error("Failed to generate QR code", e);
            return null;
        }
    }
}
