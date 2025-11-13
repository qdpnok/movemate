package com.human.movemate.dto;

import lombok.Getter;
import lombok.Setter;

import java.security.PrivilegedExceptionAction;

@Getter
@Setter
public class PostFormDto {
    private String title; // 제목
    private Long boardTypeNo; // 게시판 타입 (1 running 2 weight)
    private String content; // 내용
}
