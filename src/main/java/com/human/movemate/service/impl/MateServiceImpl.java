package com.human.movemate.service.impl;

import com.human.movemate.dao.AddMateDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import com.human.movemate.dao.MateDao;
import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.model.Post;
import com.human.movemate.service.MateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MateServiceImpl implements MateService {

    private final MateDao mateDao; // 창고지기(DAO)를 부름
    private final AddMateDao addMateDao; // 창고지기(DAO)를 부름

    @Override
    public List<AddMate> findAllMates() {
        // DAO에게 모든 메이트 목록을 가져오라고 시킴
        return mateDao.findAll();
    }

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

    @Override
    public List<AddMate> findTop3Crew() {
        return mateDao.findTop3Crew();
    }

    @Override
    public List<AddMate> findTop3Solo() {
        return mateDao.findTop3Solo();
    }

    // 매칭 상태 변경 로직
    @Override
    @Transactional // DB 작업을 하나의 논리적 단위로 묶어줍니다.
    public void updateMatchingStatus(Long matchNo, String status) {

        // 1. DAO를 호출하여 매칭 상태를 변경
        mateDao.updateStatus(matchNo, status);

        // 2. [비즈니스 로직]: 수락 시 (status == "ACCEPT")
        if ("ACCEPT".equals(status)) {
            try {
                // 2-1. 매칭 번호로 모집글 번호를 찾습니다.
                Long mateNo = mateDao.findMateNoByMatchNo(matchNo);

                // 2-2. 해당 모집글의 현재 인원(current_members)을 1 증가시킵니다.
                addMateDao.incrementCurrentMembers(mateNo); // ⬅️ AddMateDao 사용으로 수정

                // 2-3. ★★★ 핵심: MATE_MEMBER 테이블에 신청자 추가 로직 (MateMemberDao가 필요)
                // MatchingDetailDto detail = mateDao.findMatchingDetail(matchNo);
                // if (detail != null && mateMemberDao != null) {
                //     mateMemberDao.save(new MateMember(mateNo, detail.getApplicantNo()));
                // }

                log.info("매칭 수락 완료: MatchNo={}, MateNo={}. MATE 인원 1 증가 처리됨.", matchNo, mateNo);

            } catch (EmptyResultDataAccessException e) {
                log.error("매칭({})에 연결된 모집글(Mate)을 찾을 수 없습니다. 인원 증가 실패. 트랜잭션 롤백됨.", matchNo);
                throw new RuntimeException("매칭된 모집글 정보를 찾을 수 없습니다.", e);
            }
        }

        log.info("매칭 상태 변경 완료: MatchNo={}, Status={}", matchNo, status);
    }

    @Override
    public List<MatchingDto> findAcceptedMatchings(Long userNo) {
        // 1. 내가 만든 글에 수락된 사람 목록 (내가 수락자)
        List<MatchingDto> receivedAccepts = mateDao.findAcceptedMatchingsByWriter(userNo);

        // 2. 내가 신청해서 수락된 글 목록 (내가 신청자)
        List<MatchingDto> sentAccepts = mateDao.findAcceptedMatchingsByApplicant(userNo);

        // 3. 두 목록을 합치기 (나의 모든 매칭 완료 목록)
        List<MatchingDto> combinedList = new ArrayList<>(receivedAccepts);
        combinedList.addAll(sentAccepts);

        return combinedList;
    }

}
