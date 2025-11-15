package com.human.movemate.dao;

import com.human.movemate.dto.UserPro;
import com.human.movemate.model.User;
import com.human.movemate.model.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserProfileDao {
    private final JdbcTemplate jdbc;

    public void save(UserProfile profile) {
        @Language("SQL")
        String sql = """
        INSERT INTO USER_PROFILE (user_no, gender, age, region, sport_type, pace_detail, profile_image_url)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        jdbc.update(sql, profile.getUserNo(), profile.getGender(),
                profile.getAge(), profile.getRegion(), profile.getSportType(),
                profile.getPaceDetail(), profile.getProfileImageUrl());
    }

    public UserPro findByNo(Long no) {
        @Language("SQL")
        String sql = """
            SELECT USERS.user_no, name, user_id, password, email, phone_no,
            profile_id, gender, age, prefer_matching_type, region, sport_type,
            pace_detail, profile_image_url
            FROM USERS JOIN USER_PROFILE
            ON USERS.user_no = USER_PROFILE.user_no
            WHERE USERS.user_no = ?
            """;

        List<UserPro> list = jdbc.query(sql, new UserProRowMapper(), no);
        return list.isEmpty() ? null : list.get(0);
    }

    // 유저 번호로 이미지 경로 업로드
    public boolean updateProfile(Long userNo, String path){
        // UPDATE [테이블명] SET [변경할 칼럼명1] = ?, ... , [변경할 칼럼명n] = ? WHERE [조건]
        @Language("SQL")
        String sql = "UPDATE USER_PROFILE SET profile_image_url = ? WHERE user_no = ?";

        // jdbc.update : 데이터 삽입, 수정, 삭제에 사용함.
        // update( sql, ?에 담을 값 1, ... , ?에 담을 값 n);

        // return 으로 반환하는 값은 메서드의 반환 타입과 일치해야함.

        // jdbc.update는 삽입, 수정, 삭제에 성공한 행의 갯수를 반환함.
        // 때문에 결과값이 0보다 큰지 검사해서 성공/실패 여부를 확인
        return jdbc.update(sql, path, userNo) > 0;
    }


    static class  UserProRowMapper implements RowMapper<UserPro> {

        @Override
        public UserPro mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserPro(
                    rs.getLong("user_no"),
                    rs.getString("name"),
                    rs.getString("user_id"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("phone_no"),
                    rs.getLong("profile_id"),
                    rs.getString("gender"),
                    rs.getLong("age"),
                    rs.getString("prefer_matching_type"),
                    rs.getString("region"),
                    rs.getString("sport_type"),
                    rs.getString("pace_detail"),
                    rs.getString("profile_image_url")
                    // 오라클에서 날짜 타입은 timestamp 밖에 없기 때문에 날짜 타입을 가져올 때
                    // .toLocalDateTime() 메서드를 사용해 localDateTime 타입으로 변환을 해줘야함.
            );
        }
    }

}
