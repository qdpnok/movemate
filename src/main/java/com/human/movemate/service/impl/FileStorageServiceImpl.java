package com.human.movemate.service.impl;

import com.human.movemate.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    // application.properties에서 'file.upload-dir' 값을 주입받음
    @Value("${file.upload-dir}")
    private String uploadDir;

    // PostController가 호출하는 메서드
    @Override
    public String storeFile(MultipartFile file, String domain, Long id) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            // 업로드 경로(Path 객체) 생성
            // ${user.home}/movemate-uploads/
            Path uploadPath = Paths.get(uploadDir, domain);

            // 폴더가 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("업로드 폴더 생성: {}", uploadPath);
            }

            // 파일명 중복 방지를 위한 UUID, id가 있으면 id 값으로 저장
            // (파일명 생성)
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.lastIndexOf('.') > -1) {
                ext = original.substring(original.lastIndexOf('.'));
            }

            String storedFileName;
            if (id == null) {
                storedFileName = UUID.randomUUID().toString() + "_";
            } else {
                storedFileName = String.valueOf(id);
            }
            storedFileName += ext;

            // 최종 저장 경로
            // (파일 저장)
            Path filePath = uploadPath.resolve(storedFileName);
            // 디버깅 로그
            log.info("파일 저장 시도: {}", filePath);
            // 파일 저장 (Files.copy)
            Files.copy(file.getInputStream(), filePath);

            // DB에 저장할 파일명 반환
            return domain + "/" + storedFileName;

        } catch (IOException e) {
            // Controller가 예외를 잡아서 로그로 보여줄 것
            throw new RuntimeException("파일을 저장하지 못했습니다: " + e.getMessage());
        }
    }

    // 기존 파일 삭제 (교체 시 사용)
    @Override
    public void deleteIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path p = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(p);
            log.trace("파일을 삭제했습니다.");
        } catch (IOException ignore) {}
    }

    }

