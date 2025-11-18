package com.human.movemate.controller;

import com.human.movemate.model.AddMate;
import com.human.movemate.service.MateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MateController {

    private final MateService mateService; // 매니저(Service)를 부름

    // 손님이 "http://.../mates" 주소를 요청(GET)하면 이 메서드가 실행됨
    @GetMapping("/mate")
    public String showMateList(Model model) {

        // 1. 매니저에게 모든 메이트 목록을 가져오라고 시킴
        List<AddMate> allMates = mateService.findAllMates();

        // 2. 받은 목록을 '1:1 메이트'와 '그룹(크루) 메이트'로 분리
        List<AddMate> soloMates = allMates.stream()
                .filter(mate -> "SOLO".equals(mate.getMateType())) // AddMateController에서 "SOLO"로 저장함
                .collect(Collectors.toList());

        List<AddMate> crewMates = allMates.stream()
                .filter(mate -> "CREW".equals(mate.getMateType())) // AddMateController에서 "CREW"로 저장함
                .collect(Collectors.toList());

        // 3. '쟁반(Model)'에 담아서 HTML 파일에게 전달
        model.addAttribute("soloMates", soloMates);
        model.addAttribute("crewMates", crewMates);

        // 4. "templates/mate/mate.html" 파일을 화면에 보여줌
        return "mate/mate";
    }
}