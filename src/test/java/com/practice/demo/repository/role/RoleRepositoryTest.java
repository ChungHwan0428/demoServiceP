package com.practice.demo.repository.role;

import com.practice.demo.entity.member.Role;
import com.practice.demo.entity.member.RoleType;
import com.practice.demo.exception.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @Autowired RoleRepository roleRepository;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void before(){
        roleRepository.deleteAllInBatch();
    }

    @Test
    void 생성조회(){
        //given
        Role role = createRole();

        //when
        roleRepository.save(role);
        clear();

        //then
        Role foundRole = roleRepository.findById(role.getId()).orElseThrow(RoleNotFoundException::new);
        assertThat(foundRole.getId()).isEqualTo(role.getId());
    }

    @Test
    void 삭제(){
        //given
        Role role = createRole();
        roleRepository.save(role);
        clear();

        //when
        roleRepository.delete(role);
        clear();

        //then
        assertThatThrownBy(()->roleRepository.findById(role.getId()).orElseThrow(RoleNotFoundException::new))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void 유일성테스트(){
        //given
        Role role = createRole();
        roleRepository.save(role);
        clear();

        //when && then
        assertThatThrownBy(()->roleRepository.save(createRole()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Role createRole() {
        return new Role(RoleType.ROLE_NORMAL);
    }

    private void clear() {
        em.flush();
        em.clear();
    }

}
