package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class MatchingDetailDto {
    private Long applicantNo;         // 신청자 고유 번호 (수락/거절 처리 시 필요)
    private Long matchNo;
    private Long mateNo;
    private Long mateWriterNo;      // 모집글 작성자 번호 (로직 분기용)
    private String matchStatus;     // 매칭 상태 (액션 처리용)

    // 상세 정보
    private String mateType;          // 메이트 신청 종류 (예: "1:1러닝메이트 신청")
    private String mateName;

    // 신청자 정보
    private String profileImageUrl;
    private String applicantName;     // 신청자 이름
    private String applicantRegion;   // 신청자 지역

}
