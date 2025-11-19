package com.human.movemate.dao;

import com.human.movemate.model.Post;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostDao {
    private final JdbcTemplate jdbc;

    // 게시글 조회
    public List<Post> get(Long boardTypeNo) {
        @Language("SQL")
        String sql = "SELECT " +
                "  p.post_no, p.board_type_no, p.user_no, " +
                "  p.title, p.content, p.created_at, p.image_url, " +
                "  u.user_id, " + // USERS 테이블의 user_id
                "  up.profile_image_url " +
                "FROM POST p " +
                "JOIN USERS u ON p.user_no = u.user_no " + // user_no로 JOIN
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE p.board_type_no = ? " + // board_type_no로 필터링
                "ORDER BY p.created_at DESC"; //최신순 정렬
        return jdbc.query(sql, new PostRowMapper(), boardTypeNo);
    }

    // 페이지네이션 조회
    public List<Post> getPostsByPage(Long boardTypeNo, int offset, int size) {
        int startRow = offset + 1;
        int endRow = offset + size;

        @Language("SQL")
        String sql = """
        SELECT
            post_no, board_type_no, user_no,
            title, content, created_at, image_url,
            user_id, profile_image_url
        FROM (
            SELECT
                p.post_no, p.board_type_no, p.user_no,
                p.title, p.content, p.created_at, p.image_url,
                u.user_id,
                up.profile_image_url,
                ROW_NUMBER() OVER (ORDER BY p.created_at DESC) AS rn
            FROM POST p
            JOIN USERS u ON p.user_no = u.user_no
            LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no
            WHERE p.board_type_no = ?
        )
        WHERE rn BETWEEN ? AND ? -- 범위로 필터링
        """;

        return jdbc.query(sql, new PostRowMapper(), boardTypeNo, startRow, endRow);
    }

    public int countTotalPosts(Long boardTypeNo) {
        String sql = "SELECT COUNT(*) FROM POST WHERE board_type_no = ?";
        return jdbc.queryForObject(sql, Integer.class, boardTypeNo);
    }

    public Post getById(Long postId) {
        @Language("SQL")
        String sql = "SELECT " +
                "  p.post_no, p.board_type_no, p.user_no, " +
                "  p.title, p.content, p.created_at, p.image_url, " +
                "  u.user_id, up.profile_image_url " +
                "FROM POST p " +
                "JOIN USERS u ON p.user_no = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " + // USER_PROFILE 조인
                "WHERE p.post_no = ? "; // post_no로 조회
        try {
            // queryForObject : 결과가 1개가 아니면(없거나 많으면) 예외를 던짐
            return jdbc.queryForObject(sql, new PostRowMapper(), postId);
        } catch (EmptyResultDataAccessException e) {
            return null; // 게시글 없으면 null 반환
        }
    }

    // 내가 쓴 글만 조회하기
    public List<Post> findByUserNo(Long userNo, Long boardTypeNo) {
        @Language("SQL")
        String sql = "SELECT " +
                "  p.post_no, p.board_type_no, p.user_no, " +
                "  p.title, p.content, p.created_at, p.image_url, " +
                "  u.user_id, " +
                "  up.profile_image_url " +
                "FROM POST p " +
                "JOIN USERS u ON p.user_no = u.user_no " +
                "LEFT JOIN USER_PROFILE up ON u.user_no = up.user_no " +
                "WHERE p.user_no = ? AND p.board_type_no = ? " + // user_no, 게시판 종류로 필터링
                "ORDER BY p.created_at DESC"; // 최신순 정렬
        return jdbc.query(sql, new PostRowMapper(), userNo, boardTypeNo);
    }

    // 게시글 저장
    public void save(Post post) {
        @Language("SQL")
        String sql = "INSERT INTO POST (board_type_no, user_no, title, content, image_url) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbc.update(sql,
                post.getBoardTypeNo(),
                post.getUserNo(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl()
        );
    }

    // 게시글 수정
    public void update(Post post) {
        String sql;
        if (post.getImageUrl() != null) {
            // 이미지를 새로 첨부한 경우
            sql = "UPDATE POST SET board_type_no = ?, title = ?, content = ?, image_url = ? " +
                    "WHERE post_no = ?";
            jdbc.update(sql,
                    post.getBoardTypeNo(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getPostNo());
        } else {
            // 이미지를 수정하지 않은 경우 (기존 이미지 유지)
            sql = "UPDATE POST SET board_type_no = ?, title = ?, content = ? " +
                    "WHERE post_no = ?";
            jdbc.update(sql,
                    post.getBoardTypeNo(),
                    post.getTitle(),
                    post.getContent(),
                    post.getPostNo());
        }
    }

    // 게시글 삭제
    public void deleteById(Long postId) {
        @Language("SQL")
        String sql = "DELETE FROM POST WHERE post_no = ?";
        jdbc.update(sql, postId);
    }

    // RowMapper
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
                    rs.getString("user_id"),
                    rs.getString("profile_image_url")
            );
        }
    }
}
