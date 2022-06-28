package com.practice.demo.controller.post;

import com.practice.demo.dto.post.PostCreateRequest;
import com.practice.demo.dto.sign.SignInResponse;
import com.practice.demo.entity.category.Category;
import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.post.Post;
import com.practice.demo.exception.MemberNotFoundException;
import com.practice.demo.exception.PostNotFoundException;
import com.practice.demo.init.TestInitDB;
import com.practice.demo.repository.category.CategoryRepository;
import com.practice.demo.repository.member.MemberRepository;
import com.practice.demo.repository.post.PostRepository;
import com.practice.demo.service.post.PostService;
import com.practice.demo.service.sign.SignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.practice.demo.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static com.practice.demo.factory.dto.SignInRequestFactory.createSingInRequest;
import static com.practice.demo.factory.entity.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class PostControllerIntegrationTest {
    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @Autowired
    TestInitDB initDB;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    SignService signService;

    @Autowired
    PostService postService;

    @PersistenceContext
    EntityManager em;

    Member member1,member2,admin;
    Category category;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
        member1 = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findByEmail(initDB.getMember2Email()).orElseThrow(MemberNotFoundException::new);
        admin = memberRepository.findByEmail(initDB.getAdminEmail()).orElseThrow(MemberNotFoundException::new);

        category = categoryRepository.findAll().get(0);
    }

    @Test
    void createTest() throws Exception {
        // given
        SignInResponse signInRes = signService.signIn(createSingInRequest(member1.getEmail(), initDB.getPassword()));
        PostCreateRequest req = createPostCreateRequest("title", "content", 1000L, member1.getId(), category.getId(), List.of());

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("title");
        assertThat(post.getContent()).isEqualTo("content");
        assertThat(post.getMember().getId()).isEqualTo(member1.getId()); // 1
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception {
        // given
        PostCreateRequest req = createPostCreateRequest("title", "content", 1000L, member1.getId(), category.getId(), List.of());

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void readTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member1, category));

        // when, then
        mockMvc.perform(
                        get("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByResourceOwnerTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member1, category));
        em.flush();
        em.clear();

        SignInResponse signInRes = signService.signIn(createSingInRequest(member1.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", post.getId())
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> postService.read(post.getId())).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteByAdminTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member1, category));
        SignInResponse adminSignInRes = signService.signIn(createSingInRequest(admin.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", post.getId())
                                .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> postService.read(post.getId())).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member1, category));
        SignInResponse notOwnerSignInRes = signService.signIn(createSingInRequest(member2.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", post.getId())
                                .header("Authorization", notOwnerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member1, category));

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", post.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void updateByResourceOwnerTest() throws Exception {
        // given
        SignInResponse signInRes = signService.signIn(createSingInRequest(member1.getEmail(), initDB.getPassword()));
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updatedContent = "updatedContent";
        Long updatedPrice = 1234L;

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", post.getId())
                                .param("title", updatedTitle)
                                .param("content", updatedContent)
                                .param("price", String.valueOf(updatedPrice))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        assertThat(updatedPost.getTitle()).isEqualTo(updatedTitle);
        assertThat(updatedPost.getContent()).isEqualTo(updatedContent);
        assertThat(updatedPost.getPrice()).isEqualTo(updatedPrice);
    }

    @Test
    void updateByAdminTest() throws Exception {
        // given
        SignInResponse adminSignInRes = signService.signIn(createSingInRequest(admin.getEmail(), initDB.getPassword()));
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updatedContent = "updatedContent";
        Long updatedPrice = 1234L;

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", post.getId())
                                .param("title", updatedTitle)
                                .param("content", updatedContent)
                                .param("price", String.valueOf(updatedPrice))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        assertThat(updatedPost.getTitle()).isEqualTo(updatedTitle);
        assertThat(updatedPost.getContent()).isEqualTo(updatedContent);
        assertThat(updatedPost.getPrice()).isEqualTo(updatedPrice);
    }

    @Test
    void updateUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updatedContent = "updatedContent";
        Long updatedPrice = 1234L;

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", post.getId())
                                .param("title", updatedTitle)
                                .param("content", updatedContent)
                                .param("price", String.valueOf(updatedPrice))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void updateAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        SignInResponse notOwnerSignInRes = signService.signIn(createSingInRequest(member2.getEmail(), initDB.getPassword()));
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updatedContent = "updatedContent";
        Long updatedPrice = 1234L;

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", post.getId())
                                .param("title", updatedTitle)
                                .param("content", updatedContent)
                                .param("price", String.valueOf(updatedPrice))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("Authorization", notOwnerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }
}
