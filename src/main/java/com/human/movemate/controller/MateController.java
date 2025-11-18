package com.human.movemate.controller;

import com.human.movemate.service.MateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MateController {

    private final MateService mateService;

    @GetMapping("/mate")
    public String mateMain(Model model) {

        model.addAttribute("mateList", mateService.getMateList());
        model.addAttribute("crewList", mateService.getCrewList());

        return "mate/mate";
    }
}
