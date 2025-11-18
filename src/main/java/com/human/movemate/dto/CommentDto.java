package com.human.movemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
    private Long commentNo;
    private Long postId; // 댓글 리다이렉트 및 댓글 수정 폼에 필요함
    private Long userNo; // 작성자 user_no
    private String authorId; // 작성자 user_id
    private String authorProfileUrl; // 작성자 프로필 이미지
    private String content;
    private LocalDateTime createdAt;
}
