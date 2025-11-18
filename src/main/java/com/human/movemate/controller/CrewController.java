package com.human.movemate.controller;

import com.human.movemate.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;

    @GetMapping("/crew")
    public String crewMain(Model model) {
        model.addAttribute("crewList", crewService.getCrewList());
        return "crew/index";
    }
}
