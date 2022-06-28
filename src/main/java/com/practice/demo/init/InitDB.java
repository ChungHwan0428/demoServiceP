package com.practice.demo.init;

import com.practice.demo.entity.category.Category;
import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.Role;
import com.practice.demo.entity.member.RoleType;
import com.practice.demo.exception.RoleNotFoundException;
import com.practice.demo.repository.category.CategoryRepository;
import com.practice.demo.repository.member.MemberRepository;
import com.practice.demo.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class InitDB {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDB(){
        log.info("db 초기화");
        initRole();
        initTestAdmin();
        initTestMember();
        initCategory();
    }

    private void initTestMember() {
        memberRepository.saveAll(
                List.of(
                        new Member("member1@member.com", passwordEncoder.encode("123456a!"), "member1", "member1",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))),
                        new Member("member2@member.com", passwordEncoder.encode("123456a!"), "member2", "member2",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))))
        );
    }

    private void initRole(){
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin(){
        memberRepository.save(
                new Member("admin@admin.com",passwordEncoder.encode("123456a!"),"admin","admin",
                        List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                                roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new)))
        );
    }

    private void initCategory() {
        Category c1 = categoryRepository.save(new Category("category1", null));
        Category c2 = categoryRepository.save(new Category("category2", c1));
        Category c3 = categoryRepository.save(new Category("category3", c1));
        Category c4 = categoryRepository.save(new Category("category4", c2));
        Category c5 = categoryRepository.save(new Category("category5", c2));
        Category c6 = categoryRepository.save(new Category("category6", c4));
        Category c7 = categoryRepository.save(new Category("category7", c3));
        Category c8 = categoryRepository.save(new Category("category8", null));
    }

}
