package com.human.movemate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.security.PrivilegedExceptionAction;

@Getter
@Setter
public class PostFormDto {
    private String title; // 제목
    private Long boardTypeNo; // 게시판 타입 (1 running 2 weight)
    private String content; // 내용
    private MultipartFile postImage; // <input type="file">을 받기 위한 필드
}
