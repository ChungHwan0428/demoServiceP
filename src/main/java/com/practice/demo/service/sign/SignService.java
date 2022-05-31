package com.practice.demo.service.sign;

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
@Transactional(readOnly = true)
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public void signUp(SignUpRequest request){
        validateSignUpInfo(request);
        memberRepository.save(SignUpRequest.toEntity(request,
                roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                passwordEncoder));
    }

    public SignInResponse signIn(SignInRequest request){
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(MemberNotFoundException::new);
        validatePassword(request,member);

        String subject = creatSubject(member);
        String accessToken = tokenService.createAccessToken(subject);
        String refreshToken = tokenService.createRefreshToken(subject);
        return new SignInResponse(accessToken,refreshToken);
    }

    private void validateSignUpInfo(SignUpRequest request){
        if(memberRepository.existsByEmail(request.getEmail()))
            throw new MemberEmailAlreadyExistsException(request.getEmail());

        if(memberRepository.existsByNickname(request.getNickname()))
            throw new MemberNicknameAlreadyExistsException(request.getNickname());
    }

    private void validatePassword(SignInRequest request, Member member){
        if(!passwordEncoder.matches(request.getPasswrod(), member.getPassword())){
            throw new LoginFailurException();
        }
    }

    private String creatSubject(Member member){
        return String.valueOf(member.getId());
    }
}
