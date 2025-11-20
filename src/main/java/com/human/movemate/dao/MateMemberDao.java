package com.human.movemate.dao;

import com.human.movemate.dto.MateMemberDto;
import com.human.movemate.model.MateMember;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MateMemberDao {
    private final JdbcTemplate jdbc;

    // 특정 크루(mateNo)의 멤버 목록 조회
    public List<MateMemberDto> findMembersByMateNo(Long mateNo) {
        @Language("SQL")
        String sql = "SELECT mm.member_no, mm.user_no, mm.joined_at, " +
                "u.name, up.region, up.profile_image_url " +
                "FROM MATE_MEMBER mm " +
                "JOIN USERS u ON mm.user_no = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE mm.mate_no = ? " +
                "ORDER BY mm.joined_at ASC";
        return jdbc.query(sql, (rs, rowNum) -> new MateMemberDto(
                rs.getLong("member_no"), rs.getLong("user_no"), rs.getString("name"),
                rs.getString("region"), rs.getString("profile_image_url"),
                rs.getTimestamp("joined_at").toLocalDateTime()
        ), mateNo);
    }
    // 크루원 강퇴
    public void deleteMember(Long memberNo) {
        jdbc.update("DELETE FROM MATE_MEMBER WHERE member_no = ?", memberNo);
    }

    public void save(MateMember mateMember) {
    }
}
