package com.human.movemate.service;
import com.human.movemate.dto.PostFormDto;
import com.human.movemate.model.Post;
import java.util.List;

public interface PostService {
    List<Post> find(Long boardTypeNo);
    void save(PostFormDto postFormDto, Long userNo);
}
