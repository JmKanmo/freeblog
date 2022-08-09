package com.service.core.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserSocialAddressInput {
    @Size(max = 128, message = "주소는 최대 256글자 까지 작성 가능합니다.")
    private final String address;

    @Size(max = 128, message = "깃허브 주소는 최대 256글자 까지 작성 가능합니다.")
    private final String github;

    @Size(max = 128, message = "트위터 주소는 최대 256글자 까지 작성 가능합니다.")
    private final String twitter;

    @Size(max = 128, message = "인스타그램 주소는 최대 256글자 까지 작성 가능합니다.")
    private final String instagram;

    @NotEmpty
    @NotBlank
    @Pattern(regexp = "^[a-z]{1}[a-z0-9]{4,11}$", message = "아이디 패턴에 어긋나는 형식입니다.")
    private final String id;
}
