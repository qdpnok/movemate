package com.human.movemate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    // 루트경로(http://localhost:8282)로 get 방식을 사용해서 이동하겠다
    @GetMapping("/")
    public String mainPage() {
        return "main/main";
    }
}
