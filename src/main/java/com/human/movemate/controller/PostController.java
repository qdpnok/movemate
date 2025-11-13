package com.human.movemate.controller;

import com.human.movemate.dto.PostFormDto;
import com.human.movemate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping // 러닝 게시판(디폴트 값으로 가져감)
    public String postPageRunning(Model model) {
        model.addAttribute("postList", postService.find(1L));
        return "post/running_post";
    }

    @GetMapping("/weight") // 웨이트 게시판
    public String postPageWeight(Model model) {
        model.addAttribute("postList", postService.find(2L));
        return "post/weight_post";
    }

    @GetMapping("/write") // 글쓰기 폼
    public String writePostForm(Model model) {
        // "postForm"이라는 이름으로 빈 DTO 객체를 모델에 담아 전달
        model.addAttribute("postForm", new PostFormDto());
        return "post/write";
    }

    @PostMapping("/write") // 진짜 글 등록하는 메소드
    public String writePost(PostFormDto postFormDto) {
        Long loginUserNo = 1L;
        log.info("게시판 제목: {}", postFormDto.getTitle());
        log.info("게시판 내용: {}", postFormDto.getContent());
        log.info("게시판 타입: {}", postFormDto.getBoardTypeNo());
        return "redirect:/posts"; // 추후 이(가) 완료되었습니다 공통 페이지로 변경 필요
    }
}
