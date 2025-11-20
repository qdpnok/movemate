package com.human.movemate.service;

import com.human.movemate.dto.ApplyReqDto;
import com.human.movemate.dto.MateApplyFormDto; // ⭐️ 이거 임포트 추가!
import com.human.movemate.dto.MateMemberDto;
import java.io.IOException; // ⭐️ 이것도 추가!
import java.util.List;

public interface MateMemberService {
    // (팀원이 만든 코드)
    List<MateMemberDto> getCrewMembers(Long mateNo);
    void kickMember(long memberNo);

    // 언니! 여기에 메이트 신청하기 기능도 추가되었습니당!
    void applyForMate(MateApplyFormDto formDto, Long userNo) throws IOException;
}
