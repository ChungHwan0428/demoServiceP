package com.practice.demo.service.sign;

import com.practice.demo.handler.JwtHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks TokenService tokenService;
    @Mock
    JwtHandler jwtHandler;

    //Mock을 이용하기 때문에 빈 객체이므로 @Value도 처리를 못하기 때문에 주입?
    @BeforeEach
    void before(){
        ReflectionTestUtils.setField(tokenService,"accessTokenMaxAgeSeconds",10L);
        ReflectionTestUtils.setField(tokenService,"refreshTokenMaxAgeSeconds",10L);
        ReflectionTestUtils.setField(tokenService,"accessKey","accessKey");
        ReflectionTestUtils.setField(tokenService,"refreshKey","refreshKey");

    }

    @Test
    void 엑세스토큰생성(){
        //given
        given(jwtHandler.createToken(anyString(),anyString(),anyLong())).willReturn("access");

        //when
        String token = tokenService.createAccessToken("subject");

        //then
        assertThat(token).isEqualTo("access");
        verify(jwtHandler).createToken(anyString(),anyString(),anyLong());

    }

    @Test
    void 리프레시토큰생성(){
        //given
        given(jwtHandler.createToken(anyString(),anyString(),anyLong())).willReturn("refresh");

        //when
        String token = tokenService.createRefreshToken("subject");

        //then
        assertThat(token).isEqualTo("refresh");
        verify(jwtHandler).createToken(anyString(),anyString(),anyLong());

    }


}
