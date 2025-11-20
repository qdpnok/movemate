package com.human.movemate.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class MatchingHistoryDto {
    private String status;
    private Long mateNo;
    private String mateType;
    private String sportType;
    private String mateName;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String region;

    public long getDurationDays() {
        if (createdAt == null) {
            return 0;
        }
        // LocalDateTime을 사용하여 등록일과 현재 날짜 사이의 일수 차이를 계산
        // 정확한 일수 차이를 위해 .toLocalDate()를 사용하여 시간 정보를 제외합니다.
        return ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDateTime.now().toLocalDate());
    }
}
