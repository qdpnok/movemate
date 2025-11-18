package com.human.movemate.service.impl;
import com.human.movemate.dao.AddMateDao;
import com.human.movemate.dto.AddMateFormDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.service.AddMateService;
import com.human.movemate.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddMateServiceImpl implements AddMateService {
    private final AddMateDao addMateDao; // 메이트 DB 담당
    private final FileStorageService fileStorageService; // 파일 저장 담당 (기존 것)

    @Override
    public void saveMate(AddMateFormDto addMateFormDto, Long userNo) {
        // 파일 저장 로직 (FileStorageService 재활용)
        MultipartFile file = addMateFormDto.getMateImage();
        String storedFileName = null; // DB에 저장될 파일 경로
        if (file != null && !file.isEmpty()) {
            // "mates"라는 하위 폴더에 (userNo) ID 기반으로 파일 저장
            // 예: "mates/1_abc.jpg"
            storedFileName = fileStorageService.storeFile(file, "mates", userNo);
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
}
