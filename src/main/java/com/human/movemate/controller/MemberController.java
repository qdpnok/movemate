package com.human.movemate.controller;

import com.human.movemate.model.Member;
import com.human.movemate.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    // Get방식으로 루트경로/members/new 로 요청이 들어오면 members/new 페이지를 렌더링
    @GetMapping("/new")
    public String signupPage(Model model) {
        model.addAttribute("memberForm", new Member());
        return "members/new"; }

    @PostMapping("/new")
    public String signup(@ModelAttribute Member member) {
        log.error("멤버 객체: {}", member);
        memberService.signup(member);
        return "redirect:/";
    }

}
