package com.human.movemate.controller;

import com.human.movemate.dto.UserPro;
import com.human.movemate.model.User;
import com.human.movemate.service.FileStorageService;
import com.human.movemate.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;

    // Get방식으로 루트경로/members/new 로 요청이 들어오면 members/new 페이지를 렌더링
    @GetMapping("/new")
    public String signupPage(Model model) {
        // members/new.html 을 렌더링할 때
        // memberForm 이라는 이름으로 새 User 객체를 생성하여 넘겨줌
        model.addAttribute("userForm", new UserPro());
        return "users/new"; }

    @PostMapping("/new")
    // @ModelAttribute 어노테이션은 model.addAttribute 했던 객체를
    // 해당 클래스에서 받겠다는 의미
    // members/new.html에 가보시면 user 객체에 어떻게 값을 넣었는지 확인 가능
    public String signup(@ModelAttribute UserPro userPro,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        log.error("멤버 객체: {}", userPro);
        Long no = userService.signup(userPro);

        if (profileImage != null && !profileImage.isEmpty()) {
            fileStorageService.saveImage(profileImage, "users", no);
        }

        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String myPageForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if(user == null) return "redirect:/";
        model.addAttribute("userInfo", userService.getByNo(user.getUserNo()));
        return "users/mypage";
    }

}
