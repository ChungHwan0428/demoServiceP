package com.practice.demo.controller.member;

import com.practice.demo.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @InjectMocks MemberController memberController;
    @Mock
    MemberService memberService;

    MockMvc mockMvc;

    @BeforeEach
    void before(){
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    void readTest() throws Exception{
        //given
        Long id = 1L;

        //when, then
        mockMvc.perform(get("/api/members/{id}",id))
                .andExpect(status().isOk());

        verify(memberService).read(id);
    }

    @Test
    void deteleTest() throws Exception{
        //given
        Long id = 1L;

        //when,then
        mockMvc.perform(delete("/api/members/{id}",id))
                .andExpect(status().isOk());

        verify(memberService).delete(id);
    }

}
