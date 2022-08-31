package com.service.core.category.service;

import com.service.core.category.dto.CategoryDto;
import com.service.core.post.dto.PostTotalDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Test
    @Disabled
    void findCategoriesService() {
        try {
            CategoryDto categoryDto = categoryService.findCategoryDto("nebi25");
            Assertions.assertNotNull(categoryDto);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @Disabled
    void findTotalPostTest() {
        try {
            PostTotalDto postTotalDto = categoryService.findPostByCategoryId(100L);
            Assertions.assertNotNull(postTotalDto);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void findTotalPostByBlogId() {
        try{
            PostTotalDto postTotalDto = categoryService.findPostByBlogId(1L);
            Assertions.assertNotNull(postTotalDto);
        }catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
