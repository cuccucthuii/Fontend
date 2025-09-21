package org.example.cinema_reservation_system.service.utility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.example.cinema_reservation_system.service.utility.BarcodeService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    private static final int BARCODE_WIDTH = 300;
    private static final int BARCODE_HEIGHT = 100;
    private static final int QR_CODE_SIZE = 200;

    @Override
    public BufferedImage generateBarcode(String bookingCode) {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(bookingCode, BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT);
        
        BufferedImage image = new BufferedImage(BARCODE_WIDTH, BARCODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        
        // Set background to white
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, BARCODE_WIDTH, BARCODE_HEIGHT);
        
        // Draw barcode
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < BARCODE_WIDTH; x++) {
            for (int y = 0; y < BARCODE_HEIGHT; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        
        // Add text below barcode
        graphics.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(bookingCode);
        int textX = (BARCODE_WIDTH - textWidth) / 2;
        int textY = BARCODE_HEIGHT - 10;
        
        graphics.setColor(Color.BLACK);
        graphics.drawString(bookingCode, textX, textY);
        
        graphics.dispose();
        return image;
    }

    @Override
    public BufferedImage generateQRCode(String bookingCode) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);
            
            BitMatrix bitMatrix = qrCodeWriter.encode(bookingCode, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);
            
            BufferedImage image = new BufferedImage(QR_CODE_SIZE, QR_CODE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            
            // Set background to white
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, QR_CODE_SIZE, QR_CODE_SIZE);
            
            // Draw QR code
            graphics.setColor(Color.BLACK);
            for (int x = 0; x < QR_CODE_SIZE; x++) {
                for (int y = 0; y < QR_CODE_SIZE; y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }
            
            graphics.dispose();
            return image;
            
        } catch (WriterException e) {
            throw new RuntimeException("Lỗi tạo QR code: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean generateBarcodeToFile(String bookingCode, String filePath) {
        try {
            BufferedImage barcode = generateBarcode(bookingCode);
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
            return ImageIO.write(barcode, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu barcode: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean generateQRCodeToFile(String bookingCode, String filePath) {
        try {
            BufferedImage qrCode = generateQRCode(bookingCode);
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
            return ImageIO.write(qrCode, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu QR code: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateBarcodeBase64(String bookingCode) {
        try {
            BufferedImage barcode = generateBarcode(bookingCode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(barcode, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo barcode Base64: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateQRCodeBase64(String bookingCode) {
        try {
            BufferedImage qrCode = generateQRCode(bookingCode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCode, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo QR code Base64: " + e.getMessage(), e);
        }
    }
}
