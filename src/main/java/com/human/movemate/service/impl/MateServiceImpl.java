package com.human.movemate.service.impl;

import com.human.movemate.model.Crew;
import com.human.movemate.model.Mate;
import com.human.movemate.service.MateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateServiceImpl implements MateService {
    @Override
    public List<Mate> getMateList() {
        return List.of();
    }

    @Override
    public List<Crew> getCrewList() {
        return List.of();
    }
}
