package com.service.core.category.service;

import com.service.core.category.dto.CategoryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Test
    void findCategoriesService() {
        try{
            CategoryDto categoryDto = categoryService.findCategoryDto("nebi25");
            Assertions.assertNotNull(categoryDto);
        }catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
