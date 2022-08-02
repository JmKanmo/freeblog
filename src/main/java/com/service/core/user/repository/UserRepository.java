package com.service.core.user.repository;

import com.service.core.user.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDomain, String> {
    Optional<UserDomain> findByEmail(String email);
    List<UserDomain> findUsersByNicknameStartsWith(String nickname);
}
