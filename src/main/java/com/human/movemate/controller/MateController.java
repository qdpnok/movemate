package com.human.movemate.controller;

import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.model.User;
import com.human.movemate.service.MateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mate")   // 해당 클래스의 기본 경로를 localhost:포트번호/mate 으로 설정
public class MateController {
    private final MateService mateService;


    @GetMapping("")
//    public String mateHome(@RequestParam(required = false, defaultValue = "sent") String type,
//                           @RequestParam Long userNo, Model model) {
//        // type이 sent이면 내가 신청한 목록, received면 받은 목록
//        // Controller에서 URL 파라미터 type을 보고, “sent”인지 “received”인지 판단
//        if ("sent".equals(type)) {
//            List<MatchingDto> sentList = mateService.findReceivedApplications(userNo);
//            model.addAttribute("list", sentList);
//            model.addAttribute("type", "sent");
//        } else if ("received".equals(type)) {
//            List<MatchingDto> receivedList = mateService.findSentApplications(userNo);
//            model.addAttribute("list", receivedList);
//            model.addAttribute("type", "received");
//        }
//        return "post/mateManage";   // Thymeleaf가 찾을 템플릿 파일 경로 설정
//    }

    public String mateHome(
            @RequestParam(required = false, defaultValue = "sent") String type,
            // @RequestParam(required = false) Long userNo, // ⬅️ 삭제하거나 주석 처리
            HttpSession session, // ⬅️ HttpSession 인자 유지
            Model model
    ) {
        // 1. 로그인 유저 객체 정보 확인 (LoginController가 저장한 "loginUser" 키 사용)
        User loginUser = (User) session.getAttribute("loginUser"); // ⬅️ User 객체를 가져옵니다.

        // 로그인 유저가 아니면 /login으로 리다이렉트
        if (loginUser == null) {
            log.warn("비로그인 유저가 메이트 관리 페이지 접근 시도.");
            return "redirect:/login"; // 로그인 페이지 URL로 리다이렉트
        }

        // 2. 로그인 유저 번호를 User 객체에서 추출
        Long userNo = loginUser.getUserNo(); // ⬅️ User 객체의 getUserNo() 메서드를 사용

        log.info("mateHome 호출 - userNo: {}, type: {}", userNo, type);

        List<MatchingDto> list;

        // ★★★★★ 핵심 수정 부분: 타입과 메서드 호출을 일치시킴 ★★★★★
        if ("sent".equals(type)) {
            // "sent" (보낸 신청) 요청 시 -> findSentApplications 호출
            list = mateService.findSentApplications(userNo);
        } else if ("received".equals(type)) {
            // // "received" (받은 신청) 요청 시 -> findReceivedApplications 호출
            list = mateService.findReceivedApplications(userNo);
        } else if ("accepted".equals(type)) {
            // [추가된 로직] "accepted" (매칭 완료) 요청 시 -> findAcceptedMatchings 호출
            list = mateService.findAcceptedMatchings(userNo);
        } else {
            // 정의되지 않은 타입이 들어왔을 경우 기본값(sent) 처리
            log.warn("알 수 없는 매칭 타입 요청: {}", type);
            list = mateService.findSentApplications(userNo);
            type = "sent";
        }

        // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

        // 1. List를 Map<PostType, List<DTO>> 형태로 그룹화
//        Map<String, List<MatchingDto>> groupedList = list.stream()
//                .collect(Collectors.groupingBy(MatchingDto::getPostType));

//        Map<String, List<MatchingDto>> groupedList = list.stream()
//                .collect(Collectors.groupingBy(
//                        // 그룹 키를 결정하는 함수: SOLO만 '1:1'로 변환하고 나머지는 그대로 사용
//                        dto -> {
//                            String postType = dto.getPostType();
//                            if ("SOLO".equals(postType)) {
//                                return "1:1";
//                            }
//                            // "CREW"를 포함한 나머지 모든 타입은 원본 값 그대로 반환
//                            return postType;
//                        }
//                ));
        Map<String, List<MatchingDto>> groupedList = list.stream()
                .collect(Collectors.groupingBy(this::getGroupingKey));

        model.addAttribute("groupedList", groupedList);
        model.addAttribute("type", type);
        model.addAttribute("userNo", userNo); // ⬅️ 추출한 userNo를 사용

        // DTO 로그는 디버깅에 매우 유용합니다.
        if (list != null && !list.isEmpty()) {
            log.info("--- DTO List Content Start (Size: {}) ---", list.size());
            for (MatchingDto dto : list) {
                log.info("DTO Item: {}", dto);
            }
            log.info("--- DTO List Content End ---");
        } else {
            log.info("조회된 Matching 목록이 비어 있습니다. userNo: {}", userNo);
        }

        return "post/mateManage";
    }

