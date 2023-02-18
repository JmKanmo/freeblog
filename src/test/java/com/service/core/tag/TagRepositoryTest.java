package com.service.core.tag;


import com.service.core.tag.dto.TagDto;
import com.service.core.tag.repository.mapper.TagMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TagRepositoryTest {
    @Autowired
    private TagMapper tagMapper;

    @Test
    @Disabled
    public void recentTagTest() {
        List<TagDto> tagDtoList = tagMapper.findTagDtoList(9L);
        Assertions.assertNotNull(tagDtoList);
    }
}
