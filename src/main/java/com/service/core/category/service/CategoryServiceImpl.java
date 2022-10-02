package com.service.core.category.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryDto;
import com.service.core.category.repository.CategoryRepository;
import com.service.core.category.repository.mapper.CategoryMapper;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CategoryManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostSearchDto;
import com.service.core.post.service.PostService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.core.post.paging.PostPaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PostService postService;
    private final BlogService blogService;

    @Override
    public CategoryDto findCategoryDtoByUserId(String userId) {
        return CategoryDto.fromEntity(categoryMapper.findCategoriesByUserId(userId));
    }

    @Override
    public CategoryDto findCategoryDtoByBlogId(Long blogId) {
        return CategoryDto.fromEntity(categoryMapper.findCategoriesByBlogId(blogId));
    }

    @Override
    public PostTotalDto findPostByCategoryId(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            return PostTotalDto.fromPostDtoList(Collections.emptyList(), ConstUtil.NOT_EXIST_CATEGORY);
        } else {
            Category category = categoryOptional.get();
            return PostTotalDto.fromPostDtoList(category.getPostList().stream().sorted(Comparator.comparing(Post::getRegisterTime).reversed())
                    .map(PostDto::fromEntity).collect(Collectors.toList()), findCategoryName(category));
        }
    }

    @Override
    public PostPaginationResponse<PostTotalDto> findPaginationPostByCategoryId(Long categoryId, PostSearchDto postSearchDto) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            int postCount = 0;
            PostPagination postPagination = new PostPagination(postCount, postSearchDto);
            postSearchDto.setPostPagination(postPagination);
            return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(Collections.emptyList(), ConstUtil.NOT_EXIST_CATEGORY), postPagination);
        } else {
            Category category = categoryOptional.get();
            List<Post> postList = category.getPostList();
            int postCount = postList.size();
            PostPagination postPagination = new PostPagination(postCount, postSearchDto);
            postSearchDto.setPostPagination(postPagination);

            return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(
                    BlogUtil.getSlice(
                                    category.getPostList().stream().sorted(Comparator.comparing(Post::getRegisterTime).reversed()),
                                    postSearchDto.getPostPagination().getLimitStart(),
                                    postSearchDto.getRecordSize())
                            .map(PostDto::fromEntity).collect(Collectors.toList()), findCategoryName(category)), postPagination);
        }
    }

    @Override
    public PostTotalDto findPostByBlogId(Long blogId) {
        return postService.findTotalPost(blogId, ConstUtil.TOTAL_CATEGORY);
    }

    @Override
    public PostPaginationResponse<PostTotalDto> findPaginationPostByBlogId(Long blogId, PostSearchDto postSearchDto) {
        return postService.findTotalPaginationPost(blogId, postSearchDto, ConstUtil.TOTAL_CATEGORY);
    }

    @Override
    public String findCategoryName(Category category) {
        Long parentCategoryId = category.getParentId();
        String categoryName = category.getName();

        if (parentCategoryId == 0 || !categoryRepository.existsById(parentCategoryId)) {
            return categoryName;
        } else {
            Category parentCategory = categoryRepository.findById(parentCategoryId).get();
            String parentCategoryName = parentCategory.getName();
            return parentCategoryName + "/" + categoryName;
        }
    }

    @Override
    public Category findCategoryById(String email, Long categoryId) {
        List<Category> categoryList = blogService.findBlogByEmail(email).getCategoryList();

        for (Category category : categoryList) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        throw new CategoryManageException(ServiceExceptionMessage.CATEGORY_NOT_FOUND);
    }

    @Transactional
    @Override
    public Category registerBasicCategory(Blog blog) {
        return categoryRepository.save(Category.from(blog));
    }
}
