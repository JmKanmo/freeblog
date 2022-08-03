package com.service.core.user.repository;

import com.service.core.user.domain.UserPasswordAuth;
import org.springframework.data.repository.CrudRepository;

public interface UserPasswordAuthRepository extends CrudRepository<UserPasswordAuth, Integer> {
}
