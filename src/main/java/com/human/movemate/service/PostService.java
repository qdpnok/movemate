package com.human.movemate.service;
import com.human.movemate.dto.PostFormDto;
import com.human.movemate.model.Post;
import java.util.List;

public interface PostService {
    List<Post> find(Long boardTypeNo); // 게시글 목록
    void save(PostFormDto postFormDto, Long userNo); // 저장
    Post findById(Long postId); // 게시글 상세 조회
}
