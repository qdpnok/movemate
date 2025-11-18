package com.human.movemate.service;

import com.human.movemate.model.Crew;
import com.human.movemate.model.Mate;

import java.util.List;

public interface MateService {
    List<Mate> getMateList();
    List<Crew> getCrewList();
}
