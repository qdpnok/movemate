package com.human.movemate.controller;

import com.human.movemate.dto.AddMateFormDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.service.AddMateService;
import com.human.movemate.service.MateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.human.movemate.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

// '메이트 모집' (1:1, 그룹) 관련 컨트롤러
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/addMate")
public class AddMateController {
    private final AddMateService addMateService;
    private final MateService mateService;

    // 손님이 "http://.../mates" 주소를 요청(GET)하면 이 메서드가 실행됨
    @GetMapping
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

    // 메이트 모집 글 상세 조회
    // GET/addMate/{mateNo}
    @GetMapping("/{mateNo}")
    public String viewMate(@PathVariable("mateNo") Long mateNo, Model model) {
        AddMate mate = addMateService.findById(mateNo);
        if (mate == null) {
            // (임시) 글이 없으면 메인으로
            return "redirect:/";
        }
        model.addAttribute("mate", mate);
        // "SOLO" / "CREW" 타입에 따라 다른 제목 전달
        if ("SOLO".equals(mate.getMateType())) {
            model.addAttribute("pageTitle", "내가 쓴 1:1 메이트 모집 글");
        } else {
            model.addAttribute("pageTitle", "내가 만든 크루 관리");
        }
        // 화면에 뿌려줄 HTML
        return "addmate/add_mate_view";
    }

    // 글 수정 폼
    // POST/addMate/update/{mateNo}
    @GetMapping("/edit/{mateNo}")
    public String editMateForm(@PathVariable("mateNo") Long mateNo, Model model, HttpSession session) {
        AddMate mate = addMateService.findById(mateNo);
        User loginUser = (User) session.getAttribute("loginUser");
        // 권한 확인 (본인 글이 아니면)
        if (loginUser == null || !loginUser.getUserNo().equals(mate.getUserNo())) {
            return "redirect:/"; // (임시) 메인으로
        }
        // Model -> DTO 변환 (폼에 채우기 위함)
        AddMateFormDto mateForm = new AddMateFormDto();
        mateForm.setMateType(mate.getMateType());
        mateForm.setRegion(mate.getRegion());
        mateForm.setSportType(mate.getSportType());
        mateForm.setMateName(mate.getMateName());
        mateForm.setDescription(mate.getDescription());
        model.addAttribute("mateForm", mateForm);
        model.addAttribute("mateNo", mateNo);
        if ("SOLO".equals(mate.getMateType())) {
            model.addAttribute("pageTitle", "1:1 메이트 생성 내용 수정");
        } else {
            model.addAttribute("pageTitle", "메이트 크루 생성 내용 수정");
        }
        return "addmate/add_mate_form";
    }

    // 실제로 수정하는 메소드
    @PostMapping("/update/{mateNo}")
    public String updateMate(@PathVariable("mateNo") Long mateNo,
                             AddMateFormDto mateFormDto,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        // 로그인 확인
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        try {
            // 2Service 호출 (업데이트 로직 실행 / 권한 확인은 Service 내부에서 수행)
            addMateService.updateMate(mateNo, mateFormDto, loginUser.getUserNo());
            // 성공 메시지
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            // 실패 메시지
            log.error("메이트 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패했습니다: " + e.getMessage());
        }
        // 수정 완료 후, 해당 글 상세 페이지로 리다이렉트
        return "redirect:/addMate/" + mateNo;
    }

    // 글 삭제
    // GET/addMate/delete/{mateNo}
    @GetMapping("/delete/{mateNo}")
    public String deleteMate(@PathVariable("mateNo") Long mateNo,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        try {
            // Service에서 권한 확인 및 삭제
            addMateService.deleteMate(mateNo, loginUser.getUserNo());
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("메이트 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "삭제에 실패했습니다: " + e.getMessage());
        }
        // (임시) 삭제 후 메인으로 (목록 페이지 완성 시 그곳으로)
        return "redirect:/";
    }

//    1:1 메이트 신청 민아
    // AddMateController 안에 아래 메서드를 추가하세요

    // 메이트 신청하기 화면 이동
    // 주소 예시: /addMate/apply/1 (1번 글에 신청하기)
    @GetMapping("/apply/{mateNo}")
    public String applyPage(@PathVariable Long mateNo, Model model) {
        // 1. DB에서 해당 메이트 글 정보를 가져옴
        AddMate mate = addMateService.getMateDetail(mateNo);

        // 2. 화면에 전달
        model.addAttribute("mate", mate);

        // 3. addmate 폴더 안의 html로 이동
        return "addmate/add_mate_apply";
    }
}
