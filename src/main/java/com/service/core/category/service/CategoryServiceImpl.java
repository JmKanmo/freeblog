package com.service.core.category.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryBasicMapperDto;
import com.service.core.category.dto.CategoryMapperDto;
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
import com.service.core.post.dto.PostTitleDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.service.PostService;
import com.service.util.ConstUtil;
import com.service.core.post.paging.PostPaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
            BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByCategoryId(categoryId);

            if (category.isDelete()) {
                throw new CategoryManageException(ServiceExceptionMessage.ALREADY_DELETE_CATEGORY);
            } else if (blogDeleteDto == null || blogDeleteDto.isDelete()) {
                throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
            }

            Long blogId = blogDeleteDto.getId();
            int postCount = postService.findPostCountByBlogCategory(blogId, categoryId);

            PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
            postSearchPagingDto.setPostPagination(postPagination);

            return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(postService.findPostPaginationById(PostSearchDto.from(blogId, categoryId, postSearchPagingDto)), postCount, findCategoryName(category)), postPagination);
        }
    }

    @Override
    public PostPaginationResponse<List<PostTitleDto>> findPaginationPostTitleByCategoryId(Long categoryId, PostSearchPagingDto postSearchPagingDto) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            int postCount = 0;
            PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
            postSearchPagingDto.setPostPagination(postPagination);
            return new PostPaginationResponse<>(Collections.emptyList(), postPagination);
        } else {
            Category category = categoryOptional.get();
            BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByCategoryId(categoryId);

            if (category.isDelete()) {
                throw new CategoryManageException(ServiceExceptionMessage.ALREADY_DELETE_CATEGORY);
            } else if (blogDeleteDto == null || blogDeleteDto.isDelete()) {
                throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
            }

            Long blogId = blogDeleteDto.getId();
            int postCount = postService.findPostCountByBlogCategory(blogId, categoryId);

            PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
            postSearchPagingDto.setPostPagination(postPagination);

            return new PostPaginationResponse<>(postService.findPostTitlePaginationById(PostSearchDto.from(blogId, categoryId, postSearchPagingDto)), postPagination);
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
            String parentCategoryName = categoryRepository.findById(parentCategoryId).get().getName();
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
        List<Category> newCategories = new ArrayList<>();

        checkCategoryInputRelation(categoryInputList);

        for (Category category : categories) {
            boolean contains = false;

            for (CategoryInput categoryInput : categoryInputList) {
                if (checkCategory(category, categoryInput)) {
                    category.setName(categoryInput.getName());
                    categoryInputList.remove(categoryInput);
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                category.setDelete(true);

                for (Post post : category.getPostList()) {
                    if (!post.isDelete()) {
                        post.setDelete(true);
                    }
                }
            }
        }

        for (CategoryInput categoryInput : categoryInputList) {
            newCategories.add(Category.from(categoryInput, blog));
        }

        List<Category> createdCategories = categoryRepository.saveAll(newCategories);

        if (getCategoryTypeSet(createdCategories).size() >= 2) {
            List<CategoryMapperDto> categoryDtoList = categoryMapper.findCategoriesByBlogId(blogId);

            Map<Long, CategoryMapperDto> parentCategoryMap = new HashMap<>();

            for (CategoryMapperDto categoryMapperDto : categoryDtoList) {
                if (categoryMapperDto.getParentId() == 0) {
                    parentCategoryMap.put(categoryMapperDto.getSeq(), categoryMapperDto);
                }
            }

            for (Category category : createdCategories) {
                if (category.getParentId() != 0) {
                    if (parentCategoryMap.containsKey(category.getSeq())) {
                        category.setParentId(parentCategoryMap.get(category.getSeq()).getCategoryId());
                    }
                }
            }
            categoryRepository.saveAll(createdCategories);
        }
    }

    @Override
    public CategoryBasicMapperDto findCategoryBasicMapperDtoByCategoryId(Long categoryId) {
        CategoryBasicMapperDto categoryBasicMapperDto = categoryMapper.findCategoryBasicMapperDtoByCategoryId(categoryId);

        if (categoryBasicMapperDto == null) {
            throw new CategoryManageException(ServiceExceptionMessage.CATEGORY_NOT_FOUND);
        } else if (categoryBasicMapperDto.getIsDelete()) {
            throw new CategoryManageException(ServiceExceptionMessage.ALREADY_DELETE_CATEGORY);
        }

        return categoryBasicMapperDto;
    }

    @Override
    public CategoryBasicMapperDto findCategoryBasicMapperDtoByCategoryIdAndEmail(Long categoryId, String email) {
        CategoryBasicMapperDto categoryBasicMapperDto = categoryMapper.findCategoryBasicMapperDtoByCategoryIdAndEmail(categoryId, email);

        if (categoryBasicMapperDto == null) {
            throw new CategoryManageException(ServiceExceptionMessage.CATEGORY_NOT_FOUND);
        } else if (categoryBasicMapperDto.getIsDelete()) {
            throw new CategoryManageException(ServiceExceptionMessage.ALREADY_DELETE_CATEGORY);
        }

        return categoryBasicMapperDto;
    }

    private boolean checkCategory(Category category, CategoryInput categoryInput) {
        String categoryType = categoryInput.getType();

        if (!categoryType.equals("childCategory") && !categoryType.equals("parentCategory")) {
            throw new CategoryManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
        }

        if (category.getId() == categoryInput.getId() &&
                category.getSeq() == categoryInput.getSeq()) {
            boolean isChild = category.getParentId() == 0 ? false : true;
            if (isChild && categoryInput.getType().equals("childCategory") || (!isChild && categoryInput.getType().equals("parentCategory"))) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getCategoryTypeSet(List<Category> categories) {
        Set<String> set = new HashSet<>();

        for (Category category : categories) {
            set.add(category.getParentId() == 0 ? "parentCategory" : "childCategory");
        }
        return set;
    }

    private void checkCategoryInputRelation(List<CategoryInput> categoryInputList) {
        Map<Long, CategoryInput> categoryInputMap = new HashMap<>();
        Set<String> categoryTypeSet = new HashSet<>();

        for (CategoryInput categoryInput : categoryInputList) {
            String categoryType = categoryInput.getType();
            categoryTypeSet.add(categoryType);

            if (categoryType.equals("parentCategory")) {
                categoryInputMap.put(categoryInput.getId(), categoryInput);
            } else if (categoryType.equals("childCategory")) {
                if (!categoryInputMap.containsKey(categoryInput.getParentId())) {
                    throw new CategoryManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
                }
            }
        }
    }
}
