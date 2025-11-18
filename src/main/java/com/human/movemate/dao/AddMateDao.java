package com.human.movemate.dao;

import com.human.movemate.model.AddMate;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// 'ADD_MATE' 테이블에 접근하는 DAO
@Repository
@RequiredArgsConstructor
public class AddMateDao {
    private final JdbcTemplate jdbc;

    // 메이트 모집 글을 DB에 INSERT
    // @param mate Service가 전달한 AddMate 모델 객체
    public void save(AddMate mate) {
        // mate_no, created_at은 DB의 트리거/디폴트로 자동 생성됨
        @Language("SQL")
        String sql = "INSERT INTO ADD_MATE (user_no, mate_type, region, sport_type, mate_name, description, image_url) " +
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

    // (추후 목록/상세 조회를 위한 get, getById, RowMapper 메서드 필요)
}
