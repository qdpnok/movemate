package com.human.movemate.dao;

import com.human.movemate.dto.MatchingHistoryDto;
import com.human.movemate.model.Post;
import com.human.movemate.model.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MatchDao {
    private final JdbcTemplate jdbc;

    public List<MatchingHistoryDto> findByNo(Long userNo) {
        final String matchTypeDerivation = """
            (CASE
                WHEN BT.board_name LIKE '%크루%' THEN 'crew'
                WHEN BT.board_name LIKE '%1:1%' THEN '1v1'
                ELSE 'unknown'
            END) AS match_type
        """;

        @Language("SQL")
        String sql = """
        (
            -- 1. 내가 '신청자'인 경우 (상대방 = 글쓴이)
            -- MATE(MT) 테이블의 글쓴이(U_Author) 정보를 상대방으로 가져옴
            SELECT M.match_no, M.status, M.applicant_user_no, 
                   MT.mate_no, MT.mate_type, MT.mate_name, MT.region, MT.created_at, 
                   U_Author.name AS opponent_name, U_Author.user_id AS opponent_user_id,
                   UP_Author.profile_image_url AS opponent_profile_image_url
            FROM MATCHING M
                JOIN MATE MT ON M.post_no = MT.mate_no  -- 매칭 테이블의 post_no가 MATE 테이블의 mate_no를 참조
                JOIN USERS U_Author ON MT.user_no = U_Author.user_no
                JOIN USER_PROFILE UP_Author ON U_Author.user_no = UP_Author.user_no
            WHERE M.applicant_user_no = ? 
        )
        UNION
        (
            -- 2. 내가 '글쓴이'인 경우 (상대방 = 신청자)
            -- MATCHING(M) 테이블의 신청자(U_App) 정보를 상대방으로 가져옴
            SELECT M.match_no, M.status, M.applicant_user_no, 
                   MT.mate_no, MT.mate_type, MT.mate_name, MT.region, MT.created_at, 
                   U_App.name AS opponent_name, U_App.user_id AS opponent_user_id,
                   UP_App.profile_image_url AS opponent_profile_image_url
            FROM MATCHING M
                JOIN MATE MT ON M.post_no = MT.mate_no
                JOIN USERS U_App ON M.applicant_user_no = U_App.user_no
                JOIN USER_PROFILE UP_App ON U_App.user_no = UP_App.user_no
            WHERE MT.user_no = ? 
              AND M.applicant_user_no != ? 
        )
        ORDER BY created_at DESC
        """;

        return jdbc.query(sql, new MatchHistoryRowMapper(), userNo, userNo, userNo);
    }

    // RowMapper
    static class MatchHistoryRowMapper implements RowMapper<MatchingHistoryDto> {
        @Override
        public MatchingHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MatchingHistoryDto(
                    rs.getLong("match_no"),
                    rs.getString("status"),
                    rs.getLong("applicant_user_no"),
                    rs.getLong("mate_no"),
                    rs.getString("mate_type"),
                    rs.getString("mate_name"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("opponent_name"),
                    rs.getString("opponent_user_id"),
                    rs.getString("opponent_profile_image_url")
            );
        }
    }
}
