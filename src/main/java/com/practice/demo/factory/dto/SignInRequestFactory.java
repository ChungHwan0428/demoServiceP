package com.practice.demo.factory.dto;

import com.practice.demo.dto.sign.SignInRequest;

public class SignInRequestFactory {
    public static SignInRequest createSingInRequest(){
        return new SignInRequest("email@naver.com","123456a!");
    }

    public static SignInRequest createSingInRequest(String email, String password){
        return new SignInRequest(email,password);
    }

    public static SignInRequest createSingInRequestWithEmail(String email){
        return new SignInRequest(email,"123456a!");
    }

    public static SignInRequest createSingInRequestWithPassword(String password){
        return new SignInRequest("email@naver.com",password);
    }
}
