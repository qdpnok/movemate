package com.human.movemate.service;

import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.model.Post;
import com.human.movemate.model.UserProfile;

import java.util.List;

public interface MateService {
    List<MatchingDto> findReceivedApplications(Long userNo); // 받은 신청자
    List<MatchingDto> findSentApplications(Long userNo);     // 보낸 신청자
    MatchingDetailDto getMatchingDetail(Long matchNo, Long loggedInUserNo);

    // 모든 메이트 목록을 가져오는 기능
    List<AddMate> findAllMates();

    // (추후 상세보기를 위한 기능도 추가할 수 있어요)
    // AddMate findMateById(Long mateNo);

    List<AddMate> findTop3Crew();

    List<AddMate> findTop3Solo();
}
