package com.practice.demo.controller.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.demo.dto.category.CategoryCreateRequest;
import com.practice.demo.dto.sign.SignInResponse;
import com.practice.demo.entity.category.Category;
import com.practice.demo.init.TestInitDB;
import com.practice.demo.repository.category.CategoryRepository;
import com.practice.demo.repository.member.MemberRepository;
import com.practice.demo.service.sign.SignService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.practice.demo.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static com.practice.demo.factory.dto.SignInRequestFactory.createSingInRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    MockMvc mockMvc;

    @Autowired TestInitDB testInitDB;
    @Autowired CategoryRepository categoryRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired SignService signService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void before(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        testInitDB.initDB();
    }

    @Test
    void readAllTest () throws Exception{
        //given
        //when
        //then
        mockMvc.perform(get("/api/categories")).andExpect(status().isOk());
    }

    @Test
    void createTest () throws Exception{
        //given
        CategoryCreateRequest request = createCategoryCreateRequest();
        SignInResponse adminSignInInRes = signService.signIn(createSingInRequest(testInitDB.getAdminEmail(), testInitDB.getPassword()));
        int beforeSize = categoryRepository.findAll().size();

        //when
        //then
        mockMvc.perform(post("/api/categories")
                .header("Authorization",adminSignInInRes.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(beforeSize+1);
    }

    @Test
    void createUnauthorizedByNoneTokenTest () throws Exception{
        //given
        CategoryCreateRequest request = createCategoryCreateRequest();

        //when
        //then
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void createAccessDeniedByNormalMemberTest () throws Exception{
        //given
        CategoryCreateRequest request = createCategoryCreateRequest();
        SignInResponse signInResponse = signService.signIn(createSingInRequest(testInitDB.getMember1Email(), testInitDB.getPassword()));

        //when
        //then
        mockMvc.perform(post("/api/categories")
                .header("Authorization",signInResponse.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void deleteTest () throws Exception{
        // given
        Long id = categoryRepository.findAll().get(0).getId();
        SignInResponse adminSignInRes = signService.signIn(createSingInRequest(testInitDB.getAdminEmail(), testInitDB.getPassword()));

        // when, then
        mockMvc.perform(delete("/api/categories/{id}", id)
                        .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());

        List<Category> result = categoryRepository.findAll();
//        System.out.println(result.get(0).getName() + " " + result.get(0).getParent().getName()+"------------------------\n\n");
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest () throws Exception{
        //given
        Long id = categoryRepository.findAll().get(0).getId();

        //when
        //then
        mockMvc.perform(delete("/api/categories/{id}",id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void deleteAccessDeniedByNormalMemberTest () throws Exception{
        //given
        Long id = categoryRepository.findAll().get(0).getId();
        SignInResponse signInResponse = signService.signIn(createSingInRequest(testInitDB.getMember1Email(), testInitDB.getPassword()));

        //when
        //then
        mockMvc.perform(delete("/api/categories/{id}",id)
                .header("Authorization",signInResponse.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }
}
