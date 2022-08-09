package com.service.core.user.controller;

import static org.mockito.BDDMockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.core.user.model.UserBasicInfoInput;
import com.service.core.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.CharacterEncodingFilter;

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
    public void loginApiTest() throws Exception {
        mockMvc.perform(get("/user/login")).andExpect(status().isOk());
    }

    @Test
    public void signUpCompleteTest() throws Exception {
        mockMvc.perform(get("/user/signup-complete")).andExpect(status().isOk());
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