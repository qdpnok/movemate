package com.human.movemate.controller;

import com.human.movemate.service.MateService;
import com.human.movemate.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final MateService mateService;

    // 루트경로(http://localhost:8282)로 get 방식을 사용해서 이동하겠다
    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("top3Crew", mateService.findTop3Crew());

        return "main/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
