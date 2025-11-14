package com.human.movemate.dao;

import com.human.movemate.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserProfileDao {
    private final JdbcTemplate jdbc;

    public void save(UserProfile profile) {
        @Language("SQL")
        String sql = """
        INSERT INTO USER_PROFILE (user_no, gender, age, region, sport_type, pace_detail)
        VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbc.update(sql, profile.getUserNo(), profile.getGender(),
                profile.getAge(), profile.getRegion(), profile.getSportType(),
                profile.getPaceDetail());
    }
}
