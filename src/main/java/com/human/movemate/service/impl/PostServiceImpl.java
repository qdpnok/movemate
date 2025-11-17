package com.human.movemate.service.impl;
import com.human.movemate.dao.PostDao;
import com.human.movemate.dto.PostFormDto;
import com.human.movemate.model.Post;
import com.human.movemate.service.FileStorageService;
import com.human.movemate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final FileStorageService fileStorageService;

    @Override
    public List<Post> find(Long boardTypeNo) {
        return postDao.get(boardTypeNo);
    }

    @Override
    public void save(PostFormDto postFormDto, Long userNo) {

        // 파일 저장 로직
        MultipartFile file = postFormDto.getPostImage();
        String storedFileName = null; // DB에 저장할 파일명
        if (file != null && !file.isEmpty()) {
            // FileStorageService를 호출하여 파일을 디스크에 저장
            storedFileName = fileStorageService.storeFile(file, "posts", null);
        }

        // DTO를 Post 모델(Entity)로 변환
        Post post = new Post();
        post.setBoardTypeNo(postFormDto.getBoardTypeNo());
        post.setTitle(postFormDto.getTitle());
        post.setContent(postFormDto.getContent());
        // 컨트롤러에서 받은 작성자 userNo 설정
        post.setUserNo(userNo);
        // 저장된 파일명을 Post 객체에 설정
        post.setImageUrl(storedFileName);
        // DAO를 호출하여 DB에 저장
        postDao.save(post);
    }

    // 게시글 상세 조회
    @Override
    public Post findById(Long postId) {
        return postDao.getById(postId);
    }

    @Override
    public void update(Long postId, PostFormDto postFormDto) {
        // 기존 게시글 정보 조회 (파일 삭제 로직을 위해)
        Post existingPost = postDao.getById(postId);
        if (existingPost == null) {
            throw new RuntimeException("수정할 게시글이 없습니다.");
        }
        // 새 파일 처리
        MultipartFile file = postFormDto.getPostImage();
        String newImageUrl = null;
        if (file != null && !file.isEmpty()) {
            // 새 파일 저장 (userNo 대신 postId를 사용)
            newImageUrl = fileStorageService.storeFile(file, "posts", postId);
            // 기존 파일이 있었다면 삭제
            if (existingPost.getImageUrl() != null) {
                fileStorageService.deleteIfExists(existingPost.getImageUrl());
            }
        }
        // DTO -> Post 모델로 변환 (DB 업데이트용)
        Post postToUpdate = new Post();
        postToUpdate.setPostNo(postId);
        postToUpdate.setBoardTypeNo(postFormDto.getBoardTypeNo());
        postToUpdate.setTitle(postFormDto.getTitle());
        postToUpdate.setContent(postFormDto.getContent());
        if (newImageUrl != null) {
            postToUpdate.setImageUrl(newImageUrl); // 새 이미지 URL 설정
        }
        // (imageUrl이 null이면 DAO는 기존 이미지를 건드리지 않음)
        // DB 업데이트
        postDao.update(postToUpdate);
    }

    // 게시글 삭제
    @Transactional // DB 삭제와 파일 삭제를 하나로 묶음
    @Override
    public void deleteById(Long postId) {
        // 파일 삭제를 위해 기존 게시글 정보 조회
        Post post = postDao.getById(postId);
        if (post == null) {
            return; // 이미 삭제되었거나 없는 경우
        }

        // 게시글 DB에서 삭제
        postDao.deleteById(postId);

        // 첨부 파일이 있었다면, 로컬 디스크에서도 삭제
        if (post.getImageUrl() != null) {
            fileStorageService.deleteIfExists(post.getImageUrl());
        }

    }
}
