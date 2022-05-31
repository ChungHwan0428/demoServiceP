package com.practice.demo.exception;

public class MemberNicknameAlreadyExistsException extends RuntimeException{
    public MemberNicknameAlreadyExistsException(String message){
        super(message);
    }
}
