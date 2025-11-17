package com.human.movemate.controller;
import com.human.movemate.model.User;
import jakarta.servlet.http.HttpSession;
import com.human.movemate.dto.CommentDto;
import com.human.movemate.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록(저장)
    @PostMapping("/comments/save")
    public String saveComment(
            @RequestParam("postId") Long postId,    // 어느 게시글에
            @RequestParam("content") String content,  // 어떤 내용을
            HttpSession session, // 세션에서 로그인 정보 가져오기 위함
            RedirectAttributes redirectAttributes // 메시지 전달 위함
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 댓글을 작성할 수 있습니다.");
            return "redirect:/posts/" + postId; // 로그인 페이지로 리다이렉트하거나 오류 처리
        }
        Long loginUserNo = loginUser.getUserNo(); // 실제 로그인 유저 번호 사용

        try {
            commentService.save(postId, loginUserNo, content);
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("댓글 저장 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 등록에 실패했습니다.");
        }
        return "redirect:/posts/" + postId;
    }

    // 댓글 삭제
    @GetMapping("/comments/delete/{commentId}")
    public String deleteComment(
            @PathVariable("commentId") Long commentId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 댓글을 삭제할 수 있습니다.");
            // postId를 알 수 없으므로 일단 /posts로 리다이렉트
            return "redirect:/posts";
        }
        try {
            // 삭제하려는 댓글의 userNo와 로그인한 유저의 userNo가 일치하는지 확인
            CommentDto comment = commentService.findById(commentId);
            if (comment != null && !comment.getUserNo().equals(loginUser.getUserNo())) {
                redirectAttributes.addFlashAttribute("errorMessage", "자신이 작성한 댓글만 삭제할 수 있습니다.");
                return "redirect:/posts/" + comment.getPostId(); // 해당 게시글로 돌아감
            }
            // Service를 호출 (삭제 후 postId를 반환)
            Long postId = commentService.deleteById(commentId);
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 삭제되었습니다.");
            // 삭제 후, 해당 게시글 페이지로 리다이렉트
            return "redirect:/posts/" + postId;
        } catch (Exception e) {
            log.error("댓글 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 삭제에 실패했습니다.");
            // (실패 시 에러 메시지 / posts로 이동)
            return "redirect:/posts";
        }
    }

    // 댓글 수정
    @PostMapping("/comments/update")
    public String updateComment(
            @RequestParam("commentNo") Long commentNo,
            @RequestParam("postId") Long postId, // 리다이렉트용
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 댓글을 수정할 수 있습니다.");
            return "redirect:/posts/" + postId;
        }
        try {
            // (권한 확인)
            CommentDto comment = commentService.findById(commentNo);
            if (comment != null && !comment.getUserNo().equals(loginUser.getUserNo())) {
                redirectAttributes.addFlashAttribute("errorMessage", "자신이 작성한 댓글만 수정할 수 있습니다.");
                return "redirect:/posts/" + postId;
            }
            commentService.update(commentNo, content);
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 수정에 실패했습니다.");
        }
        // 수정 완료 후, 원래 게시글로 리다이렉트
        return "redirect:/posts/" + postId;
    }
}
