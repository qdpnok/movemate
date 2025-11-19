package com.human.movemate.dao;

import com.human.movemate.dto.MatchingDetailDto;
import com.human.movemate.dto.MatchingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
                "T4.MATE_NAME AS postTitle, T4.MATE_TYPE AS postType " +
                "FROM MATCHING T1 " +
                "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO " +             // MATCHING과 MATE 연결
                "JOIN USERS T2 ON T4.USER_NO = T2.USER_NO " +            // MATE 작성자(상대방) 정보 조인
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T1.APPLICANT_USER_NO = ?"; // 로그인 유저가 신청자

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
                    rs.getString("postType")
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
                "T4.MATE_NAME AS postTitle, T4.MATE_TYPE AS postType " +
                "FROM MATCHING T1 " +
                "JOIN MATE T4 ON T1.MATE_NO = T4.MATE_NO " +             // MATCHING과 MATE 연결
                "JOIN USERS T2 ON T1.APPLICANT_USER_NO = T2.USER_NO " +  // 신청자(상대방) 정보 조인
                "JOIN USER_PROFILE T3 ON T2.USER_NO = T3.USER_NO " +
                "WHERE T4.USER_NO = ?"; // 로그인 유저가 모집글 작성자

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
                    rs.getString("postType")
            );
        });
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

}
