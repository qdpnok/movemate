package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ApplyReqDto {
    private Long mateNo;
    private Long applicantUserNo;
    private String applicationMessage;
}
