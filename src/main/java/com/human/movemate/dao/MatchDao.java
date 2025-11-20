package com.human.movemate.dao;

import com.human.movemate.dto.MatchingHistoryDto;
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
public class MatchDao {
    private final JdbcTemplate jdbc;

    // 매칭 기록 조회
    public List<MatchingHistoryDto> findByNo(Long userNo) {
        @Language("SQL")
        String sql = """
        SELECT M.mate_no, M.sport_type, M.mate_type, M.mate_name, M.region,
               M.image_url, M.created_at, MT.status
        FROM MATE M INNER JOIN MATCHING MT ON M.mate_no = MT.mate_no
        WHERE MT.applicant_user_no = ? AND MT.status = 'mate'
        """;

        return jdbc.query(sql, new MatchHistoryRowMapper(), userNo);
    }

    // 매칭 기록 조회
    public List<MatchingHistoryDto> findByType(Long userNo, String mateType, String sportType) {
        @Language("SQL")
        String sql = """
        SELECT M.mate_no, M.sport_type, M.mate_type, M.mate_name, M.region,
               M.image_url, M.created_at, MT.status
        FROM MATE M INNER JOIN MATCHING MT ON M.mate_no = MT.mate_no
        WHERE MT.applicant_user_no = ? AND MT.status = 'mate' AND M.mate_type = ? AND M.sport_type = ?
        """;

        return jdbc.query(sql, new MatchHistoryRowMapper(), userNo, mateType, sportType);
    }

    // MATCHING 테이블에 데이터 저장!
    public void save(Long mateNo, Long applicantUserNo) {
        @Language("SQL")
        String sql = "INSERT INTO MATCHING (mate_no, applicant_user_no, status) VALUES (?, ?, 'PENDING')";

        jdbc.update(sql, mateNo, applicantUserNo);
    }

    // RowMapper
    static class MatchHistoryRowMapper implements RowMapper<MatchingHistoryDto> {
        @Override
        public MatchingHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MatchingHistoryDto(
                    rs.getString("status"),
                    rs.getLong("mate_no"),
                    rs.getString("mate_type"),
                    rs.getString("sport_type"),
                    rs.getString("mate_name"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("image_url"),
                    rs.getString("region")
            );
        }
    }
}