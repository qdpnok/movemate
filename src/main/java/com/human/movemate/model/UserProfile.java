package com.human.movemate.model;

import lombok.*;

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
