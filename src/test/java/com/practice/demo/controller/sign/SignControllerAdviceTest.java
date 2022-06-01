package com.practice.demo.controller.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.demo.advice.ExceptionAdvice;
import com.practice.demo.dto.sign.SignInRequest;
import com.practice.demo.dto.sign.SignUpRequest;
import com.practice.demo.exception.LoginFailurException;
import com.practice.demo.exception.MemberEmailAlreadyExistsException;
import com.practice.demo.exception.MemberNicknameAlreadyExistsException;
import com.practice.demo.exception.RoleNotFoundException;
import com.practice.demo.service.sign.SignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SignControllerAdviceTest {

    @InjectMocks SignController signController;
    @Mock SignService signService;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void before(){
        mockMvc = MockMvcBuilders.standaloneSetup(signController).setControllerAdvice(new ExceptionAdvice()).build();
    }

    @Test
    void 로그인실패예외() throws Exception{
        //given
        SignInRequest request = new SignInRequest("email@naver.com", "123456a!");
        given(signService.signIn(any())).willThrow(LoginFailurException.class);

        //when,then
        mockMvc.perform(
                post("/api/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인유효하지않은변수() throws Exception{
        //given
        SignInRequest request = new SignInRequest("email", "1234567");

        //when,then
        mockMvc.perform(post("/api/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입이메일중복() throws Exception{
        //given
        SignUpRequest request = new SignUpRequest("email@naver.com", "123456a!", "username", "nickname");
        doThrow(MemberEmailAlreadyExistsException.class).when(signService).signUp(any());

        //when,then
        mockMvc.perform(post("/api/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

    }

    @Test
    void 회원가입닉네임중복() throws Exception{
        //given
        SignUpRequest request = new SignUpRequest("email@naver.com", "123456a!", "username", "nickname");
        doThrow(MemberNicknameAlreadyExistsException.class).when(signService).signUp(any());

        //when,then
        mockMvc.perform(post("/api/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void 회원가입존재하지않는역할() throws Exception{
        //given
        SignUpRequest request = new SignUpRequest("email@naver.com", "123456a!", "username", "nickname");
        doThrow(RoleNotFoundException.class).when(signService).signUp(any());

        //when,then
        mockMvc.perform(post("/api/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 회원가입잘못된입력값() throws Exception{
        //given
        SignUpRequest request = new SignUpRequest("", "", "", "");

        //when,then
        mockMvc.perform(post("/api/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
