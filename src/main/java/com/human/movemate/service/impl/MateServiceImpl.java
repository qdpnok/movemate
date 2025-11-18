package com.human.movemate.service.impl;

import com.human.movemate.dao.MateDao;
import com.human.movemate.model.AddMate;
import com.human.movemate.service.MateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateServiceImpl implements MateService {

    private final MateDao mateDao; // 창고지기(DAO)를 부름

    @Override
    public List<AddMate> findAllMates() {
        // DAO에게 모든 메이트 목록을 가져오라고 시킴
        return mateDao.findAll();
    }
}
