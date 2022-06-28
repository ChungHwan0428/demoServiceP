package com.practice.demo.config.security.guard;

import com.practice.demo.entity.member.RoleType;
import com.practice.demo.entity.post.Post;
import com.practice.demo.exception.AccessDeniedException;
import com.practice.demo.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostGuard {

    private final AuthHelper authHelper;
    private final PostRepository postRepository;

    public boolean check(Long id){
        return authHelper.isAuthenticated() && hasAuthority(id);
    }

    private boolean hasAuthority(Long id){
        return hasAdminRole() || isResourceOwner(id);
    }

    private boolean isResourceOwner(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(); });
        Long memberId = authHelper.extractMemberId();
        return post.getMember().getId().equals(memberId);
    }

    private boolean hasAdminRole(){
        return authHelper.extractMemberRoles().contains(RoleType.ROLE_ADMIN);
    }
}
