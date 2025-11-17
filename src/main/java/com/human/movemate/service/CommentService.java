package com.human.movemate.service;
import com.human.movemate.dto.CommentDto;
import java.util.List;

public interface CommentService {
    List<CommentDto> findByPostId(Long postId);
    // 댓글 저장 / 삭제 추가해야됨
}
