package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ApplyResDto {
    private Long applicationNo;
    private Long mateNo;
    private Long applicantUserNo;
    private LocalDateTime appliedAt;
    private String status;
    private String applicationMessage;
    private String mateName;
    private String imageUrl;
    private String sportType;
    private String mateType;
    private String region;
}
