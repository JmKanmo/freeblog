package com.service.core.category.repository;

import com.service.core.category.dto.CategoryBasicMapperDto;
import com.service.core.category.dto.CategoryMapperDto;
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

    @Test
    @Transactional(readOnly = true)
    void categoryBasicDtoMapperTest() {
        try {
            CategoryBasicMapperDto categoryBasicMapperDto = categoryMapper.findCategoryBasicMapperDtoByCategoryId(4L);
            Assertions.assertNotNull(categoryBasicMapperDto);
            categoryBasicMapperDto = categoryMapper.findCategoryBasicMapperDtoByCategoryIdAndEmail(87L, "nebi25@naver.com");
            Assertions.assertNotNull(categoryBasicMapperDto);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}