    /**
     * MatchingDto의 타입(SOLO/CREW)과 운동 종류(러닝/웨이트)를 조합하여 그룹핑 키를 생성합니다.
     */
    private String getGroupingKey(MatchingDto matching) {
        String type = matching.getPostType();   // SOLO 또는 CREW
        // ★★★ Null 방지 코드 추가: sportType이 null이면 빈 문자열("")로 처리합니다. ★★★
        String sport = (matching.getSportType() != null) ? matching.getSportType() : "";

        if ("SOLO".equals(type)) {
            // 비교할 때도 안정성을 위해 리터럴 문자열을 앞에 둡니다.
            if ("러닝".equals(sport)) return "1:1 러닝 신청";
            if ("웨이트".equals(sport)) return "1:1 웨이트 신청";
        } else if ("CREW".equals(type)) {
            if ("러닝".equals(sport)) return "러닝 크루 신청";
            if ("웨이트".equals(sport)) return "웨이트 크루 신청";
        }

        // DAO에서 sportType을 가져오지 못한 경우
        log.warn("분류 실패: Type={}, Sport={}", type, sport.isEmpty() ? "NULL" : sport);
        return type + " " + sport + " (분류 오류)";
    }

    @GetMapping("/detail")
    public String matchingDetail(
            @RequestParam("matchNo") Long matchNo,
            HttpSession session,
            Model model
    ) {
        // 1. 로그인 유저 정보 확인
        // "loginUser" 객체를 가져와 userNo를 추출
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            log.warn("비로그인 유저가 매칭 상세 조회 시도.");
            return "redirect:/login";
        }

        // User 객체에서 userNo를 추출하여 사용합니다.
        Long loggedInUserNo = loginUser.getUserNo(); // ⬅️ 이 부분을 수정했습니다.

        // 2. 상세 데이터 조회
        MatchingDetailDto matchingDetail = mateService.getMatchingDetail(matchNo, loggedInUserNo);

        if (matchingDetail == null) {
            return "redirect:/mate"; // 데이터 없으면 목록으로
        }

        // 3. 핵심 => 받은 신청 vs 보낸 신청 vs 제3자 접근 구분
        String viewType;

        // ... (이하 로직은 loggedInUserNo를 사용하므로 수정 없이 그대로 유지) ...
        if (matchingDetail.getMateWriterNo().equals(loggedInUserNo)) {
            // A. 로그인 유저 == 모집글 작성자: 받은 신청
            viewType = "RECEIVED";
        } else if (matchingDetail.getApplicantNo().equals(loggedInUserNo)) {
            // B. 로그인 유저 == 신청자: 보낸 신청
            viewType = "SENT";
        } else {
            // C. 권한 없는 유저
            log.warn("권한 없는 유저({})가 매칭({}) 조회 시도.", loggedInUserNo, matchNo);
            return "redirect:/mate"; // 권한 없으면 목록으로 리다이렉트
        }

        // 4. View에 데이터 전달
        model.addAttribute("detail", matchingDetail);
        model.addAttribute("viewType", viewType);

        return "post/mateDetail";       // 뷰 경로
    }

    // -------------------------------------------------------------
    // ★★★ 수락/거절 폼 제출 처리 엔드포인트 구현 ★★★
    // -------------------------------------------------------------
    @PostMapping("/matching/action")
    public String handleMatchingAction(
            @RequestParam Long matchNo,
            @RequestParam String action, // "ACCEPT" 또는 "REJECT"
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            log.warn("비로그인 유저가 매칭 처리 시도.");
            return "redirect:/login";
        }
        Long loggedInUserNo = loginUser.getUserNo();

        // 1. 권한 검사를 위한 상세 정보 조회
        MatchingDetailDto detail = mateService.getMatchingDetail(matchNo, loggedInUserNo);

        // 유효성 및 권한 검사
        if (detail == null ||
                !(action.equals("ACCEPT") || action.equals("REJECT")) ||
                !detail.getMateWriterNo().equals(loggedInUserNo))
        {
            log.warn("잘못된 요청 또는 권한 없는 유저({})가 매칭({}) 처리 시도.", loggedInUserNo, matchNo);
            // 권한이 없거나 요청이 잘못되면 상세 페이지로 복귀
            return "redirect:/mate/detail?matchNo=" + matchNo;
        }

        try {
            // 2. Service 호출하여 상태 변경 및 인원 증가 로직 처리
            mateService.updateMatchingStatus(matchNo, action);
            log.info("매칭 처리 완료: MatchNo={}, Action={}", matchNo, action);

            // 3. 처리가 완료되면 '받은 신청 조회' 목록으로 이동 (목록에서 사라짐)
            return "redirect:/mate?type=received";

        } catch (Exception e) {
            log.error("매칭 처리 중 오류 발생: MatchNo={}", matchNo, e);
            // 오류 발생 시 상세 페이지로 리다이렉트
            return "redirect:/mate/detail?matchNo=" + matchNo;
        }
    }

}
