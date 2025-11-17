package com.human.movemate.controller;

import com.human.movemate.dto.PostFormDto;
import com.human.movemate.model.Post;
import com.human.movemate.service.CommentService;
import com.human.movemate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

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
    public String writePost(PostFormDto postFormDto, RedirectAttributes redirectAttributes) {
        Long loginUserNo = 1L;
        try {
            postService.save(postFormDto, loginUserNo);
            // 4. [성공 시] 리다이렉트 페이지로 "successMessage"를 보냄
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("게시글 저장 실패: {}", e.getMessage());
            // 5. [실패 시] 리다이렉트 페이지로 "errorMessage"를 보냄
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 등록에 실패했습니다.");
            // (실패 시 목록이 아닌, 글쓰기 폼으로 다시 돌려보낼 수도 있음)
            // return "redirect:/posts/write";
        }

        // 6. 저장이 성공하든 실패하든, 메시지를 담아서 목록 페이지로 리다이렉트
        return "redirect:/posts"; // 추후 이(가) 완료되었습니다 공통 페이지로 변경 필요
    }

    @GetMapping("/{postId}") // 게시글 상세 조회
    public String postView(@PathVariable("postId") Long postId, Model model) {
        // 게시글 정보 조회 (작성자 프로필 사진 포함) / Service를 호출해 1개의 게시글 정보를 가져옴
        Post post = postService.findById(postId);
        // 댓글 목록 조회
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.findByPostId(postId));
        // templates/post/view_post.html 페이지로 이동
        return "post/view_post";
    }
}
