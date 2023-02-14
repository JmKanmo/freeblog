package com.service.core.category.repository;

import com.service.core.category.domain.CategoryMapperDto;
import com.service.core.category.repository.mapper.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @Transactional(readOnly = true)
    @Disabled
    void findCategoriesRepo() {
        try {
            List<CategoryMapperDto> categoryList = categoryMapper.findCategoriesByUserId("akxk25");
            Assertions.assertNotNull(categoryList);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}