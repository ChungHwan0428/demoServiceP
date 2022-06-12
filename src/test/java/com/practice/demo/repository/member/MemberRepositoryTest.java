package com.practice.demo.repository.member;

import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.MemberRole;
import com.practice.demo.entity.member.Role;
import com.practice.demo.entity.member.RoleType;
import com.practice.demo.exception.MemberNotFoundException;
import com.practice.demo.repository.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.practice.demo.factory.entity.MemberFactory.createMember;
import static com.practice.demo.factory.entity.MemberFactory.createMemberWithRoles;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void before(){
        memberRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
    }

    @Test
    void 맴버생성읽기(){
        Member member = createMember();

        memberRepository.save(member);
        clear();

        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void 맴버생성날짜테스트(){
        Member member = createMember();

        //when
        memberRepository.save(member);
        clear();

        //then
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getCreatedAt()).isNotNull();
        assertThat(foundMember.getModifiedAt()).isNotNull();
        assertThat(foundMember.getCreatedAt()).isEqualTo(foundMember.getModifiedAt());

    }

    @Test
    void 맴버업데이트(){
        //given
        String updatedNickname = "hello";
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.updateNickname(updatedNickname);
        clear();

        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(updatedMember.getNickname()).isEqualTo(updatedNickname);
    }

    @Test
    void 맴버삭제(){
        //given
        Member member = createMember();
        memberRepository.save(member);
        clear();

        //when
        memberRepository.delete(member);
        clear();

        //then
        assertThatThrownBy(()->memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 이메일로맴버찾기(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findByEmail(member.getEmail()).orElseThrow(MemberNotFoundException::new);

        //then
        assertThat(member.getEmail()).isEqualTo(foundMember.getEmail());

    }

    @Test
    void 닉네임으로맴버찾기(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findByNickname(member.getNickname()).orElseThrow(MemberNotFoundException::new);

        //then
        assertThat(member.getNickname()).isEqualTo(foundMember.getNickname());

    }

    @Test
    void 유일한이메일테스트(){
        //given
        Member member = memberRepository.save(createMember("email1","password1","username1","nickname1"));
        clear();

        //when && then
        assertThatThrownBy(()->memberRepository.save(createMember(member.getEmail(),"password2","username2","nickname2")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 유일한닉네임테스트(){
        //given
        Member member = memberRepository.save(createMember("email1","password1","username1","nickname1"));
        clear();

        //when && then
        assertThatThrownBy(()->memberRepository.save(createMember("email2","password2","username2",member.getNickname())))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 이미존재하는이메일인가요(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when && then
        assertThat(memberRepository.existsByEmail(member.getEmail())).isTrue();
        assertThat(memberRepository.existsByEmail(member.getEmail()+"test")).isFalse();

    }

    @Test
    void 이미존재하는닉네임인가요(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when && then
        assertThat(memberRepository.existsByNickname(member.getNickname())).isTrue();
        assertThat(memberRepository.existsByNickname(member.getNickname()+"test")).isFalse();

    }

    @Test
    void memberRoleCascadePersistTest(){
        //given
        List<RoleType> roleTypes = List.of(RoleType.ROLE_NORMAL,RoleType.ROLE_SPECIAL_BUYER,RoleType.ROLE_ADMIN);
        List<Role>roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(Collectors.toList());
        roleRepository.saveAll(roles);
        clear();

        Member member = memberRepository.save(createMemberWithRoles(roleRepository.findAll()));
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        Set<MemberRole>memberRoles = foundMember.getRoles();

        //then
        assertThat(memberRoles.size()).isEqualTo(roles.size());

    }

    @Test
    void memberRoleCascadeDeleteTest(){
        //given
        List<RoleType> roleTypes = List.of(RoleType.ROLE_NORMAL,RoleType.ROLE_SPECIAL_BUYER,RoleType.ROLE_ADMIN);
        List<Role>roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(Collectors.toList());
        roleRepository.saveAll(roles);
        clear();

        Member member = memberRepository.save(createMemberWithRoles(roleRepository.findAll()));
        clear();

        //when
        memberRepository.deleteById(member.getId());
        clear();

        //then
        List<MemberRole>result = em.createQuery("select mr from MemberRole mr",MemberRole.class).getResultList();
        assertThat(result.size()).isZero();

    }

    private void clear() {
        em.flush();
        em.clear();
    }


}
