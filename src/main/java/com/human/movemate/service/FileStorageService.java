package com.human.movemate.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String domain, Long id);

    void deleteIfExists(String relativePath);
}
