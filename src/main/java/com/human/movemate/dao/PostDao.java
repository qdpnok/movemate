package com.human.movemate.dao;

import com.human.movemate.model.Post;
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
public class PostDao {
    private final JdbcTemplate jdbc;

    public List<Post> get(Long boardTypeNo) {
        @Language("SQL")
        String sql = "SELECT " +
                "  p.post_no, p.board_type_no, p.user_no, " +
                "  p.title, p.content, p.created_at, p.image_url, " +
                "  u.user_id " + // USERS 테이블의 user_id
                "FROM POST p " +
                "JOIN USERS u ON p.user_no = u.user_no " + // user_no로 JOIN
                "WHERE p.board_type_no = ?"; // board_type_no로 필터링
        return jdbc.query(sql, new PostRowMapper(), boardTypeNo);
    }

    public void save(Post post) {
        @Language("SQL")
        String sql = "INSERT INTO POST (board_type_no, user_no, title, content) VALUES (?,?,?,?)";
        jdbc.update(sql,
                post.getBoardTypeNo(),
                post.getUserNo(),
                post.getTitle(),
                post.getContent());
    }

    static class PostRowMapper implements RowMapper<Post> {

        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Post(
                    rs.getLong("post_no"),
                    rs.getLong("board_type_no"),
                    rs.getLong("user_no"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("image_url"),
                    rs.getString("user_id")
            );
        }
    }
}
