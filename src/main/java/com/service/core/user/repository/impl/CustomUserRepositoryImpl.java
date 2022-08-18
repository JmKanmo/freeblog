package com.service.core.user.repository.impl;

import static com.service.core.user.domain.QUserDomain.*;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserStatus;
import com.service.core.user.repository.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 추후에 Paging 적용
     * 발생 쿼리
     * SELECT email, nickname
     * from user
     * WHERE nickname LIKE CONCAT(#{nickname}, '%')
     * and status not in ('WITHDRAW', 'STOP')
     *
     * @param nickname
     * @return List<UserEmailFindDto>
     */
    @Override
    public List<UserEmailFindDto> findUsersByNickName(String nickname) {
        return jpaQueryFactory.select(Projections.bean(UserEmailFindDto.class, userDomain.email, userDomain.nickname))
                .from(userDomain)
                .where(userDomain.nickname.startsWith(nickname).and(userDomain.status.notIn(UserStatus.WITHDRAW, UserStatus.STOP)))
                .fetch();
    }
}
