package com.service.core.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class UserWithdrawInput {
    @NotEmpty
    @NotBlank
    @Pattern(regexp = "^[a-z]{1}[a-z0-9]{4,11}$", message = "아이디 패턴에 어긋나는 형식입니다.")
    private final String id;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "비밀번호 패턴에 어긋나는 형식입니다.")
    private final String password;
}
