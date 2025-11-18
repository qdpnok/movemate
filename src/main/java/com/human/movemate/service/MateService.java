package com.human.movemate.service;

import com.human.movemate.dto.MatchingDto;
import com.human.movemate.model.UserProfile;

import java.util.List;

public interface MateService {
    List<MatchingDto> findReceivedApplications(Long userNo); // 받은 신청자
    List<MatchingDto> findSentApplications(Long userNo);     // 보낸 신청자
}
