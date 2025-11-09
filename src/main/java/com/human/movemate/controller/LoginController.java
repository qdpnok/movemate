package com.human.movemate.controller;

import com.human.movemate.model.Member;
import com.human.movemate.service.impl.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// @ 붙은 애들을 어노테이션이라고 부름

// Controller : 스프링에 Controller(Service 메서드를 활용해 화면을 그리는 역할)로 등록
// RequiredArgsConstructor : 의존성 주입을 축약해줌
// Slf4j : 오류 로그 출력 기능을 제공
@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final MemberServiceImpl memberService;

    // 루트경로(http://localhost:8282)로 get 방식을 사용해서 이동하겠다

    // get 방식 vs post 방식
    // get은 경로에 정보를 바로 담는 반면, post는 인코딩(암호화)된 정보를 body에 담는다.
    // 보안이 필요하거나 많은 정보들을 담아야 한다면 post 방식을, 아니라면 get방식을 사용함.

    // 조금 더 부정확하지만 일반적인 이야기를 하자면
    // 보안이 필요 없는 정보만 담거나 화면만 뿌려준다면 get 방식을
    // 보안이 필요한 정보들을 담거나 정보를 생성/수정 한다면 post 방식을 자주 씁니다.
    @GetMapping("/")
    public String mainPage() { return "login/login"; }  // resources/templates/login/login.html을 가리킴

    // 관찰해보니까 return "경로" 로 작성하는 건 resources/templates 밑의 html을 가리키고,
    // return "redirect:/경로" 로 작성하는 건 http://localhost:8282/경로 를 가리키는 것 같슴다

    @PostMapping("/login")
    public String login(@ModelAttribute Member member, HttpSession session) {
        Member memberRes = memberService.login(member);
        log.info("로그인 : {}", memberRes);
        if(memberRes == null) {
            return "redirect:/";
        }
        session.setAttribute("loginMember", member);
        return "main/main";
    }

}
