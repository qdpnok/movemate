package com.human.movemate.dao;

import com.human.movemate.model.Crew;
import com.human.movemate.model.Mate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MateDao {

    // 메이트 리스트 (천안 주요 동네)
    public List<Mate> getMateList() {
        return List.of(
                new Mate(1, "제니", "두정동"),
                new Mate(2, "로제", "불당동"),
                new Mate(3, "리사", "성정동"),
                new Mate(4, "지수", "쌍용동"),
                new Mate(5, "닝닝", "백석동"),
                new Mate(6, "윈터", "신부동")
        );
    }

    // 러닝 크루 리스트 (천안 동네 기반)
    public List<Crew> getCrewList() {
        return List.of(
                new Crew(1, "천안러너스", "쌍용동", 5, 20),
                new Crew(2, "불당크루", "불당동", 7, 20),
                new Crew(3, "두정러닝팀", "두정동", 4, 20),
                new Crew(4, "성정마라톤회", "성정동", 6, 20),
                new Crew(5, "백석러닝크루", "백석동", 8, 20),
                new Crew(6, "신부러닝클럽", "신부동", 5, 20)
        );
    }
}

