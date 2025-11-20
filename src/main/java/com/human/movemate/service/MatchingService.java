package com.human.movemate.service;

import com.human.movemate.dto.MatchingHistoryDto;

import java.util.List;

public interface MatchingService {
    List<MatchingHistoryDto> findHistoryByNo(Long no);
}
