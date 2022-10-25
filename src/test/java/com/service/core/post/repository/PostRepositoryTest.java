package com.service.core.post.repository;

import com.service.core.post.domain.Post;
import com.service.core.post.repository.mapper.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    @Transactional(readOnly = true)
    public void findPostPagingTest() {
        List<Post> postList = postRepository.findPostByCategoryId(4L, PageRequest.of(1, 2)).getContent();
        System.out.println(postList.size());
    }
}
