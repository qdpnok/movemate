package com.human.movemate.service;

import com.human.movemate.dto.MatchingHistoryDto;

import java.util.List;
import java.util.Map;

public interface MatchingService {
    List<MatchingHistoryDto> findHistoryByNo(Long no);

    List<MatchingHistoryDto> findByType(Long no, String mateType, String sportType);
}
