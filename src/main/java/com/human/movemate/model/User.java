package com.human.movemate.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 모델 : DB 테이블과 같은 구조의 객체(붕어빵)를 생성할 수 있는 클래스(붕어빵 틀)

// @ 붙은 애들을 어노테이션이라고 부름
// Getter : 객체의 값을 [추출]하기 위한 getter를 생성해줌
// Setter : 객체에 값을 [설정]하기 위한 setter를 생성해줌

// NoArgConstructor : [매개변수가 없는 생성자]를 만들어줌.
//          -> new User(); 로 멤버 객체를 생성할 수 있음

// AllArgsConstructor : [매개변수가 전부 있는 생성자]를 만들어줌
//          -> new User(id, email, tel, pwd, name, regDate);로 멤버 객체를 생성할 수 있음

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class User {
    // DB 테이블의 컬럼과 대응되는 이름, 자료형을 가진 인스턴스 필드 생성
    // DB { (자료형) Number (이름) id } -> 모델 { (자료형) Long (이름) id }
    private Long userNo;
    private String name;
    private String userId;
    private String password;
    private String email;
    private String phoneNo;
    private String isDeleted;
    private LocalDateTime deletedAt;
}
