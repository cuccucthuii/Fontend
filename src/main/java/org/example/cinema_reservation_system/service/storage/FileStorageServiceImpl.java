package org.example.cinema_reservation_system.service.storage;

import org.example.cinema_reservation_system.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;


@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file, String folder) {
        String newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path folderPath = Paths.get(uploadDir, folder);

        try {
            Files.createDirectories(folderPath); // Tạo folder nếu chưa có
            Path filePath = folderPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + file.getOriginalFilename(), e);
        }

        // Trả về đường dẫn đầy đủ có thể Ctrl + Click được
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(folder + "/")
                .path(newFileName)
                .toUriString();
    }
    
    @Override
    public String saveFileFromUrl(String imageUrl, String folder) {
        if (!isValidImageUrl(imageUrl)) {
            throw new IllegalArgumentException("URL hình ảnh không hợp lệ: " + imageUrl);
        }
        
        try {
            String extension = getFileExtension(imageUrl);
            String newFileName = UUID.randomUUID() + "_downloaded" + extension;
            Path folderPath = Paths.get(uploadDir, folder);
            
            Files.createDirectories(folderPath);
            Path filePath = folderPath.resolve(newFileName);
            
            // Download image from URL
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(folder + "/")
                    .path(newFileName)
                    .toUriString();
                    
        } catch (IOException e) {
            throw new RuntimeException("Không thể tải hình ảnh từ URL: " + imageUrl, e);
        }
    }
    
    @Override
    public boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra format URL
        Pattern urlPattern = Pattern.compile("^https?://.+\\.(jpg|jpeg|png|gif|webp)$", Pattern.CASE_INSENSITIVE);
        return urlPattern.matcher(url).matches();
    }
    
    @Override
    public String getFileExtension(String url) {
        if (url == null || !url.contains(".")) {
            return ".jpg"; // Default extension
        }
        
        String extension = url.substring(url.lastIndexOf("."));
        // Chỉ cho phép các extension hợp lệ
        if (extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
            return extension;
        }
        return ".jpg";
    }
}
