package com.human.movemate.service;
import com.human.movemate.dto.PostFormDto;
import com.human.movemate.dto.PagedResDto;
import com.human.movemate.model.Post;
import java.util.List;

public interface PostService {
    List<Post> find(Long boardTypeNo); // 게시글 목록

    PagedResDto<Post> getPagination(Long boardTypeNo, int page, int size);

    void save(PostFormDto postFormDto, Long userNo); // 저장
    Post findById(Long postId); // 게시글 상세 조회
    void update(Long postId, PostFormDto postFormDto); // 게시글 수정
    void deleteById(Long postId); // 게시글 삭제
    List<Post> findMyPosts(Long userNo, Long boardTypeNo); // 내가 쓴 글만 조회
}
