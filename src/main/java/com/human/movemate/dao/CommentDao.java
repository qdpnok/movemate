package com.human.movemate.dao;

import com.human.movemate.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sound.midi.VoiceStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDao {
    private final JdbcTemplate jdbc;

    public List<CommentDto> findByPostId(Long postId) {
        @Language("SQL")
        String sql = "SELECT " +
                "  c.comment_no, c.post_id, c.user_id, c.content, c.created_at, " +
                "  u.user_id AS author_id, up.profile_image_url " +
                "FROM COMMENTS c " +
                "JOIN USERS u ON c.user_id = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE c.post_id = ? " +
                "ORDER BY c.created_at ASC"; // 오래된 순으로 정렬

        return jdbc.query(sql, new CommentDtoRowMapper(), postId);
    }

    // 댓글 저장
    public void commentSave(Long postId, Long userNo, String content) {
        @Language("SQL")
        String sql = "INSERT INTO COMMENTS (post_id, user_id, content) VALUES (?, ?, ?)";
        jdbc.update(sql, postId, userNo, content);
    }

    // 댓글 삭제
    public void commentDeleteById(Long commentId) {
        @Language("SQL")
        String sql = "DELETE FROM COMMENTS WHERE comment_no = ?";
        jdbc.update(sql, commentId);
    }

    // 댓글 수정
    public void commentUpdate(Long commentId, String content) {
        @Language("SQL")
        String sql = "UPDATE COMMENTS SET content = ? WHERE comment_no = ?";
        jdbc.update(sql, content, commentId);
    }

    // 댓글 수정을 위한 댓글 1개 조회 (수정 폼) / comment_no로 찾음
    public CommentDto findById(Long commentId) {
        @Language("SQL")
        String sql = "SELECT " +
                "  c.comment_no, c.post_id, c.user_id, c.content, c.created_at, " +
                "  u.user_id AS author_id, up.profile_image_url " +
                "FROM COMMENTS c " +
                "JOIN USERS u ON c.user_id = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE c.comment_no = ?";
        try {
            return jdbc.queryForObject(sql, new CommentDtoRowMapper(), commentId);
        } catch (EmptyResultDataAccessException e) {
            return null; // 댓글이 없는 경우
        }
    }

    // 댓글 삭제 후 리다이렉트를 위해 comment_no로 post_id 찾는 메소드
    public Long findPostIdByCommentId(Long commentId) {
        @Language("SQL")
        String sql = "SELECT post_id FROM COMMENTS WHERE comment_no = ?";
        try {
            // queryForObject: 결과가 1개인 스칼라 값(Long, String 등)을 가져옴
            return jdbc.queryForObject(sql, Long.class, commentId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    static class CommentDtoRowMapper implements RowMapper<CommentDto> {
        @Override
        public CommentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommentDto(
                    rs.getLong("comment_no"),
                    rs.getLong("post_id"),
                    rs.getLong("user_id"),
                    rs.getString("author_id"),
                    rs.getString("profile_image_url"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
        }
    }


}
