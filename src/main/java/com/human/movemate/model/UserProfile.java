package com.human.movemate.model;

import lombok.*;

// Getter : 객체의 값을 [추출]하기 위한 getter를 생성해줌
// Setter : 객체에 값을 [설정]하기 위한 setter를 생성해줌

// NoArgConstructor : [매개변수가 없는 생성자]를 만들어줌.
//          -> new User(); 로 멤버 객체를 생성할 수 있음

// AllArgsConstructor : [매개변수가 전부 있는 생성자]를 만들어줌
//          -> new User(id, email, tel, pwd, name, regDate);로 멤버 객체를 생성할 수 있음

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserProfile {
    private Long profileId;
    private Long userNo;
    private String gender;
    private Long age;
    private String preferMatchingType;
    private String region;
    private String sportType;
    private String paceDetail;
    private String profileImageUrl;
}
