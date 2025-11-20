package com.human.movemate.dao;

import com.human.movemate.dto.ApplyReqDto;
import com.human.movemate.dto.ApplyResDto;
import com.human.movemate.dto.MateMemberDto;
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

    public boolean save(ApplyReqDto applyReqDto) {
        @Language("SQL")
        String sql = "INSERT INTO MATE_APPLICATION (mate_no, applicant_user_no, application_message) VALUES (?, ?, ?)";

        return jdbc.update(sql, applyReqDto.getMateNo(), applyReqDto.getApplicantUserNo(), applyReqDto.getApplicationMessage()) > 0;
    }

    // 받은 신청 조회
    public List<ApplyResDto> findReceivedApply (Long userNo) {
        @Language("SQL")
        String sql = """
        SELECT MA.application_no, MA.mate_no, MA.applicant_user_no,
               MA.application_message, MA.applied_at, MA.status,
               UP.region, UP.profile_image_url AS image_url,
               M.sport_type, M.mate_type,
               U.name AS mate_name
        FROM MATE_APPLICATION MA
        INNER JOIN MATE M ON MA.mate_no = M.mate_no
        INNER JOIN USERS U ON U.user_no = M.user_no
        INNER JOIN USER_PROFILE UP ON U.user_no = UP.user_no
        WHERE U.user_no = ? AND MA.status = 'PENDING'
        ORDER BY MA.applied_at DESC
        """;

        return jdbc.query(sql, new ApplyRowMapper(), userNo);
    }

    // 보낸 신청 조회
    public List<ApplyResDto> findSentApply(Long userNo) {
        @Language("SQL")
        String sql = """
        SELECT MA.application_no, MA.mate_no, MA.applicant_user_no,
               MA.application_message, MA.applied_at, MA.status,
               M.region, M.image_url, M.sport_type, M.mate_type, M.mate_name
        FROM MATE_APPLICATION MA
        INNER JOIN MATE M ON MA.mate_no = M.mate_no
        INNER JOIN USERS U ON MA.applicant_user_no = U.user_no
        WHERE U.user_no = ? AND MA.status = 'PENDING'
        ORDER BY MA.applied_at DESC
        """;

        return jdbc.query(sql, new ApplyRowMapper(), userNo);
    }

    static class ApplyRowMapper implements RowMapper<ApplyResDto> {

        @Override
        public ApplyResDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ApplyResDto(
                    rs.getLong("application_no"),
                    rs.getLong("mate_no"),
                    rs.getLong("applicant_user_no"),
                    rs.getTimestamp("applied_at").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getString("application_message"),
                    rs.getString("mate_name"),
                    rs.getString("image_url"),
                    rs.getString("sport_type"),
                    rs.getString("mate_type"),
                    rs.getString("region")
            );
        }
    }
}
