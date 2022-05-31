package com.practice.demo.repository.role;

import com.practice.demo.entity.member.Role;
import com.practice.demo.entity.member.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByRoleType(RoleType roleType);

}
