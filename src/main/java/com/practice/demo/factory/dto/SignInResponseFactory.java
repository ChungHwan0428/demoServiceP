package com.practice.demo.factory.dto;

import com.practice.demo.dto.sign.SignInResponse;

public class SignInResponseFactory {
    public static SignInResponse createSignInResponse(String accessToken, String refreshToken){
        return new SignInResponse(accessToken,refreshToken);
    }
}
