package com.practice.demo.controller.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.demo.controller.response.Response;
import com.practice.demo.dto.sign.SignInRequest;
import com.practice.demo.dto.sign.SignUpRequest;
import com.practice.demo.service.sign.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.practice.demo.controller.response.Response.success;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/api/signUp")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest request){
        log.info("직렬화 성공\n\n");
        signService.signUp(request);
        log.info("login success\n\n");
        return success();
    }

    @PostMapping("/api/signIn")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest request){
        return success(signService.signIn(request));
    }

    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response refreshToken(@RequestHeader(value = "Authorization") String refreshToken){
        return success(signService.refreshToken(refreshToken));
    }
}
