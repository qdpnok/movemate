package com.human.movemate.service.impl;


import com.human.movemate.dao.MateDao;
import com.human.movemate.model.AddMate;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.model.Post;
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
    public List<AddMate> findTop3Crew() {
        return mateDao.findTop3Crew();
    }

}
