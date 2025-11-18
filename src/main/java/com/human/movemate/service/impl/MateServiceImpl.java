package com.human.movemate.service.impl;


import com.human.movemate.dao.MateDao;
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
}
