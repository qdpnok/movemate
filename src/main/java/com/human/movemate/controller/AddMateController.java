package com.human.movemate.controller;

import com.human.movemate.dto.AddMateFormDto;
import com.human.movemate.dto.MateMemberDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.model.User;
import com.human.movemate.service.AddMateService;
import com.human.movemate.service.MateMemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

// '메이트 모집' (1:1, 그룹) 관련 컨트롤러
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/addMate")
public class AddMateController {
    private final AddMateService addMateService;
    private final MateMemberService mateMemberService;

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
    public String viewMate(@PathVariable("mateNo") Long mateNo, Model model, HttpSession session) {
        AddMate mate = addMateService.findById(mateNo);
        if (mate == null) {
            // (임시) 글이 없으면 메인으로
            return "redirect:/";
        }
        model.addAttribute("mate", mate);
        // "SOLO" / "CREW" 타입에 따라 다른 제목 전달
        if ("SOLO".equals(mate.getMateType())) {
            model.addAttribute("pageTitle", "내가 쓴 1:1 메이트 모집 글");
            // 화면에 뿌려줄 HTML
            return "addmate/view_mate_solo";
        } else {
            return "redirect:/addMate/my/crew";
        }
    }

    // 내가 만든 크루 상세 조회
    // GET/addMate/my/crew
    @GetMapping("/my/crew") // 링크 이거 아닌듯
    public String myCrewList(Model model, HttpSession session,
                             @RequestParam(value="sport", defaultValue="러닝") String sportType) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        List<AddMate> myCrews = addMateService.findMyCrews(loginUser.getUserNo(), sportType);

        model.addAttribute("mateList", myCrews);
        model.addAttribute("activeTab", sportType); // "러닝" or "웨이트"
        model.addAttribute("pageTitle", "내가 만든 크루 관리");

        return "addmate/view_mate_crew"; //
    }

    // 크루원 관리
    // GET/addMate/admin/{mateNo}
    @GetMapping("/admin/{mateNo}")
    public String adminCrew(@PathVariable Long mateNo, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        AddMate mate = addMateService.findById(mateNo);

        // 내가 만든 크루인지 확인
        if (!mate.getUserNo().equals(loginUser.getUserNo())) {
            return "redirect:/addMate/my/crew"; // 권한 없으면 목록으로
        }

        List<MateMemberDto> members = mateMemberService.getCrewMembers(mateNo);

        model.addAttribute("mate", mate);
        model.addAttribute("members", members);
        model.addAttribute("pageTitle", "크루원 관리");

        return "addmate/admin_crew";
    }

    // 크루원 강퇴
    // GET/addMate/kick/{mateNo}/{memberNo}
    @GetMapping("/kick/{mateNo}/{memberNo}")
    public String kickMember(@PathVariable Long mateNo, @PathVariable Long memberNo, RedirectAttributes rttr) {
        try {
            mateMemberService.kickMember(memberNo);
            rttr.addFlashAttribute("successMessage", "크루원을 강퇴했습니다.");
        } catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", "강퇴 실패");
        }
        return "redirect:/addMate/admin/" + mateNo;
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
}
