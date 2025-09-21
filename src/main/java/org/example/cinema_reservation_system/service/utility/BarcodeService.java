package org.example.cinema_reservation_system.service.utility;

import java.awt.image.BufferedImage;

public interface BarcodeService {
    
    /**
     * Tạo barcode từ mã đặt vé
     * @param bookingCode Mã đặt vé
     * @return BufferedImage của barcode
     */
    BufferedImage generateBarcode(String bookingCode);
    
    /**
     * Tạo QR code từ mã đặt vé
     * @param bookingCode Mã đặt vé
     * @return BufferedImage của QR code
     */
    BufferedImage generateQRCode(String bookingCode);
    
    /**
     * Tạo barcode và lưu thành file
     * @param bookingCode Mã đặt vé
     * @param filePath Đường dẫn file
     * @return true nếu thành công
     */
    boolean generateBarcodeToFile(String bookingCode, String filePath);
    
    /**
     * Tạo QR code và lưu thành file
     * @param bookingCode Mã đặt vé
     * @param filePath Đường dẫn file
     * @return true nếu thành công
     */
    boolean generateQRCodeToFile(String bookingCode, String filePath);
    
    /**
     * Tạo barcode dạng Base64 để embed vào email
     * @param bookingCode Mã đặt vé
     * @return String Base64 của barcode
     */
    String generateBarcodeBase64(String bookingCode);
    
    /**
     * Tạo QR code dạng Base64 để embed vào email
     * @param bookingCode Mã đặt vé
     * @return String Base64 của QR code
     */
    String generateQRCodeBase64(String bookingCode);
}









