package com.human.movemate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile; // 파일 업로드를 위해 추가

// 메이트 신청 폼에서 입력받는 데이터를 담는 DTO
@Getter
@Setter
public class MateApplyFormDto {
    private Long mateNo; // 신청할 메이트 글 번호
    private String applicantName; // 신청자명
    private String applyMessage; // 신청 메시지
    private MultipartFile applyImage; // 신청자 프로필 사진 파일
}
