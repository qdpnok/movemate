package com.human.movemate.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddMate {
    private Long mateNo;
    private Long userNo; // 작성자(FK)
    private String mateType; // "1:1" or "GROUP"
    private String region; // 지역선택
    private String sportType; // 종목선택
    private String mateName; // 메이트명(글 제목이 될 부분)
    private String description; // 모집설명(글 내용이 될 부분)
    private String imageUrl; // 사진 등록 (프사)
    private LocalDateTime createdAt;

}
