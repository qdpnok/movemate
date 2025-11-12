package com.human.movemate.service;

import com.human.movemate.model.User;

// MemberServiceImpl로 상속을 줄 인터페이스.
// MemberService에 미리 선언해둔 메서드는 무조건 MemberServiceImpl에 있어야 하며,
// 접근제한자, 반환타입, 이름, 매개변수가 일치해야한다.
public interface MemberService {
    // 회원 가입
    boolean signup(User user);

    // 로그인
    User login(User user);

    // 회원 정보 수정
    boolean update(Long no, User user);

    // 회원 정보 삭제
    boolean delete(Long no);
}
