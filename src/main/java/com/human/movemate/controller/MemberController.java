package com.human.movemate.controller;

import com.human.movemate.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    // Get방식으로 루트경로/member/new 로 요청이 들어오면 members/signup 페이지를 렌더링
    @GetMapping("/new")
    public String signup() { return "members/new"; }

}
