package com.practice.demo.dto.sign;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

public class SignInRequestValidationTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest(){
        //given
        SignInRequest request = createRequest();

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request);

        //then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByNotFormattedEmailTest() {
        // given
        String invalidValue = "email";
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty(); // 4
        assertThat(validate.stream().map(v->v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyEmail(){
        //given
        String invalidValue = null;
        SignInRequest request = createRequestWithEmail(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v->v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankEmailTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyPasswordTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankPasswordTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createRequestWithPassword(" ");

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    private SignInRequest createRequest() {
        return new SignInRequest("email@naver.com","123456a!");
    }

    private SignInRequest createRequestWithEmail(String email){
        return new SignInRequest(email,"123456a!");
    }

    private SignInRequest createRequestWithPassword(String password){
        return new SignInRequest("email@naver.com",password);
    }
}
