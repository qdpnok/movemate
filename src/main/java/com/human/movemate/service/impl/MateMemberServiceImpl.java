package com.human.movemate.service.impl;

import com.human.movemate.dao.MatchDao; // MatchDao 추가함
import com.human.movemate.dao.MateMemberDao;
import com.human.movemate.dto.ApplyReqDto;
import com.human.movemate.dto.MateApplyFormDto;
import com.human.movemate.dto.MateMemberDto;
import com.human.movemate.model.MateMember;
import com.human.movemate.service.FileStorageService; // 파일 서비스 (이름 확인 필요)
import com.human.movemate.service.MateMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MateMemberServiceImpl implements MateMemberService {

    private final MateMemberDao mateMemberDao;
    private final MatchDao matchDao; // 매칭 테이블 저장을 위해 필요해
    private final FileStorageService fileStorageService; // 파일 저장 도와주는 친구

    // 1. 팀원이 만든 기능 구현 (크루원 목록)
    @Override
    public List<MateMemberDto> getCrewMembers(Long mateNo) {
        return null;
    }

    // 2. 팀원이 만든 기능 구현 (강퇴)
    @Override
    public void kickMember(long memberNo) {
        // mateMemberDao.kickMember(memberNo);
    }

    // 3. [본인 추가] 메이트 신청 기능
    @Override
    public boolean applyForMate(ApplyReqDto applyReq) throws IOException {
        return mateMemberDao.save(applyReq);
    }
}