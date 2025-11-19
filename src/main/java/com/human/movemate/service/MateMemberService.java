package com.human.movemate.service;
import com.human.movemate.dto.MateMemberDto;
import java.util.List;

public interface MateMemberService {
    List<MateMemberDto> getCrewMembers(Long mateNo);
    void kickMember(long memberNo);
}
