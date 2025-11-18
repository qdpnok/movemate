package com.human.movemate.dao;

import com.human.movemate.model.AddMate; // (AddMate 모델 재사용)
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MateDao {

    private final JdbcTemplate jdbc;

    // DB 데이터를 AddMate 객체로 변환해주는 '번역기'
    private final RowMapper<AddMate> mateRowMapper = new RowMapper<AddMate>() {
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
            return mate;
        }
    };

    // MATE 테이블의 모든 데이터를 조회하는 메서드
    public List<AddMate> findAll() {
        String sql = "SELECT * FROM MATE ORDER BY created_at DESC"; // 최신순 정렬
        return jdbc.query(sql, mateRowMapper);
    }

    // (추후 상세보기를 위한 메서드도 추가할 수 있어요)
    // public AddMate findById(Long mateNo) { ... }
}