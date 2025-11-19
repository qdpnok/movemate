package com.human.movemate.dao;

import com.human.movemate.model.AddMate; // (AddMate 모델 사용)
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// 'MATE' 테이블에 접근하는 DAO
@Repository
@RequiredArgsConstructor
public class AddMateDao {
    private final JdbcTemplate jdbc;

    // 메이트 모집 글을 DB에 INSERT
    public void save(AddMate mate) {
        // mate_no, created_at은 DB의 트리거/디폴트로 자동 생성됨
        // [수정] 테이블 이름을 'ADD_MATE'에서 'MATE'로 변경
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

    // 내가 만든 크루 목록 조회
    public List<AddMate> findByUserNoAndType(Long userNo, String mateType, String sportType) {
        @Language("SQL")
        String sql = "SELECT m.*, u.user_id, up.profile_image_url " +
                "FROM MATE m " +
                "JOIN USERS u ON m.user_no = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE m.user_no = ? AND m.mate_type = ? ";

        if (sportType != null && !sportType.equals("전체")) {
            sql += "AND m.sport_type = ? ORDER BY m.created_at DESC";
            return jdbc.query(sql, new AddMateRowMapper(), userNo, mateType, sportType);
        } else {
            sql += "ORDER BY m.created_at DESC";
            return jdbc.query(sql, new AddMateRowMapper(), userNo, mateType);
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
            // 메이트 우측하단 크루아이콘 인원수 표기
            try {
                mate.setCurrentMembers(rs.getInt("current_members"));
            } catch (SQLException e) {
                mate.setCurrentMembers(1); // 컬럼 없으면 기본 1명
            }

            // JOIN된 필드
            mate.setAuthorId(rs.getString("user_id"));
            mate.setAuthorProfileUrl(rs.getString("profile_image_url"));
            // 상세보기 우측하단 크루 아이콘에 인원수 반영
            mate.setCurrentMembers(rs.getInt("current_members"));

            return mate;
        }
    }

    //    1:1 메이트 신청 민아
    public AddMate findById(Long mateNo) {
        // 1. MATE 테이블(m.*)을 조회해서 인원수(current_members)를 확보
        // 2. USERS, USER_PROFILE 테이블을 JOIN해서 작성자 이름/사진 확보
        String sql = "SELECT m.*, u.user_id, up.profile_image_url " +
                "FROM MATE m " +
                "JOIN USERS u ON m.user_no = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE m.mate_no = ?";
        try {
            // 아래에 있는 RowMapper를 재사용해서 데이터를 담습니다.
            return jdbc.queryForObject(sql, new AddMateRowMapper(), mateNo);
        } catch (Exception e) {
            return null;
        }
    }
}
