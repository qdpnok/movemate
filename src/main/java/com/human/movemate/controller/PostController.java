package com.human.movemate.controller;

import com.human.movemate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
