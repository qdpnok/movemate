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
        } else { // "received"일 경우
            // "received" (받은 신청) 요청 시 -> findReceivedApplications 호출
            list = mateService.findReceivedApplications(userNo);
        }
        // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

        // 1. List를 Map<PostType, List<DTO>> 형태로 그룹화
        Map<String, List<MatchingDto>> groupedList = list.stream()
                .collect(Collectors.groupingBy(MatchingDto::getPostType));

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

}
