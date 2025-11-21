package com.human.movemate.controller;

import com.human.movemate.dto.PostFormDto;
import com.human.movemate.dto.PagedResDto;
import com.human.movemate.model.Post;
import com.human.movemate.model.User;
import com.human.movemate.service.CommentService;
import com.human.movemate.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/running/{page}")
    public String getPageRunning(@PathVariable int page,
                                  @RequestParam(defaultValue = "10") int size, Model model) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PagedResDto<Post> pagedRes = postService.getPagination(1L, page, size);

        model.addAttribute("pagedRes", pagedRes);
        model.addAttribute("boardTypeNo", 1);

        log.info ("페이지네이션 data : {}", pagedRes.getPageInfo());

        return "post/running_post";
    }

    @GetMapping("/weight/{page}")
    public String getPageWeight(@PathVariable int page,
                                  @RequestParam(defaultValue = "10") int size, Model model) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PagedResDto<Post> pagedRes = postService.getPagination(2L, page, size);

        model.addAttribute("pagedRes", pagedRes);
        model.addAttribute("boardTypeNo", 2);

        log.info ("페이지네이션 data : {}", pagedRes.getPageInfo());

        return "post/weight_post";
    }

    @GetMapping("/my_post") // 내가 쓴 "러닝"글만 보기
    public String myPostPageRunning(Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인을 안 했으면 로그인 페이지로 리다이렉트
        if (loginUser == null) {
            return "redirect:/login";
        }
        // 로그인한 사용자의 userNo로 게시글 조회
        Long loginUserNo = loginUser.getUserNo();
        List<Post> myPostList = postService.findMyPosts(loginUserNo,1L);
        // 모델에 "postList"라는 이름으로 담기
        model.addAttribute("postList", myPostList);
        // '내가 쓴 러닝 글' 전용 HTML 페이지 반환
        return "post/my_posts_running";
    }

    @GetMapping("/my_post/weight") // 내가 쓴 "웨이트"글만 보기
    public String myPostPageWeight(Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인을 안 했으면 로그인 페이지로 리다이렉트
        if (loginUser == null) {
            return "redirect:/login";
        }
        // 로그인한 사용자의 userNo로 게시글 조회
        Long loginUserNo = loginUser.getUserNo();
        List<Post> myPostList = postService.findMyPosts(loginUserNo,2L);
        // 모델에 "postList"라는 이름으로 담기
        model.addAttribute("postList", myPostList);
        // '내가 쓴 러닝 글' 전용 HTML 페이지 반환
        return "post/my_posts_weight";
    }

    @GetMapping("/write") // 글쓰기 폼
    public String writePostForm(Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인 안 했으면(null이면) 로그인 페이지로 리다이렉트
        if (loginUser == null) {
            return "redirect:/login";
        }
        // "postForm"이라는 이름으로 빈 DTO 객체를 모델에 담아 전달
        model.addAttribute("postForm", new PostFormDto());
        return "post/write";
    }

    @PostMapping("/write") // 진짜 글 등록하는 메소드
    public String writePost(PostFormDto postFormDto, RedirectAttributes redirectAttributes, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        Long loginUserNo = user.getUserNo();
        log.info("글쓴이 : {}", loginUserNo);
        try {
            postService.save(postFormDto, loginUserNo);
            // [성공 시] 리다이렉트 페이지로 "successMessage"를 보냄
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 등록되었습니다.");
            Long boardTypeNo = postFormDto.getBoardTypeNo();
            String redirectPath;
            if (boardTypeNo != null) {
                String boardName = (boardTypeNo == 1L) ? "running" : "weight";
                // ⭐ /posts/경로를 포함한 절대 경로로 수정
                redirectPath = "redirect:/posts/" + boardName + "/1";
            } else {
                redirectPath = "redirect:/posts/running/1";
            }
            return redirectPath;
        } catch (Exception e) {
            log.error("게시글 저장 실패: {}", e.getMessage());
            // [실패 시] 리다이렉트 페이지로 "errorMessage"를 보냄
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 등록에 실패했습니다.");
            // (실패 시 목록이 아닌, 글쓰기 폼으로 다시 돌려보낼 수도 있음)
            // return "redirect:/posts/write";
        }
        // 저장이 성공하든 실패하든, 메시지를 담아서 목록 페이지로 리다이렉트
        return "redirect:/running/1"; // 추후 이(가) 완료되었습니다 공통 페이지로 변경 필요
    }

    // "어디서 왔는지"를 @RequestParam(value = "from")으로 받음
    @GetMapping("/{postId}") // 게시글 상세 조회
    public String postView(@PathVariable("postId") Long postId,
                           @RequestParam(value = "from", required = false) String from,
                           Model model) {
        // 게시글 정보 조회 (작성자 프로필 사진 포함) / Service를 호출해 1개의 게시글 정보를 가져옴
        Post post = postService.findById(postId);
        log.info("회원 이미지 링크 : {}", post.getAuthorProfileUrl());
        // 댓글 목록 조회
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.findByPostId(postId));
        // 뷰(HTML)로 'from' 값을 그대로 전달 / 이 값은 나중에 삭제 버튼이 사용
        model.addAttribute("fromPage", from);
        return "post/view_post";
    }

    @GetMapping("/edit/{postId}") // 게시글 수정을 위해 수정하기 폼으로 이동
    public String editPostForm(@PathVariable("postId") Long postId, Model model, HttpSession session) {

        Post post = postService.findById(postId);

        // 로그인한 사용자와 게시글 작성자가 같은지 확인
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getUserNo().equals(post.getUserNo())) {
            // 권한이 없다면 글 목록으로 리다이렉트
            return "redirect:/posts";
        }

        // DB에서 가져온 Post 모델을 PostFormDto로 변환
        PostFormDto postForm = new PostFormDto();
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        postForm.setBoardTypeNo(post.getBoardTypeNo());
        // 이미지는 수정 시 새로 첨부하므로 DTO에 담지 않음

        // 폼에 기존 데이터를 채우기 위해 모델에 담음
        model.addAttribute("postForm", postForm);
        // 어떤 게시글을 수정하는지 ID를 모델에 담음
        model.addAttribute("postId", postId);
        // 글쓰기 폼(write.html)을 재활용
        return "post/write";
    }

    @PostMapping("/update/{postId}") // 게시글 수정
    public String updatePost(@PathVariable("postId") Long postId,
                             PostFormDto postFormDto,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        try {
            postService.update(postId, postFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("게시글 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 수정에 실패했습니다.");
        }
        // 수정 완료 후 해당 게시글 상세 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/delete/{postId}") // 게시글 삭제
    public String deletePost(@PathVariable("postId") Long postId,
                             @RequestParam(value = "from", required = false) String from,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        try {
            postService.deleteById(postId);
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 삭제에 실패했습니다.");
        }

        //  'from' 값에 따라 올바른 경로로 리다이렉트
        return getRedirectUrl(from);
    }
    private String getRedirectUrl(String from) { // 'from' 값에 따라 리다이렉트 URL을 반환하는 헬퍼(도우미) 메소드
        if (from == null) {
            return "redirect:/posts"; // 'from' 값이 없으면 기본 경로
        }
        switch (from) {
            case "weight": // 웨이트 게시판
                return "redirect:/posts/weight";
            case "my_running": // 내가 쓴 글 - 러닝 게시판
                return "redirect:/posts/my_post";
            case "my_weight": // 내가 쓴 글 - 웨이트 게시판
                return "redirect:/posts/my_post/weight";
            case "posts": // 러닝 게시판
            default:
                return "redirect:/posts";
        }
    }
}
