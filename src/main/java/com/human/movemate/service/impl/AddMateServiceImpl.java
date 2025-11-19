package com.human.movemate.service.impl;
import com.human.movemate.dao.AddMateDao;
import com.human.movemate.dao.MateMemberDao;
import com.human.movemate.dto.AddMateFormDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.service.AddMateService;
import com.human.movemate.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddMateServiceImpl implements AddMateService {
    private final AddMateDao addMateDao; // 메이트 DB 담당
    private final FileStorageService fileStorageService; // 파일 저장 담당 (기존 것)
    private final MateMemberDao mateMemberDao;

    // 1:1 메이트 신청 민아
    // 구현체에 메서드 오버라이딩
    @Override
    public AddMate getMateDetail(Long mateNo) {
        return addMateDao.findById(mateNo);
    }


    @Override
    public void saveMate(AddMateFormDto addMateFormDto, Long userNo) {
        // 파일 저장 로직 (FileStorageService 재활용)
        // ★ 글 저장 시 자동으로 매니저 등급이 되는 ? 기능이 되어야 하는데 어찌 해야될지 모르게씀
        // 나중에 해결하자 ㅋ
        MultipartFile file = addMateFormDto.getMateImage();
        String storedFileName = null; // DB에 저장될 파일 경로
        if (file != null && !file.isEmpty()) {
            // "mates"라는 하위 폴더에 (userNo) ID 기반으로 파일 저장
            // 예: "mates/1_abc.jpg"
            storedFileName = fileStorageService.storeFile(file, "mates", null);
        }
        // DTO -> Model(AddMate) 객체로 변환
        AddMate mate = new AddMate();
        mate.setUserNo(userNo); // 작성자 (로그인한 사람)
        mate.setMateType(addMateFormDto.getMateType()); // "SOLO" or "CREW"
        mate.setRegion(addMateFormDto.getRegion());
        mate.setSportType(addMateFormDto.getSportType());
        mate.setMateName(addMateFormDto.getMateName());
        mate.setDescription(addMateFormDto.getDescription());
        mate.setImageUrl(storedFileName); // 저장된 파일 경로/이름 (없으면 null)
        // DAO 호출하여 DB에 INSERT
        addMateDao.save(mate);
        log.info("새 메이트 모집 글 저장 완료: " + mate.getMateName());
    }

    // 상세조회
    @Override
    public AddMate findById(Long mateNo) {
        log.info("메이트 글 조회 시도: {}", mateNo);
        return addMateDao.getById(mateNo);
    }

    // 글 수정
    @Override
    public void updateMate(Long mateNo, AddMateFormDto dto, Long userNo) {
        // 기존 글 조회 (권한 확인 및 기존 파일명 확인)
        AddMate existingMate = addMateDao.getById(mateNo);
        if (existingMate == null || !existingMate.getUserNo().equals(userNo)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        // 새 파일 처리
        MultipartFile file = dto.getMateImage();
        String newImageUrl = null;
        if (file != null && !file.isEmpty()) {
            // 새 파일 저장 ("mates" 폴더, mateNo 기준)
            newImageUrl = fileStorageService.storeFile(file, "mates", mateNo);
            // 기존 파일이 있었다면 삭제
            if (existingMate.getImageUrl() != null) {
                fileStorageService.deleteIfExists(existingMate.getImageUrl());
            }
        }
        // DTO -> Model 변환 (DB 업데이트용)
        AddMate mateToUpdate = new AddMate();
        mateToUpdate.setMateNo(mateNo);
        mateToUpdate.setMateType(dto.getMateType());
        mateToUpdate.setRegion(dto.getRegion());
        mateToUpdate.setSportType(dto.getSportType());
        mateToUpdate.setMateName(dto.getMateName());
        mateToUpdate.setDescription(dto.getDescription());

        if (newImageUrl != null) {
            mateToUpdate.setImageUrl(newImageUrl); // 새 이미지 URL 설정
        }
        // (imageUrl이 null이면 DAO는 기존 이미지를 건드리지 않음)

        // DB 업데이트
        addMateDao.update(mateToUpdate);
    }

    // 삭제
    @Override
    @Transactional // DB삭제와 파일삭제를 묶음
    public void deleteMate(Long mateNo, Long userNo) {
        // 기존 글 조회 (권한 확인 및 파일명 확인)
        AddMate existingMate = addMateDao.getById(mateNo);
        if (existingMate == null) return; // 이미 없음

        if (!existingMate.getUserNo().equals(userNo)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        // DB에서 삭제
        addMateDao.deleteById(mateNo);
        // 로컬 디스크에서 파일 삭제
        if (existingMate.getImageUrl() != null) {
            fileStorageService.deleteIfExists(existingMate.getImageUrl());
        }
}

    @Override
    public List<AddMate> findMyMates(Long userNo, String mateType,String sportType) {
        return addMateDao.findByUserNoAndType(userNo, mateType, sportType);
    }


}


