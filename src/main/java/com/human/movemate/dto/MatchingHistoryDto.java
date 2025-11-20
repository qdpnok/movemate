package com.human.movemate.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class MatchingHistoryDto {
    private Long matchNo;
    private String status;
    private Long applicantUserNo;
    private Long mateNo;
    private String mateType;
    private String sportType;
    private String mateName;
    private LocalDateTime createdAt;
    private String opponentName;
    private String opponentUserId;
    private String opponentProfileImageUrl;
    private String region;
}
