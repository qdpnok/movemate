package com.human.movemate.service.impl;

import com.human.movemate.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveImage(MultipartFile file, String subDir) {
        return "";
    }

    @Override
    public void deleteIfExists(String relativePath) {

    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 1. 업로드 경로(Path 객체) 생성
            Path uploadPath = Paths.get(uploadDir);

            // 2. 폴더가 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. 파일명 중복을 피하기 위해 UUID + 원본 파일명으로 새 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // 4. 최종 저장 경로
            Path filePath = uploadPath.resolve(storedFileName);

            // 5. 파일 저장 (핵심)
            Files.copy(file.getInputStream(), filePath);

            // 6. DB에 저장할 파일명(UUID 포함) 반환
            return storedFileName;

        } catch (IOException e) {
            // (실제로는 커스텀 예외를 던지는 것이 좋음)
            throw new RuntimeException("파일을 저장하지 못했습니다: " + e.getMessage());
        }
    }
}
