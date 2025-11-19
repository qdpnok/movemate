package com.human.movemate.service.impl;

import com.human.movemate.dao.MateMemberDao;
import com.human.movemate.dto.MateMemberDto;
import com.human.movemate.service.MateMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateMemberServiceImpl implements MateMemberService {
    private final MateMemberDao mateMemberDao;

    @Override
    public List<MateMemberDto> getCrewMembers(Long mateNo) {
        return mateMemberDao.findMembersByMateNo(mateNo);
    }

    @Override
    public void kickMember(long memberNo) {
        mateMemberDao.deleteMember(memberNo);

    }
}
