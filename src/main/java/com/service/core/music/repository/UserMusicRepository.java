package com.service.core.music.repository;

import com.service.core.music.domain.UserMusic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMusicRepository extends JpaRepository<UserMusic, Long> {
}
