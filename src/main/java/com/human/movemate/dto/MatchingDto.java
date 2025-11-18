package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MatchingDto {
    private Long matchNo;
    private Long userNo;           // 상대방 user_no
    private String name;           // 상대방 이름
    private String region;         // 지역
    private String profileImageUrl;// 프로필 사진
    private String status;         // 신청 상태
    private String postTitle;      // 관련 글 제목 (선택)

    private String postType;
}
