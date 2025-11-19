package com.human.movemate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MateMember {
    private Long memberNo; // 신청회원 번호 (PK)
    private Long mateNo;   // 메이트 글 번호 (FK)
    private Long userNo;   // 신청자 회원 번호 (FK)

    // 신청자 프로필 이미지 URL
    private String applyImageUrl;

    private String applicantName; // 신청자명 (폼에서 직접 입력받는 이름)
    private String applyMessage;  // 신청 메시지
    private LocalDateTime applyDate; // 신청일
    private String status;        // 신청 상태 (PENDING, APPROVED, REJECTED)

    // 신청자 정보 JOIN해서 가져올 필드 (user_id)
    private String userId; // 신청자의 실제 ID (DB USERS 테이블에서 조인)
}