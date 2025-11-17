package com.human.movemate.service.impl;
import com.human.movemate.dao.PostDao;
import com.human.movemate.dto.PostFormDto;
import com.human.movemate.model.Post;
import com.human.movemate.service.FileStorageService;
import com.human.movemate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
            storedFileName = fileStorageService.storeFile(file);
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
}
