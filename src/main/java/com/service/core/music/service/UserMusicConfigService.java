package com.service.core.music.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.MusicManageException;
import com.service.core.music.domain.UserMusicConfig;
import com.service.core.music.dto.UserMusicConfigDto;
import com.service.core.music.model.UserMusicConfigInput;
import com.service.core.music.repository.UserMusicConfigRepository;
import com.service.core.music.repository.mapper.UserMusicConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserMusicConfigService {
    private final UserMusicConfigRepository userMusicConfigRepository;
    private final UserMusicConfigMapper userMusicConfigMapper;
    private final BlogService blogService;

    public UserMusicConfigDto findUserMusicConfigDtoById(Long blogId) {
        return userMusicConfigMapper.searchUserMusicConfigByBlogId(blogId);
    }

    public UserMusicConfigDto findUserMusicConfigDtoByIdOrDefault(Long blogId) {
        UserMusicConfigDto userMusicConfigDto = userMusicConfigMapper.searchUserMusicConfigByBlogId(blogId);
        return userMusicConfigDto == null ? UserMusicConfigDto.getDefaultUserMusicConifgDto() : userMusicConfigDto;
    }

    public UserMusicConfig findUserMusicConfigById(Long id) {
        return userMusicConfigRepository.findById(id).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CONFIG_NOT_FOUND));
    }

    @Transactional
    public void saveUserMusicConfig(UserMusicConfigInput userMusicConfigInput, String email) {
        Blog blog = blogService.findBlogByEmail(email);
        UserMusicConfigDto userMusicConfigDto = findUserMusicConfigDtoById(blog.getId());

        if (userMusicConfigDto != null) {
            UserMusicConfig userMusicConfig = findUserMusicConfigById(userMusicConfigDto.getUserMusicConfigId());
            userMusicConfig.update(userMusicConfigInput);
        } else {
            userMusicConfigRepository.save(UserMusicConfig.from(userMusicConfigInput, blog));
        }
    }
}
