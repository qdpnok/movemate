package com.human.movemate.service.impl;


import com.human.movemate.dao.MateDao;
import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.service.MateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MateServiceImpl implements MateService {

    private final MateDao mateDao;

    @Override
    public List<MatchingDto> findReceivedApplications(Long userNo) {
        return mateDao.findReceivedMatchings(userNo);
    }

    @Override
    public List<MatchingDto> findSentApplications(Long userNo) {
        return mateDao.findSentMatchings(userNo);
    }

    @Override
    public MatchingDetailDto getMatchingDetail(Long matchNo, Long loggedInUserNo) { // ★ 시그니처 수정

        // 1. DAO를 통해 원시 데이터 (신청자 프로필 기준)를 가져옵니다.
        MatchingDetailDto rawDetail = mateDao.findMatchingDetail(matchNo);

        if (rawDetail == null) return null;

        // 2. 로그인 유저가 모집글 작성자인지 확인합니다. (Controller에서 이미 로직 분기했으므로 Service에서는 DTO 재가공만 초점)
        boolean isWriter = rawDetail.getMateWriterNo().equals(loggedInUserNo);

        // 3. 만약 '보낸 신청'인 경우 (로그인 유저 == 신청자) DTO 재가공 로직 필요
        if (!isWriter) {
            // [TODO: findUserProfile 메소드를 DAO에 추가하고, 이 부분에서 작성자의 프로필 정보를 조회하여 DTO 필드를 덮어쓰는 로직이 필요]
        }

        return rawDetail;
    }
}
