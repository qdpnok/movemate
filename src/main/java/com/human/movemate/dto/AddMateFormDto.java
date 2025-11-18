package com.human.movemate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

//메이트 생성 폼의 데이터를 컨트롤러로 전달하기 위한 DTO

@Getter
@Setter
public class AddMateFormDto {
    private String mateType; // 1:1 / CREW 구분
    private String region; // 지역선택
    private String sportType; // 종목선택
    private String mateName; // 메이트명
    private MultipartFile mateImage; // 사진등록 (파일 업로드)
    private String description; // 모집설명

}

