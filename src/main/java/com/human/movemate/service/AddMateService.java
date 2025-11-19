package com.human.movemate.service;
import com.human.movemate.dto.AddMateFormDto;
import com.human.movemate.model.AddMate;

import java.util.List;

// '메이트 모집' 관련 비즈니스 로직 인터페이스
// 메이트 모집 글 저장 (파일 업로드 포함)
public interface AddMateService {
    void saveMate(AddMateFormDto addMateFormDto, Long userNo);
    AddMate findById(Long mateNo); // 게시글 상세 조회
    void updateMate(Long mateNo, AddMateFormDto addMateFormDto, Long userNo); // 게시글 수정
    void deleteMate(Long mateNo, Long userNo); // 삭제
    List<AddMate> findMyCrews(Long userNo, String sportType); // 내가 만든 크루 목록 조회


}
