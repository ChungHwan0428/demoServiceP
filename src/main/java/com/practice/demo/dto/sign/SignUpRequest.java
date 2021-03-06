package com.practice.demo.dto.sign;

import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @Email(message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호을 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
    message = "비밀번호는 최소 8자리입니다. 1개 이상의 알파벳, 숫자, 특수문자를 포함해주세요.")
    private String password;

    @NotBlank(message = "사용자 이름을 입력해주세요")
    @Size(min = 2, message = "사용자 이름이 너무 짧습니다.")
    @Pattern(regexp="^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.")
    private String username;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임이 너무 짧습니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "닉네임은 한글 또는 알파벳만 입력해주세요.")
    private String nickname;

    public static Member toEntity(SignUpRequest request, Role role, PasswordEncoder encoder){
        return new Member(request.email,encoder.encode(request.password), request.username, request.nickname, List.of(role));
    }
}
