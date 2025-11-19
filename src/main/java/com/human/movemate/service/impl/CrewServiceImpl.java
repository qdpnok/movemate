package com.human.movemate.service.impl;

import com.human.movemate.dao.CrewDao;
import com.human.movemate.model.Crew;
import com.human.movemate.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {
    private final CrewDao crewDao;

    @Override
    public List<Crew> getCrewList() {
        return crewDao.findAllCrews();
    }
}

