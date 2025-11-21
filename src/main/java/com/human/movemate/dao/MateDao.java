package com.human.movemate.dao;

import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.dto.MatchingDto;
import com.human.movemate.model.AddMate;
import com.human.movemate.model.Post;
import com.human.movemate.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository                 // Spring Container에 Bean 객체 등록, 싱글톤 객체가 됨
@RequiredArgsConstructor    // 생성자를 통한 의존성 주입을 하기 위해 사용
@Slf4j                      // log 메세지 출력 지원하기 위한 lombok 기능
public class MateDao {
    private final JdbcTemplate jdbc;    // JdbcTemplate을 의존성 주입 받기

    /**
     * 내가 보낸 신청 목록 조회 (로그인 유저 = 신청자)
     * - 상대방 정보는 모집글 작성자(MATE.user_no)의 프로필을 사용
     */
    public List<MatchingDto> findSentMatchings(Long userNo) {
        String sql = "SELECT " +
                "T1.MATCH_NO, " +
                "T4.USER_NO AS userNo, T2.NAME AS name, T3.REGION AS region, " + // ★ 상대방: 모집글 작성자(T4.USER_NO)
                "T3.PROFILE_IMAGE_URL AS profileImageUrl, T1.STATUS AS MATCH_STATUS, " +
                "T4.MATE_NAME AS postTitle, T4.MATE_TYPE AS postType, " +
                "T4.SPORT_TYPE AS sportType " +
                "FROM MATCHING T1 " +
                "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO " +             // MATCHING과 MATE 연결
                "JOIN USERS T2 ON T4.USER_NO = T2.USER_NO " +            // MATE 작성자(상대방) 정보 조인
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T1.APPLICANT_USER_NO = ? " + // 로그인 유저가 신청자
                "AND (T1.STATUS = '대기' OR T1.STATUS = 'PENDING')"; // '대기' 상태인 것만 조회하도록 추가, 수락/거절 버튼 누른 대상자 안보이게 함

        // 쿼리 결과 매핑 (MatchingDto에 맞게 수정)
        return jdbc.query(sql, new Object[]{userNo}, (rs, rowNum) -> {
            return new MatchingDto(
                    rs.getLong("MATCH_NO"),
                    rs.getLong("userNo"),           // 모집글 작성자의 user_no
                    rs.getString("name"),
                    rs.getString("region"),
                    rs.getString("profileImageUrl"),
                    rs.getString("MATCH_STATUS"),
                    rs.getString("postTitle"),
                    rs.getString("postType"),
                    rs.getString("sportType")
            );
        });
    }

    // -------------------------------------------------------------------------

    /**
     * 내가 받은 신청 목록 조회 (로그인 유저 = 모집글 작성자)
     * - 상대방 정보는 신청자(MATCHING.applicant_user_no)의 프로필을 사용
     */
    public List<MatchingDto> findReceivedMatchings(Long userNo) {
        String sql = "SELECT " +
                "T1.MATCH_NO, " +
                "T1.APPLICANT_USER_NO AS userNo, T2.NAME AS name, T3.REGION AS region, " + // ★ 상대방: 신청자(T1.APPLICANT_USER_NO)
                "T3.PROFILE_IMAGE_URL AS profileImageUrl, T1.STATUS AS MATCH_STATUS, " +
                "T4.MATE_NAME AS postTitle, T4.MATE_TYPE AS postType, " +
                "T4.SPORT_TYPE AS sportType " +
                "FROM MATCHING T1 " +
                "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO " +             // MATCHING과 MATE 연결
                "JOIN USERS T2 ON T1.APPLICANT_USER_NO = T2.USER_NO " +  // 신청자(상대방) 정보 조인
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T4.USER_NO = ? " + // 로그인 유저가 모집글 작성자
                "AND (T1.STATUS = '대기' OR T1.STATUS = 'PENDING')"; // '대기' 상태인 것만 조회하도록 추가, 수락/거절 버튼 누른 대상자 안보이게 함

        // 쿼리 결과 매핑 (MatchingDto에 맞게 수정)
        return jdbc.query(sql, new Object[]{userNo}, (rs, rowNum) -> {
            return new MatchingDto(
                    rs.getLong("MATCH_NO"),
                    rs.getLong("userNo"),           // 신청자의 user_no
                    rs.getString("name"),
                    rs.getString("region"),
                    rs.getString("profileImageUrl"),
                    rs.getString("MATCH_STATUS"),
                    rs.getString("postTitle"),
                    rs.getString("postType"),
                    rs.getString("sportType")
            );
        });


    }

