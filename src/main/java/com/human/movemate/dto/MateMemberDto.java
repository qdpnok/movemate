package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MateMemberDto {
    private Long memberNo;        // 멤버 PK
    private Long userNo;          // 유저번호
    private String userName;      // 이름
    private String userRegion;    // 지역
    private String userProfileUrl; // 프로필
    private LocalDateTime joinedAt; // 가입일
}
