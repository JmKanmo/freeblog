package com.service.core.music.repository;

import com.service.core.music.domain.MusicCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicCategoryRepository extends JpaRepository<MusicCategory, Long> {
}
