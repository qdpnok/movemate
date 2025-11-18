package com.human.movemate.dao;

import com.human.movemate.model.Crew;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CrewDaoImpl implements CrewDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Crew> findAllCrews() {

        String sql = "SELECT id, name, area, member_count, max_count FROM crew";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Crew(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("area"),
                rs.getInt("member_count"),
                rs.getInt("max_count")
        ));
    }
}
