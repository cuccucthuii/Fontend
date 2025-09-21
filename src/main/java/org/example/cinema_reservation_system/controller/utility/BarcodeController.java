package org.example.cinema_reservation_system.controller.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.service.utility.BarcodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BarcodeController {

    private final BarcodeService barcodeService;

    /**
     * Tạo barcode và trả về dạng hình ảnh
     */
    @GetMapping(value = "/generate/{bookingCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateBarcode(@PathVariable String bookingCode) {
        try {
            BufferedImage barcode = barcodeService.generateBarcode(bookingCode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(barcode, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Tạo QR code và trả về dạng hình ảnh
     */
    @GetMapping(value = "/qr/{bookingCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@PathVariable String bookingCode) {
        try {
            BufferedImage qrCode = barcodeService.generateQRCode(bookingCode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCode, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Tạo barcode dạng Base64
     */
    @GetMapping("/base64/{bookingCode}")
    public ResponseEntity<String> generateBarcodeBase64(@PathVariable String bookingCode) {
        String base64 = barcodeService.generateBarcodeBase64(bookingCode);
        return ResponseEntity.ok(base64);
    }

    /**
     * Tạo QR code dạng Base64
     */
    @GetMapping("/qr/base64/{bookingCode}")
    public ResponseEntity<String> generateQRCodeBase64(@PathVariable String bookingCode) {
        String base64 = barcodeService.generateQRCodeBase64(bookingCode);
        return ResponseEntity.ok(base64);
    }

    /**
     * Test tạo barcode với mã mẫu
     */
    @GetMapping("/test")
    public ResponseEntity<String> testBarcode() {
        String testCode = "6337652498106002";
        String barcodeBase64 = barcodeService.generateBarcodeBase64(testCode);
        String qrCodeBase64 = barcodeService.generateQRCodeBase64(testCode);
        
        return ResponseEntity.ok(String.format("""
            <html>
            <head><title>Test Barcode</title></head>
            <body>
                <h2>Test Barcode Generation</h2>
                <p>Booking Code: %s</p>
                
                <h3>Barcode:</h3>
                <img src="data:image/png;base64,%s" alt="Barcode" style="border: 1px solid #ccc;" />
                
                <h3>QR Code:</h3>
                <img src="data:image/png;base64,%s" alt="QR Code" style="border: 1px solid #ccc;" />
                
                <p>Barcode có thể được quét bởi máy quét barcode hoặc ứng dụng điện thoại.</p>
                <p>QR Code có thể được quét bởi camera điện thoại.</p>
            </body>
            </html>
            """, testCode, barcodeBase64, qrCodeBase64));
    }
}









