package com.human.movemate.dao;

import com.human.movemate.dto.UserPro;
import com.human.movemate.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public UserPro findByNo(Long no) {
        // SELECT [칼럼 1], [칼럼 2], ... , [칼럼 n] (또는 * - ALL)
        // FROM [테이블명]
        // WHERE [조건] -> 특별한 조건이 없다면 생략, ?로 값 대입 가능
        @Language("SQL")
        String sql = """
            SELECT *
            FROM USERS INNER JOIN USER_PROFILE
            WHERE USERS.user_no = USER_PROFILE.user_no;
            """;

        // jdbc.query : 여러 행의 값을 가져올 때 사용함
        // query( sql, new RowMapper(), ?에 답을 값1, ... , ?에 담을 값 );
        List<UserPro> list = jdbc.query(sql, new UserProRowMapper(), no);

        // return 으로 반환하는 값은 메서드의 반환 타입과 일치해야함.

        // DB에서 일치하는 id 가 없으면 빈 리스트가 넘어옴.

        // 값을 하나만 가져오는 queryForObject는 반드시 하나의 값이 검색되는 것이 보장되어야 하기 때문에
        // query를 사용해서 리스트로 값을 가져온다음, 값이 있는지 없는지 체크해서
        // 값이 없다면 null을, 있다면 첫 번째 값을 반환함.
        return list.isEmpty() ? null : list.get(0);
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
                    rs.getLong("profileId"),
                    rs.getString("gender"),
                    rs.getLong("age"),
                    rs.getString("preferMatchingType"),
                    rs.getString("region"),
                    rs.getString("sportType"),
                    rs.getString("paceDetail"),
                    rs.getString("profileImageUrl")
                    // 오라클에서 날짜 타입은 timestamp 밖에 없기 때문에 날짜 타입을 가져올 때
                    // .toLocalDateTime() 메서드를 사용해 localDateTime 타입으로 변환을 해줘야함.
            );
        }
    }

}
