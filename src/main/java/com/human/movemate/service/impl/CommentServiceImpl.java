package com.human.movemate.service.impl;

import com.human.movemate.dao.CommentDao;
import com.human.movemate.dto.CommentDto;
import com.human.movemate.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;

    // 조회
    @Override
    public List<CommentDto> findByPostId(Long postId) {
        return commentDao.findByPostId(postId);
    }

    // 저장
    @Override
    public void save(Long postId, Long userNo, String content) {
        log.info("댓글 저장 시도: postId={}, userNo={}, content={}", postId, userNo, content);
        commentDao.commentSave(postId, userNo, content);
        log.info("댓글 저장 성공");
    }

    // 삭제
    @Transactional
    @Override
    public Long deleteById(Long commentId) {
        // 리다이렉트를 위해 postId 조회 (DAO에 findPostIdByCommentId 메서드 필요)
        Long postId = commentDao.findPostIdByCommentId(commentId);
        if (postId == null) {
            throw new RuntimeException("삭제할 댓글이 없습니다.");
        }
        commentDao.commentDeleteById(commentId);
        // 컨트롤러에 postId 반환
        return postId;
    }

    // 수정
    @Override
    public void update(Long commentId, String content) {
        commentDao.commentUpdate(commentId, content);
    }

    @Override
    public CommentDto findById(Long commentId) {
        return commentDao.findById(commentId);
    }
}