    // 상위 게시글 3개만 조회하기 - 크루
    public List<AddMate> findTop3Crew() {
        @Language("SQL")
        String sql = """
        SELECT *
        FROM (SELECT *
              FROM MATE
              WHERE mate_type = 'CREW' AND sport_type = '러닝'
              ORDER BY current_members DESC, created_at ASC)
        WHERE rownum <= 3
        """;

        return jdbc.query(sql, mateRowMapper);
    }

    public List<AddMate> findTop3Solo() {
        @Language("SQL")
        String sql = """
        SELECT *
        FROM (SELECT *
              FROM MATE
              WHERE mate_type = 'SOLO'
              ORDER BY created_at DESC)
        WHERE rownum <= 3
        """;

        return jdbc.query(sql, mateRowMapper);
    }

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
            mate.setCurrentMembers(rs.getInt("current_members"));
            mate.setDescription(rs.getString("description"));
            mate.setImageUrl(rs.getString("image_url"));
            mate.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return mate;
        }
    };
    // MATE 테이블의 모든 데이터를 조회하는 메서드
    public List<AddMate> findAll () {
        String sql = "SELECT * FROM MATE ORDER BY created_at DESC"; // 최신순 정렬
        return jdbc.query(sql, mateRowMapper);
    }


    public MatchingDetailDto findMatchingDetail(Long matchNo) {
        String sql = "SELECT "
                + "    T1.MATCH_NO, T1.APPLICANT_USER_NO, T1.STATUS AS MATCH_STATUS, "
                + "    T4.MATE_NO, T4.USER_NO AS MATE_WRITER_NO, T4.MATE_TYPE, T4.MATE_NAME, "
                + "    T2.NAME AS APPLICANT_NAME, T3.REGION AS APPLICANT_REGION, T3.PROFILE_IMAGE_URL "
                + "FROM MATCHING T1 "
                + "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO "
                + "JOIN USERS T2 ON T1.APPLICANT_USER_NO = T2.USER_NO "
                + "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO "
                + "WHERE T1.MATCH_NO = ?"; // 쿼리 끝에 ?가 바인딩될 위치입니다.

        try {
            // queryForObject를 사용하여 단일 MatchingDetailDto 객체를 반환합니다.
            return jdbc.queryForObject(sql, new MatchingDetailRowMapper(), matchNo);
        } catch (EmptyResultDataAccessException e) {
            // 매칭 번호에 해당하는 데이터가 없을 경우 NULL 또는 예외 처리
            return null;
        }
    }

    private static class MatchingDetailRowMapper implements org.springframework.jdbc.core.RowMapper<MatchingDetailDto> {
        @Override
        public MatchingDetailDto mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {

            MatchingDetailDto dto = new MatchingDetailDto();

            // SQL 쿼리의 대문자 별칭을 사용하여 매핑합니다.
            dto.setMatchNo(rs.getLong("MATCH_NO"));
            dto.setMateNo(rs.getLong("MATE_NO"));
            dto.setApplicantNo(rs.getLong("APPLICANT_USER_NO"));   // DTO 필드명 applicantNo 사용

            dto.setMateWriterNo(rs.getLong("MATE_WRITER_NO"));
            dto.setMatchStatus(rs.getString("MATCH_STATUS"));

            dto.setMateType(rs.getString("MATE_TYPE"));
            dto.setMateName(rs.getString("MATE_NAME"));

            dto.setApplicantName(rs.getString("APPLICANT_NAME"));
            dto.setApplicantRegion(rs.getString("APPLICANT_REGION"));
            dto.setProfileImageUrl(rs.getString("PROFILE_IMAGE_URL"));



            return dto;
        }
    }

    // 매칭 신청 상태 업데이트(ACCEPT, REJECT)
    public void updateStatus(Long matchNo, String status) {
        @Language("SQL")
        // MATCHING 테이블의 STATUS를 변경합니다.
        String sql = "UPDATE MATCHING SET STATUS = ? WHERE MATCH_NO = ?";
        jdbc.update(sql, status, matchNo);
    }

    // 매칭 번호를 통해 모집글 번호(mateNo)를 조회
    public Long findMateNoByMatchNo(Long matchNo) throws EmptyResultDataAccessException {
        @Language("SQL")
        // MATCHING 테이블에서 MATE_NO를 조회합니다.
        String sql = "SELECT MATE_NO FROM MATCHING WHERE MATCH_NO = ?";
        try {
            // 단일 값(Long)을 조회합니다.
            return jdbc.queryForObject(sql, Long.class, matchNo);
        } catch (EmptyResultDataAccessException e) {
            log.error("MatchNo {}에 해당하는 MateNo를 찾을 수 없습니다.", matchNo);
            throw e;
        }
    }

