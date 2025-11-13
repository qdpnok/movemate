package com.human.movemate.service.impl;

import com.human.movemate.dao.PostDao;
import com.human.movemate.model.Post;
import com.human.movemate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostDao postDao;


    @Override
    public List<Post> find(Long boardTypeNo) {
        return postDao.get(boardTypeNo);
    }
}
