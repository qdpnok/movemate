package com.human.movemate.controller;

import com.human.movemate.model.AddMate;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.service.MateService;
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
            @RequestParam(required = false) Long userNo,
            Model model
    ) {
        // 테스트용 기본값 설정 (로그인 없이도 테스트 가능)
        if (userNo == null) {
            userNo = 1L; // 임의 테스트 유저 번호
        }

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
        // 이 로직은 문제가 없으며, DB에서 가져온 postType(board_name) 값으로 그룹화
        Map<String, List<MatchingDto>> groupedList = list.stream()
                .collect(Collectors.groupingBy(MatchingDto::getPostType));

        model.addAttribute("groupedList", groupedList);
        model.addAttribute("type", type);
        model.addAttribute("userNo", userNo);

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
}
