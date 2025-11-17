package com.human.movemate.controller;

import com.human.movemate.dto.CommentDto;
import com.human.movemate.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록(저장)
    @PostMapping("/comments/save")
    public String saveComment(
            @RequestParam("postId") Long postId,    // 어느 게시글에
            @RequestParam("content") String content  // 어떤 내용을
    ) {
        // 로그인한 사용자 ID. (PostController와 동일하게 1L 사용)
        Long loginUserNo = 1L;

        try {
            commentService.save(postId, loginUserNo, content);
        } catch (Exception e) {
            log.error("댓글 저장 실패: {}", e.getMessage());
            // (나중에 실패 시 에러 메시지 처리 추가)
        }
        // 저장이 완료되면, 방금 댓글 단 그 게시글 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }

    // 댓글 삭제
    @GetMapping("/comments/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId) {
        try {
            // (권한 확인 로직 추가 필요)

            // Service를 호출 (삭제 후 postId를 반환)
            Long postId = commentService.deleteById(commentId);

            // 삭제 후, 해당 게시글 페이지로 리다이렉트
            return "redirect:/posts/" + postId;

        } catch (Exception e) {
            log.error("댓글 삭제 실패: {}", e.getMessage());
            // (실패 시 에러 페이지 또는 /posts로 이동)
            return "redirect:/posts";
        }
    }
    // 댓글 수정을 위한 댓글 1개 조회 (수정 폼) / comment_no로 찾음
    @GetMapping("/comments/edit/{commentId}")
    public String editCommentForm(@PathVariable("commentId") Long commentId, Model model) {
        CommentDto comment = commentService.findById(commentId);
        // (권한 확인 로직 필요 - 내가 나인지 ? 등...?ㅋ)
        // 모델에 담아서 폼으로 전달
        model.addAttribute("comment", comment);
        // 수정 폼 페이지 반환
        return "comment/edit_form";
    }
    // 댓글 수정
    @PostMapping("/comments/update")
    public String updateComment(
            @RequestParam("commentNo") Long commentNo,
            @RequestParam("postId") Long postId, // 리다이렉트용
            @RequestParam("content") String content
    ) {
        try {
            // (권한 확인 로직 필요 - 내가 나인지 ? 등...?ㅋ)
            commentService.update(commentNo, content);
        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage());
        }
        // 수정 완료 후, 원래 게시글로 리다이렉트
        return "redirect:/posts/" + postId;
    }
}
