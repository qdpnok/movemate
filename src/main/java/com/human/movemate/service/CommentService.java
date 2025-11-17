package com.human.movemate.service;
import com.human.movemate.dto.CommentDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    List<CommentDto> findByPostId(Long postId);

    // 저장
    void save(Long postId, Long userId, String content);

    // 삭제
    @Transactional
    Long deleteById(Long commentId);

    // 수정
    void update(Long commentId, String content);

    // 댓글 수정을 위한 댓글 1개 조회 (수정 폼)
    CommentDto findById(Long commentId);

}
