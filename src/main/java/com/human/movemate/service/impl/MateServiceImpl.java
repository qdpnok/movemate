package com.human.movemate.service.impl;


import com.human.movemate.dto.ApplyResDto;
import com.human.movemate.dao.MateDao;
import com.human.movemate.dao.MateMemberDao;
import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.service.MateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MateServiceImpl implements MateService {

    private final MateDao mateDao; // 창고지기(DAO)를 부름
    private final MateMemberDao mateMemberDao;

    @Override
    public List<AddMate> findAllMates() {
        // DAO에게 모든 메이트 목록을 가져오라고 시킴
        return mateDao.findAll();
    }

    @Override
    public List<ApplyResDto> findReceivedApplications(Long userNo) {
        return mateMemberDao.findSentApply(userNo);
    }

    @Override
    public List<ApplyResDto> findSentApplications(Long userNo) {
        return mateMemberDao.findReceivedApply(userNo);
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

    @Override
    public List<AddMate> findTop3Crew() {
        return mateDao.findTop3Crew();
    }

    @Override
    public List<AddMate> findTop3Solo() {
        return mateDao.findTop3Solo();
    }

}
