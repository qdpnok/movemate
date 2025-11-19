package com.human.movemate.controller;

import com.human.movemate.dto.MatchingHistoryDto;
import com.human.movemate.dto.UserProDto;
import com.human.movemate.model.User;
import com.human.movemate.service.FileStorageService;
import com.human.movemate.service.MatchingService;
import com.human.movemate.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final MatchingService matchingService;
    private final FileStorageService fileStorageService;

    // Get방식으로 루트경로/members/new 로 요청이 들어오면 members/new 페이지를 렌더링
    @GetMapping("/new")
    public String signupPage(Model model) {
        // members/new.html 을 렌더링할 때
        // memberForm 이라는 이름으로 새 User 객체를 생성하여 넘겨줌
        model.addAttribute("userForm", new UserProDto());
        return "users/new"; }

    @PostMapping("/new")
    // @ModelAttribute 어노테이션은 model.addAttribute 했던 객체를
    // 해당 클래스에서 받겠다는 의미
    // members/new.html에 가보시면 user 객체에 어떻게 값을 넣었는지 확인 가능
    public String signup(@ModelAttribute UserProDto userProDto,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        log.error("멤버 객체: {}", userProDto);
        Long no = userService.signup(userProDto);

        String newImagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            newImagePath = fileStorageService.storeFile(profileImage, "users", no);
        }

        userService.updateProfile(no, newImagePath);

        return "redirect:/";
    }

    // 내정보 페이지
    @GetMapping("/mypage")
    public String myPageForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if(user == null) return "redirect:/";
        model.addAttribute("userInfo", userService.getByNo(user.getUserNo()));
        List<MatchingHistoryDto> dto = matchingService.findHistoryByNo(user.getUserNo());

        log.info("가져온 정보: {}", dto);
        model.addAttribute("matchList", dto);
        return "users/mypage";
    }

    // 회원 정보 수정
    @GetMapping("/edit")
    public String editPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if(user == null) return "redirect:/";
        UserProDto userProDto = userService.getByNo(user.getUserNo());
        log.info("유저 정보(비번) : {}", userProDto.getPassword());
        model.addAttribute("userInfo", userProDto);
        return "users/edit";
    }

    @PostMapping("/{no}/edit")
    public String edit(@ModelAttribute UserProDto userProDto,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                       @RequestParam(name="is_image_delete_flag", defaultValue = "false") boolean isImageDeleted,
                       @PathVariable Long no) {

        log.info("멤버 id: {}, 멤버 객체: {}", no, userProDto);
        userService.update(no, userProDto, profileImage, isImageDeleted);

        return "redirect:/users/mypage";
    }

    @PostMapping("/{no}/delete")
    public String delete(@PathVariable Long no, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");

        // 로그인 체크 및 경로 변수와 세션 사용자 일치 여부 확인
        if (user == null || !user.getUserNo().equals(no)) {
            log.warn("탈퇴 실패: 권한 없는 사용자 또는 비로그인 상태");
            // 보안을 위해 권한이 없으면 메인으로 리다이렉트
            return "redirect:/";
        }

        log.info("회원 탈퇴 요청 처리: UserNo={}", no);

        // 서비스 레이어의 논리적 삭제 메서드 호출
        boolean success = userService.delete(no); // softDelete 로직을 delete에 구현했음

        if (success) {
            log.info("회원 탈퇴 (논리적 삭제) 성공. UserNo={}", no);
            //  성공 시 세션 무효화 (자동 로그아웃)
            session.invalidate();
            // 탈퇴 완료 페이지 또는 메인 페이지로 리다이렉트
            return "redirect:/";
        } else {
            log.error("회원 탈퇴 처리 중 오류 발생: UserNo={}", no);
            // 에러 페이지 또는 마이페이지로 리다이렉트 (오류 메시지 추가 가능)
            return "redirect:/users/mypage";
        }
    }
}
