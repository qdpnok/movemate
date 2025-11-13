package com.human.movemate.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Post {
    private Long postNo;
    private Long boardTypeNo;
    private Long userNo;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String imageUrl;
}
