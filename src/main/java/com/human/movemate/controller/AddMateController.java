package com.human.movemate.controller;

import com.human.movemate.dto.AddMateFormDto;
import com.human.movemate.service.AddMateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.human.movemate.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// '메이트 모집' (1:1, 그룹) 관련 컨트롤러
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/addMate")
public class AddMateController {
    private final AddMateService addMateService;

    // 1:1 메이트 생성 폼 페이지
    // GET/addMate/solo
    @GetMapping("/solo")
    public String createSoloMateForm(Model model, HttpSession session) {
        // 로그인 했는지 체크
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 로그인 안했으면 리다이렉트
        }
        // DTO 객체 생성
        AddMateFormDto mateForm = new AddMateFormDto();
        mateForm.setMateType("SOLO");
        model.addAttribute("pageTitle", "1:1 메이트 생성");
        // 폼 데이터를 담을 DTO 전달
        model.addAttribute("mateForm", mateForm);
        // 1:1이랑 crew랑 둘이 거의 비슷해서 같은 html 쓰겠음
        return "addmate/add_mate_form"; // <- 이 html이 화면에 출력
    }

    // 그룹 메이트(크루) 생성 폼 페이지 (addMateCrew)
    // GET/addMate/crew
    @GetMapping("/crew")
    public String createGroupMateForm(Model model, HttpSession session) {
        // 로그인 했는지 체크
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 로그인 안했으면 리다이렉트
        }
        AddMateFormDto mateForm = new AddMateFormDto();
        mateForm.setMateType("CREW");
        model.addAttribute("pageTitle", "크루 생성");
        model.addAttribute("mateForm", mateForm);
        // 1:1이랑 crew랑 둘이 거의 비슷해서 같은 html 쓰겠음
        return "addmate/add_mate_form"; // <- 이 html이 화면에 출력
    }

    // 메이트 생성 (1:1/그룹) 글 작성 (저장)
    // POST/addMate/save
    @PostMapping("/save")
    public String saveMate(
            AddMateFormDto mateFormDto, // 폼 데이터가 DTO로 자동 매핑됨
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 로그인 안했으면 로그인 해라
        }
        try {
            // Service 호출 (DTO와 로그인한 userNo 전달)
            addMateService.saveMate(mateFormDto, loginUser.getUserNo());
            // 성공 시 메시지 전달
            redirectAttributes.addFlashAttribute("successMessage", "메이트 모집 글이 등록되었습니다.");
        } catch (Exception e) {
            log.error("메이트 저장 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "등록에 실패했습니다: " + e.getMessage());
        }

        // 추후 메이트 목록 페이지로 바꿔야됨 ★
        return "redirect:/";
    }
}
