package com.human.movemate.dao;

import com.human.movemate.model.AddMate; // (AddMate 모델 사용)
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
// (추후 목록/상세 조회를 위한 get, getById, RowMapper 메서드 필요)