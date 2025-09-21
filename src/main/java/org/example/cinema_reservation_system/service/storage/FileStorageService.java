package org.example.cinema_reservation_system.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {

    String saveFile(MultipartFile file, String subDir);
    
    String saveFileFromUrl(String imageUrl, String subDir);
    
    boolean isValidImageUrl(String url);
    
    String getFileExtension(String url);
}
