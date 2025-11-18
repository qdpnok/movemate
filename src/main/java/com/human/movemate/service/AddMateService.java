package com.human.movemate.service;
import com.human.movemate.dto.AddMateFormDto;

// '메이트 모집' 관련 비즈니스 로직 인터페이스
// 메이트 모집 글 저장 (파일 업로드 포함)
public interface AddMateService {
    void saveMate(AddMateFormDto addMateFormDto, Long userNo);
}
