package com.practice.demo.dto.sign;

import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Data
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String username;
    private String nickname;

    public static Member toEntity(SignUpRequest request, Role role, PasswordEncoder encoder){
        return new Member(request.email,encoder.encode(request.password), request.username, request.nickname, List.of(role));
    }
}
