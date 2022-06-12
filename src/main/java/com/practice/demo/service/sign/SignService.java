package com.practice.demo.service.sign;

import com.practice.demo.config.token.TokenHelper;
import com.practice.demo.dto.sign.RefreshTokenResponse;
import com.practice.demo.dto.sign.SignInRequest;
import com.practice.demo.dto.sign.SignInResponse;
import com.practice.demo.dto.sign.SignUpRequest;
import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.RoleType;
import com.practice.demo.exception.*;
import com.practice.demo.repository.member.MemberRepository;
import com.practice.demo.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenHelper accessTokenHelper;
    private final TokenHelper refreshTokenHelper;

    @Transactional
    public void signUp(SignUpRequest request){
        validateSignUpInfo(request);
        memberRepository.save(SignUpRequest.toEntity(request,
                roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                passwordEncoder));
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request){
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(MemberNotFoundException::new);
        validatePassword(request,member);

        String subject = creatSubject(member);
        String accessToken = accessTokenHelper.createToken(subject);
        String refreshToken = refreshTokenHelper.createToken(subject);
        return new SignInResponse(accessToken,refreshToken);
    }

    private void validateSignUpInfo(SignUpRequest request){
        if(memberRepository.existsByEmail(request.getEmail()))
            throw new MemberEmailAlreadyExistsException(request.getEmail());

        if(memberRepository.existsByNickname(request.getNickname()))
            throw new MemberNicknameAlreadyExistsException(request.getNickname());
    }

    private void validatePassword(SignInRequest request, Member member){
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new LoginFailurException();
        }
    }

    private String creatSubject(Member member){
        return String.valueOf(member.getId());
    }

    public RefreshTokenResponse refreshToken(String refreshToken){
        validateRefreshToken(refreshToken);
        String subject = refreshTokenHelper.extractSubject(refreshToken);
        String accessToken = accessTokenHelper.createToken(subject);
        return new RefreshTokenResponse(accessToken);
    }

    private void validateRefreshToken(String refreshToken){
        if(!refreshTokenHelper.validate(refreshToken)){
            throw new AuthenticationEntryPointException();
        }
    }
}
