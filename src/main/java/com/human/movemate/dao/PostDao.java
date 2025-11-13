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

    public List<Post> get() {
        @Language("SQL")
        String sql = "SELECT * FROM POST";
        return jdbc.query(sql, new PostRowMapper());
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
                    rs.getString("image_url")
            );
        }
    }
}
