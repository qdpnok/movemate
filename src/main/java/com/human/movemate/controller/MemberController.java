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
        // members/new.html 을 렌더링할 때
        // memberForm 이라는 이름으로 새 Member 객체를 생성하여 넘겨줌
        model.addAttribute("memberForm", new Member());
        return "members/new"; }

    @PostMapping("/new")
    // @ModelAttribute 어노테이션은 model.addAttribute 했던 객체를
    // 해당 클래스에서 받겠다는 의미
    // members/new.html에 가보시면 member 객체에 어떻게 값을 넣었는지 확인 가능
    public String signup(@ModelAttribute Member member) {
        log.error("멤버 객체: {}", member);
        memberService.signup(member);
        return "redirect:/";
    }

}
