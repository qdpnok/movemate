package com.human.movemate.dao;

import com.human.movemate.model.AddMate; // (AddMate 모델 재사용)
import com.human.movemate.dto.MatchingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Repository                 // Spring Container에 Bean 객체 등록, 싱글톤 객체가 됨
@RequiredArgsConstructor    // 생성자를 통한 의존성 주입을 하기 위해 사용
@Slf4j                      // log 메세지 출력 지원하기 위한 lombok 기능
public class MateDao {
    private final JdbcTemplate jdbc;    // JdbcTemplate을 의존성 주입 받기


    // 내가 보낸 신청 목록 (상대방=게시글 작성자 프로필 + 게시판 이름)
    public List<MatchingDto> findSentMatchings(Long userNo) {
        String sql = "SELECT " +
                "T2.USER_NO AS userNo, T2.NAME AS name, T3.REGION AS region, " +
                "T3.PROFILE_IMAGE_URL AS profileImageUrl, T1.STATUS AS MATCH_STATUS, " + // ★ T1.STATUS 별칭 변경
                "T4.TITLE AS postTitle, B.BOARD_NAME AS postType " +
                "FROM MATCHING T1 " +
                "JOIN POST T4 ON T1.POST_NO = T4.POST_NO " +
                "JOIN BOARD_TYPE B ON T4.BOARD_TYPE_NO = B.BOARD_TYPE_NO " + // ★ POST 테이블의 BOARD_TYPE_NO로 수정 (ORA-00904 해결)
                "JOIN USERS T2 ON T4.USER_NO = T2.USER_NO " +
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T1.APPLICANT_USER_NO = ?";

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
        return jdbc.query(sql, new Object[]{userNo}, (rs, rowNum) -> {
            return new MatchingDto(
                    null,
                    rs.getLong("userNo"),
                    rs.getString("name"),
                    rs.getString("region"),
                    rs.getString("profileImageUrl"),
                    rs.getString("MATCH_STATUS"), // ★ rs.getString("status")에서 변경
                    rs.getString("postTitle"),
                    rs.getString("postType")
            );
        });
    }

    // 내가 받은 신청 목록 (상대방=신청자 프로필 + 게시판 이름)
    public List<MatchingDto> findReceivedMatchings(Long userNo) {
        String sql = "SELECT " +
                "T1.APPLICANT_USER_NO AS userNo, T2.NAME AS name, T3.REGION AS region, " +
                "T3.PROFILE_IMAGE_URL AS profileImageUrl, T1.STATUS AS MATCH_STATUS, " + // ★ T1.STATUS 별칭 변경
                "T4.TITLE AS postTitle, B.BOARD_NAME AS postType " +
                "FROM MATCHING T1 " +
                "JOIN POST T4 ON T1.POST_NO = T4.POST_NO " +
                "JOIN BOARD_TYPE B ON T4.BOARD_TYPE_NO = B.BOARD_TYPE_NO " + // ★ POST 테이블의 BOARD_TYPE_NO로 수정 (ORA-00904 해결)
                "JOIN USERS T2 ON T1.APPLICANT_USER_NO = T2.USER_NO " +
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T4.USER_NO = ?";

        return jdbc.query(sql, new Object[]{userNo}, (rs, rowNum) -> {
            return new MatchingDto(
                    null,
                    rs.getLong("userNo"),
                    rs.getString("name"),
                    rs.getString("region"),
                    rs.getString("profileImageUrl"),
                    rs.getString("MATCH_STATUS"), // ★ rs.getString("status")에서 변경
                    rs.getString("postTitle"),
                    rs.getString("postType")
            );
        });
    // MATE 테이블의 모든 데이터를 조회하는 메서드
    public List<AddMate> findAll() {
        String sql = "SELECT * FROM MATE ORDER BY created_at DESC"; // 최신순 정렬
        return jdbc.query(sql, mateRowMapper);
    }

    // (추후 상세보기를 위한 메서드도 추가할 수 있어요)
    // public AddMate findById(Long mateNo) { ... }
}
