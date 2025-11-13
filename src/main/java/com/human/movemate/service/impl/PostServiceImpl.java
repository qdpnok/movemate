package com.human.movemate.service.impl;

import com.human.movemate.dao.PostDao;
import com.human.movemate.dto.PostFormDto;
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

    @Override
    public void save(PostFormDto postFormDto, Long userNo) {
        // 1. DTO를 Post 모델(Entity)로 변환
        Post post = new Post();
        post.setBoardTypeNo(postFormDto.getBoardTypeNo());
        post.setTitle(postFormDto.getTitle());
        post.setContent(postFormDto.getContent());

        // 2. 컨트롤러에서 받은 작성자 userNo 설정
        post.setUserNo(userNo);

        // (image_url 등 다른 필드는 DB Default 또는 null)

        // 3. DAO를 호출하여 DB에 저장
        postDao.save(post);

    }
}
