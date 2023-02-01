package com.service.core.category.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryDto;
import com.service.core.category.model.CategoryInput;
import com.service.core.category.repository.CategoryRepository;
import com.service.core.category.repository.mapper.CategoryMapper;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.error.model.CategoryManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostSearchDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostSearchPagingDto;
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
        return CategoryDto.fromEntity(!blogService.isDeleteOrNotFoundBlog(blogId) ? categoryMapper.findCategoriesByBlogId(blogId) : Collections.emptyList());
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
    public PostPaginationResponse<PostTotalDto> findPaginationPostByCategoryId(Long categoryId, PostSearchPagingDto postSearchPagingDto) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            int postCount = 0;
            PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
            postSearchPagingDto.setPostPagination(postPagination);
            return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(Collections.emptyList(), ConstUtil.NOT_EXIST_CATEGORY), postPagination);
        } else {
            Category category = categoryOptional.get();
            Blog blog = category.getBlog();

            if (category.isDelete()) {
                throw new CategoryManageException(ServiceExceptionMessage.ALREADY_DELETE_CATEGORY);
            } else if (blog == null || blog.isDelete()) {
                throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
            }

            Long blogId = blog.getId();
            int postCount = postService.findPostCountByBlogCategory(blogId, categoryId);

            PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
            postSearchPagingDto.setPostPagination(postPagination);

            return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(postService.findPostPaginationById(PostSearchDto.from(blogId, categoryId, postSearchPagingDto)), postCount, findCategoryName(category)), postPagination);
        }
    }

    @Override
    public PostPaginationResponse<PostTotalDto> findPaginationPostByBlogId(Long blogId, PostSearchPagingDto postSearchPagingDto) {
        return postService.findTotalPaginationPost(blogId, postSearchPagingDto, ConstUtil.TOTAL_CATEGORY);
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
            if (category.isDelete()) {
                continue;
            } else if (category.getId() == categoryId) {
                return category;
            }
        }
        throw new CategoryManageException(ServiceExceptionMessage.CATEGORY_NOT_FOUND);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryManageException(ServiceExceptionMessage.CATEGORY_NOT_FOUND));

        if (category.isDelete()) {
            throw new CategoryManageException(ServiceExceptionMessage.ALREADY_DELETE_CATEGORY);
        }
        return category;
    }

    @Transactional
    @Override
    public Category registerBasicCategory(Blog blog) {
        return categoryRepository.save(Category.from(blog));
    }

    @Transactional
    @Override
    public void registerCategory(Long blogId, List<CategoryInput> categoryInputList) {
        Blog blog = blogService.findBlogByIdOrThrow(blogId);
        List<Category> categories = blog.getCategoryList();
        // TODO 
    }
}
