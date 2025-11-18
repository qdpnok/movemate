package com.human.movemate.dao;

import com.human.movemate.model.AddMate;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

// 'MATE' 테이블에 접근하는 DAO
@Repository
@RequiredArgsConstructor
public class AddMateDao {
    private final JdbcTemplate jdbc;

    // 메이트 모집 글을 DB에 INSERT
    // @param mate Service가 전달한 AddMate 모델 객체
    public void save(AddMate mate) {
        // mate_no, created_at은 DB의 트리거/디폴트로 자동 생성됨
        @Language("SQL")
        String sql = "INSERT INTO MATE (user_no, mate_type, region, sport_type, mate_name, description, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql,
                mate.getUserNo(),
                mate.getMateType(),
                mate.getRegion(),
                mate.getSportType(),
                mate.getMateName(),
                mate.getDescription(),
                mate.getImageUrl()
        );
    }

    // 글 상세 조회 (JOIN 포함)
    public AddMate getById(Long mateNo) {
        @Language("SQL")
        String sql = "SELECT " +
                "  m.mate_no, m.user_no, m.mate_type, m.region, m.sport_type, " +
                "  m.mate_name, m.description, m.image_url, m.created_at, " +
                "  u.user_id, up.profile_image_url " +
                "FROM MATE m " +
                "JOIN USERS u ON m.user_no = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE m.mate_no = ?";
        try {
            return jdbc.queryForObject(sql, new AddMateRowMapper(), mateNo);
        } catch (EmptyResultDataAccessException e) {
            return null; // 글이 없으면 null
        }
    }

    // 수정
    public void update(AddMate mate) {
        String sql;
        if (mate.getImageUrl() != null) {
            // 이미지 파일이 새로 첨부된 경우
            sql = "UPDATE MATE SET mate_type = ?, region = ?, sport_type = ?, " +
                    "mate_name = ?, description = ?, image_url = ? " +
                    "WHERE mate_no = ?";
            jdbc.update(sql,
                    mate.getMateType(), mate.getRegion(), mate.getSportType(),
                    mate.getMateName(), mate.getDescription(), mate.getImageUrl(),
                    mate.getMateNo());
        } else {
            // 이미지 수정이 없는 경우 (기존 이미지 유지)
            sql = "UPDATE MATE SET mate_type = ?, region = ?, sport_type = ?, " +
                    "mate_name = ?, description = ? " +
                    "WHERE mate_no = ?";
            jdbc.update(sql,
                    mate.getMateType(), mate.getRegion(), mate.getSportType(),
                    mate.getMateName(), mate.getDescription(),
                    mate.getMateNo());
        }
    }

    // 글 삭제
    public void deleteById(Long mateNo) {
        @Language("SQL")
        String sql = "DELETE FROM MATE WHERE mate_no = ?";
        jdbc.update(sql, mateNo);
    }


    // DB 결과를 AddMate 모델로 매핑하는 RowMapper
    static class AddMateRowMapper implements RowMapper<AddMate> {
        @Override
        public AddMate mapRow(ResultSet rs, int rowNum) throws SQLException {
            AddMate mate = new AddMate();
            mate.setMateNo(rs.getLong("mate_no"));
            mate.setUserNo(rs.getLong("user_no"));
            mate.setMateType(rs.getString("mate_type"));
            mate.setRegion(rs.getString("region"));
            mate.setSportType(rs.getString("sport_type"));
            mate.setMateName(rs.getString("mate_name"));
            mate.setDescription(rs.getString("description"));
            mate.setImageUrl(rs.getString("image_url"));
            mate.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            // JOIN된 필드
            mate.setAuthorId(rs.getString("user_id"));
            mate.setAuthorProfileUrl(rs.getString("profile_image_url"));

            return mate;
        }
    }
}
