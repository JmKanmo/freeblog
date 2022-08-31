package com.service.core.category.dto;

import com.service.core.category.domain.CategoryMapperDto;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentCategoryDto {
    private Long id;
    private Long parentId;
    private String name;
    private Long seq;
    private String link;
    private Long postCount;

    public static ParentCategoryDto fromEntity(CategoryMapperDto categoryMapperDto, String userId) {
        if (categoryMapperDto == null) {
            return ParentCategoryDto.builder()
                    .id(Long.MAX_VALUE)
                    .parentId(Long.MAX_VALUE)
                    .name(ConstUtil.UNDEFINED)
                    .seq(Long.MAX_VALUE)
                    .link(ConstUtil.UNDEFINED)
                    .postCount(Long.MAX_VALUE)
                    .build();
        } else {
            return ParentCategoryDto.builder()
                    .id(categoryMapperDto.getCategoryId())
                    .parentId(categoryMapperDto.getParentId())
                    .name(BlogUtil.ofNull(categoryMapperDto.getName()))
                    .seq(categoryMapperDto.getSeq())
                    .link(String.format("/category/%d", categoryMapperDto.getCategoryId()))
                    .postCount(categoryMapperDto.getPostCount())
                    .build();
        }
    }
}
