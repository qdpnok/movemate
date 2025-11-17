package com.human.movemate.dao;

import com.human.movemate.dto.CommentDto;
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
public class CommentDao {
    private final JdbcTemplate jdbc;

    public List<CommentDto> findByPostId(Long postId) {
        @Language("SQL")
        String sql = "SELECT " +
                "  c.comment_no, c.user_id, c.content, c.created_at, " +
                "  u.user_id AS author_id, up.profile_image_url " +
                "FROM COMMENTS c " +
                "JOIN USERS u ON c.user_id = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE c.post_id = ? " +
                "ORDER BY c.created_at ASC"; // 오래된 순으로 정렬

        return jdbc.query(sql, new CommentDtoRowMapper(), postId);
    }

    static class CommentDtoRowMapper implements RowMapper<CommentDto> {
        @Override
        public CommentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommentDto(
                    rs.getLong("comment_no"),
                    rs.getLong("user_id"),
                    rs.getString("author_id"),
                    rs.getString("profile_image_url"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
        }
    }


}
