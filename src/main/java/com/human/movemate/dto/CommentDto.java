package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long commentNo;
    private Long userId; // 작성자 user_no
    private String authorId; // 작성자 user_id
    private String authorProfileUrl; // 작성자 프로필 이미지
    private String content;
    private LocalDateTime createdAt;
}
