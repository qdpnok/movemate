package com.human.movemate.service.impl;

import com.human.movemate.dao.CommentDao;
import com.human.movemate.dto.CommentDto;
import com.human.movemate.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;

    @Override
    public List<CommentDto> findByPostId(Long postId) {
        return commentDao.findByPostId(postId);
    }
}
