package com.service.core.user.controller;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserBasicInfoInput;
import com.service.core.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 페이지")
    public void loginApiTest() throws Exception {
        mockMvc.perform(get("/user/login").with(anonymous())).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 페이지")
    public void signUpTest() throws Exception {
        mockMvc.perform(get("/user/signup").with(user("hello").password("jmkang"))).andDo(print()).andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/user/signup").with(anonymous())).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 완료 페이지")
    public void signUpCompleteTest() throws Exception {
        mockMvc.perform(get("/user/signup-complete").with(user("hello").password("jmkang"))).andDo(print()).andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/user/signup-complete").with(anonymous())).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 정보 찾기 페이지")
    public void findUserInfo() throws Exception {
        mockMvc.perform(get("/user/find-info").with(user("hello").password("jmkang"))).andDo(print()).andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/user/find-info").with(anonymous())).andDo(print()).andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("이메일 찾기 페이지")
    @ValueSource(strings = "JmKanmo")
    public void emailAuth(String nickname) throws Exception {
        List<UserEmailFindDto> userEmailFindDtoList = Arrays.asList(
                UserEmailFindDto.builder().email("apdh1709@gmail.com").nickname("jmkanmo").build(),
                UserEmailFindDto.builder().email("nebi25@naver.com").nickname("nebiros").build(),
                UserEmailFindDto.builder().email("akxk25@naver.com").nickname("akxk").build());

        when(userService.findUserEmailFindDtoListByNickname(nickname))
                .thenReturn(userEmailFindDtoList);

        mockMvc.perform(get("/user/find-email").param("nickname", nickname).with(user("hello").password("jmkang"))).andDo(print()).andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/user/find-email").param("nickname", nickname).with(anonymous())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", userEmailFindDtoList));
    }

    @Test
    @DisplayName("비밀번호 수정 페이지")
    public void updatePassword() throws Exception {
        mockMvc.perform(get("/user/update/password").with(user("hello").password("jmkang"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPasswordInput"));
        mockMvc.perform(get("/user/update/password").with(anonymous())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPasswordInput"));
    }

    @Test
    @DisplayName("소셜 계정 업데이트 페이지")
    public void updateSocialAddress() throws Exception {
        mockMvc.perform(get("/user/update/social-address").with(user("hello").password("jmkang"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userSocialAddressInput"));
        mockMvc.perform(get("/user/update/social-address").with(anonymous())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userSocialAddressInput"));
    }

    @Test
    public void updateBasicInfoEdit() throws Exception {
        UserBasicInfoInput userBasicInfoInput = UserBasicInfoInput.builder().id("nebi25")
                .greetings("hello").nickname("nebi25")
                .blogName("nebiros blog").build();
        doNothing().when(userService).updateUserBasicInfo(any());
        mockMvc.perform(get("/user/update/basic-info").content(objectMapper.writeValueAsString(userBasicInfoInput)))
                .andExpect(status().isOk());
    }
}