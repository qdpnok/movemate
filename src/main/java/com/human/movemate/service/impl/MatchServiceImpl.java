package com.human.movemate.service.impl;

import com.human.movemate.dao.MatchDao;
import com.human.movemate.dto.MatchingHistoryDto;
import com.human.movemate.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchingService {
    private final MatchDao matchDao;

    @Override
    public List<MatchingHistoryDto> findHistoryByNo(Long no) {
        return matchDao.findByNo(no);
    }

    @Override
    public List<MatchingHistoryDto> findByType(Long no, String mateType, String sportType) {
        return matchDao.findByType(no, mateType, sportType);
    }
}

