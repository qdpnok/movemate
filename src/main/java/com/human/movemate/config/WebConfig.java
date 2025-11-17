package com.human.movemate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // post, userprofile 사진 업로드 충돌되는 부분 수정

    // application.properties의 'file.upload-dir' 값을 주입받기
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // HTML이 요청할 URL 패턴을 "/images/**"로 변경
        registry.addResourceHandler("/images/**")
        // 실제 파일이 저장된 로컬 디스크 경로를 'file:///' 프로토콜로 매핑 (Windows는 ///)
        .addResourceLocations("file:///" + uploadDir);
    }
}
