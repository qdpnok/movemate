package com.human.movemate.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveImage(MultipartFile file, String subDir, Long userNo);

    void deleteIfExists(String relativePath);
}
