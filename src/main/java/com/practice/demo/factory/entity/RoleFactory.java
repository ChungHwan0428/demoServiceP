package com.practice.demo.factory.entity;

import com.practice.demo.entity.member.Role;
import com.practice.demo.entity.member.RoleType;

public class RoleFactory {
    public static Role createRole(){
        return new Role(RoleType.ROLE_NORMAL);
    }
}
