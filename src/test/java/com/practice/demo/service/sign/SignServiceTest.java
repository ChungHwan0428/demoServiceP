package com.practice.demo.service.sign;

import com.mysql.cj.log.Log;
import com.practice.demo.config.token.TokenHelper;
import com.practice.demo.dto.sign.RefreshTokenResponse;
import com.practice.demo.dto.sign.SignInRequest;
import com.practice.demo.dto.sign.SignInResponse;
import com.practice.demo.dto.sign.SignUpRequest;
import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.Role;
import com.practice.demo.entity.member.RoleType;
import com.practice.demo.exception.*;
import com.practice.demo.repository.member.MemberRepository;
import com.practice.demo.repository.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.practice.demo.factory.dto.SignUpRequestFactory.createSignUpRequest;
import static com.practice.demo.factory.entity.MemberFactory.createMember;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SignServiceTest {

    SignService signService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TokenHelper accessTokenHelper;
    @Mock
    TokenHelper refreshTokenHelper;

    @BeforeEach
    void before(){
        signService = new SignService(memberRepository,roleRepository,passwordEncoder,accessTokenHelper,refreshTokenHelper);
    }

    @Test
    void signUpTest(){
        //given
        SignUpRequest request = createSignUpRequest();
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.of(new Role(RoleType.ROLE_NORMAL)));

        //when
        signService.signUp(request);

        //then
        verify(passwordEncoder).encode(request.getPassword());
        verify(memberRepository).save(any());

    }

    @Test
    void 회원가입중복이메일테스트(){
        //given
        given(memberRepository.existsByEmail(anyString())).willReturn(true);

        //when,then
        assertThatThrownBy(()->signService.signUp(createSignUpRequest()))
                .isInstanceOf(MemberEmailAlreadyExistsException.class);
    }

    @Test
    void 회원가입중복닉네임테스트(){
        //given
        given(memberRepository.existsByNickname(anyString())).willReturn(true);

        //when,then
        assertThatThrownBy(()->signService.signUp(createSignUpRequest()))
                .isInstanceOf(MemberNicknameAlreadyExistsException.class);
    }

    @Test
    void signUpRoleNotFoundTest(){
        //given
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(()->signService.signUp(createSignUpRequest()))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void 로그인테스트(){
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(),anyString())).willReturn(true);
        given(accessTokenHelper.createToken(anyString())).willReturn("access");
        given(refreshTokenHelper.createToken(anyString())).willReturn("refresh");

        //when
        SignInResponse response = signService.signIn(new SignInRequest("email", "password"));

        //then
        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void 로그인회원이아닌경우(){
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy(()->signService.signIn(new SignInRequest("email","password")))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 로그인틀린비밀번호(){
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(),anyString())).willReturn(false);

        //when,then
        assertThatThrownBy(()->signService.signIn(new SignInRequest("email","password")))
                .isInstanceOf(LoginFailurException.class);
    }

    @Test
    void refreshTokenTest (){
        //given
        String refreshToken = "refreshToken";
        String subject = "subject";
        String accessToken = "accessToken";
        given(refreshTokenHelper.validate(refreshToken)).willReturn(true);
        given(refreshTokenHelper.extractSubject(refreshToken)).willReturn(subject);
        given(accessTokenHelper.createToken(subject)).willReturn(accessToken);

        //when
        RefreshTokenResponse response = signService.refreshToken(refreshToken);

        //then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    void refreshTokenExceptionByInvalideTokenTest (){
        //given
        String refreshToken = "refreshToken";
        given(refreshTokenHelper.validate(refreshToken)).willReturn(false);

        //when
        //then
        assertThatThrownBy(()->signService.refreshToken(refreshToken))
                .isInstanceOf(AuthenticationEntryPointException.class);

    }
}
