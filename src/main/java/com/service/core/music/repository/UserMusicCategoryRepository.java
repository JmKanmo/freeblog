package com.service.core.music.repository;

import com.service.core.music.domain.UserMusicCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMusicCategoryRepository extends JpaRepository<UserMusicCategory, Long> {
}
