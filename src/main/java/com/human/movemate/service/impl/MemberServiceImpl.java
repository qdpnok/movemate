package com.human.movemate.service.impl;

import com.human.movemate.dao.MemberDao;
import com.human.movemate.model.Member;
import com.human.movemate.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// @ 붙은 애들을 어노테이션이라고 부름

// Repository : 스프링에 Service(dao를 활용해 실제 기능을 구현하는 역할)로 등록
// RequiredArgsConstructor : 의존성 주입을 축약해줌
// Slf4j : 오류 로그 출력 기능을 제공
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    // MemberDao 클래스의 기능을 사용하기 위한 의존성 주입
    MemberDao memberDao;

    // 상속을 준 인터페이스 (MemberService) 에
    // 생성자 (public), 반환타입 (boolean), 메서드 이름 (signup), 매개변수 (Member member)가
    // 모두 일치하게 정의되어 있어야 함.
    @Override
    public boolean signup(Member member) {
        return memberDao.save(member);
    }

    @Override
    public Member getById(Long id) {
        return memberDao.findById(id);
    }

    @Override
    public boolean update(Long id, Member member) {
        return memberDao.update(id, member);
    }

    @Override
    public boolean delete(Long id) {
        return memberDao.delete(id);
    }
}
