package com.service.core.music.repository;

import com.service.core.music.domain.UserMusicConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMusicConfigRepository extends JpaRepository<UserMusicConfig, Long> {
}
