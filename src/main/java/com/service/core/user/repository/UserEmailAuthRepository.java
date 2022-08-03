package com.service.core.user.repository;

import com.service.core.user.domain.UserEmailAuth;
import org.springframework.data.repository.CrudRepository;

public interface UserEmailAuthRepository extends CrudRepository<UserEmailAuth, Integer> {
}
