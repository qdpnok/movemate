package com.human.movemate.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserPro {
    private Long userNo;
    private String name;
    private String userId;
    private String password;
    private String email;
    private String phoneNo;
    private Long profileId;
    private String gender;
    private Long age;
    private String preferMatchingType;
    private String region;
    private String sportType;
    private String paceDetail;
    private String profileImageUrl;
}