//    로그인 유저가 신청자인 경우
    public List<MatchingDto> findAcceptedMatchingsByApplicant(Long userNo) {
        @Language("SQL")
        String sql = "SELECT " +
                "T1.MATCH_NO, " +
                "T4.USER_NO AS userNo, T2.NAME AS name, T3.REGION AS region, " + // 상대방: 모집글 작성자
                "T3.PROFILE_IMAGE_URL AS profileImageUrl, T1.STATUS AS MATCH_STATUS, " +
                "T4.MATE_NAME AS postTitle, T4.MATE_TYPE AS postType, " +
                "T4.SPORT_TYPE AS sportType " +
                "FROM MATCHING T1 " +
                "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO " +
                "JOIN USERS T2 ON T4.USER_NO = T2.USER_NO " +
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T1.APPLICANT_USER_NO = ?" +
                "AND T1.STATUS = 'ACCEPT'"; // ★ 수락 완료 조건

        // jdbc.query 실행 및 Mapping 로직 추가
        return jdbc.query(sql, new Object[]{userNo}, (rs, rowNum) -> {
            return new MatchingDto(
                    rs.getLong("MATCH_NO"),
                    rs.getLong("userNo"), // 모집글 작성자의 userNo
                    rs.getString("name"),
                    rs.getString("region"),
                    rs.getString("profileImageUrl"),
                    rs.getString("MATCH_STATUS"),
                    rs.getString("postTitle"),
                    rs.getString("postType"),
                    rs.getString("sportType")
            );
        });
    }

    // 로그인 유저가 모집글 작성자일 때
    public List<MatchingDto> findAcceptedMatchingsByWriter(Long userNo) {
        @Language("SQL")
        String sql = "SELECT " +
                "T1.MATCH_NO, " +
                "T1.APPLICANT_USER_NO AS userNo, T2.NAME AS name, T3.REGION AS region, " + // 상대방: 신청자
                "T3.PROFILE_IMAGE_URL AS profileImageUrl, T1.STATUS AS MATCH_STATUS, " +
                "T4.MATE_NAME AS postTitle, T4.MATE_TYPE AS postType, " +
                "T4.SPORT_TYPE AS sportType " +
                "FROM MATCHING T1 " +
                "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO " +
                "JOIN USERS T2 ON T1.APPLICANT_USER_NO = T2.USER_NO " +
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T4.USER_NO = ?" +
                "AND T1.STATUS = 'ACCEPT'"; // ★ 수락 완료 조건

        // jdbc.query 실행 및 Mapping 로직 추가
        return jdbc.query(sql, new Object[]{userNo}, (rs, rowNum) -> {
            return new MatchingDto(
                    rs.getLong("MATCH_NO"),
                    rs.getLong("userNo"), // 신청자의 userNo
                    rs.getString("name"),
                    rs.getString("region"),
                    rs.getString("profileImageUrl"),
                    rs.getString("MATCH_STATUS"),
                    rs.getString("postTitle"),
                    rs.getString("postType"),
                    rs.getString("sportType")
            );
        });
    }

}


        // (추후 상세보기를 위한 메서드도 추가할 수 있어요)
        // public AddMate findById(Long mateNo) { ... }

