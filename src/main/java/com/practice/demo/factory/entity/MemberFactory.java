package com.practice.demo.factory.entity;

//해당 팩토리 클래스는 DTO 인스턴스를 생성해주는 역할을 한다.

import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.member.Role;

import java.util.Collections;
import java.util.List;

public class MemberFactory {

    public static Member createMember(){
        return new Member("email@naver.com","123456a!","username","nickname", Collections.emptyList());
    }

    public static Member createMember(String email,String password, String username, String nickname){
        return new Member(email,password,username,nickname,Collections.emptyList());
    }

    public static Member createMemberWithRoles(List<Role> roles){
        return new Member("email@naver.com","123456a!","username","nickname",roles);
    }
}
