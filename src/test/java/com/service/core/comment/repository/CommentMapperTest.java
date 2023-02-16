package com.service.core.comment.repository;

import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentLinkDto;
import com.service.core.comment.dto.CommentSearchDto;
import com.service.core.comment.paging.CommentPagination;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.comment.repository.mapper.CommentMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    @Transactional(readOnly = true)
    @Disabled
    void commentLinkDtoTest() {
        List<CommentLinkDto> commentLinkDtos = commentMapper.findCommentLinkDto("herb16987");
        Assertions.assertNotNull(commentLinkDtos);
    }

    @Test
    @Transactional(readOnly = true)
    @Disabled
    void commentMapperTest() {
        CommentSearchPagingDto commentSearchPagingDto = new CommentSearchPagingDto();
        commentSearchPagingDto.setRecordSize(10);
        CommentPagination commentPagination = new CommentPagination(1,commentSearchPagingDto);
        commentSearchPagingDto.setCommentPagination(commentPagination);
        List<CommentDto> commentDtoList = commentMapper.findCommentDtoListByPaging(CommentSearchDto.from(26L,commentSearchPagingDto));
        Assertions.assertNotNull(commentDtoList);
    }
}
