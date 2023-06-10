package com.service.core.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileMapperDto {
    private final String id;
    private final String nickname;
    private final String email;
    private final String greetings;
    private final String profileImages;
}
