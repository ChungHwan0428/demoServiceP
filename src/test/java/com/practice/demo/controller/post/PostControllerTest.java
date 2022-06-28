package com.practice.demo.controller.post;

import com.practice.demo.dto.post.PostCreateRequest;
import com.practice.demo.dto.post.PostUpdateRequest;
import com.practice.demo.exception.PostNotFoundException;
import com.practice.demo.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.practice.demo.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static com.practice.demo.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @InjectMocks
    PostController postController;
    @Mock
    PostService postService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createTest() throws Exception{
        // given
        // 1
        ArgumentCaptor<PostCreateRequest> postCreateRequestArgumentCaptor = ArgumentCaptor.forClass(PostCreateRequest.class);

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
        );
        PostCreateRequest req = createPostCreateRequestWithImages(imageFiles);

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .file("images", imageFiles.get(0).getBytes()) // 2
                                .file("images", imageFiles.get(1).getBytes())
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> { // 3
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)) // 4
                .andExpect(status().isCreated());

        verify(postService).create(postCreateRequestArgumentCaptor.capture()); // 5

        PostCreateRequest capturedRequest = postCreateRequestArgumentCaptor.getValue(); // 6
        assertThat(capturedRequest.getImages().size()).isEqualTo(2);
    }

    @Test
    void readTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/posts/{id}", id))
                .andExpect(status().isOk());
        verify(postService).read(id);
    }

    @Test
    void deleteTest () throws Exception{
        //given
        Long id = 1L;
        //when
        //then
        mockMvc.perform(delete("/api/posts/{id}",id))
                .andExpect(status().isOk());
        verify(postService).delete(id);
    }

    @Test
    void updateTest() throws Exception{
        // given
        ArgumentCaptor<PostUpdateRequest> postUpdateRequestArgumentCaptor = ArgumentCaptor.forClass(PostUpdateRequest.class);

        List<MultipartFile> addedImages = List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
        );
        List<Long> deletedImages = List.of(1L, 2L);

        PostUpdateRequest req = createPostUpdateRequest("title", "content", 1234L, addedImages, deletedImages);

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", 1L)
                                .file("addedImages", addedImages.get(0).getBytes())
                                .file("addedImages", addedImages.get(1).getBytes())
                                .param("deletedImages", String.valueOf(deletedImages.get(0)), String.valueOf(deletedImages.get(1)))
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
        verify(postService).update(anyLong(), postUpdateRequestArgumentCaptor.capture());

        PostUpdateRequest capturedRequest = postUpdateRequestArgumentCaptor.getValue();
        List<MultipartFile> capturedAddedImages = capturedRequest.getAddedImages();
        assertThat(capturedAddedImages.size()).isEqualTo(2);

        List<Long> capturedDeletedImages = capturedRequest.getDeletedImages();
        assertThat(capturedDeletedImages.size()).isEqualTo(2);
        assertThat(capturedDeletedImages).contains(deletedImages.get(0), deletedImages.get(1));
    }
}
