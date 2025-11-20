package com.human.movemate.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class PageInfoDto {
    private int page;           // 현재 페이지 번호
    private int size;           // 한 페이지의 데이터 개수
    private int totalElements;  // 테이블에 저장된 데이터의 총 개수
    private int totalPages;     // 총 페이지 수
}
