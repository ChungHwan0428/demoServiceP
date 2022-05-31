package com.practice.demo.exception;

public class MemberEmailAlreadyExistsException extends RuntimeException{
    public MemberEmailAlreadyExistsException(String message){
        super(message);
    }
}
