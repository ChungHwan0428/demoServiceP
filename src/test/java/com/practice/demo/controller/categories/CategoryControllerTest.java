package com.practice.demo.controller.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.demo.controller.category.CategoryController;
import com.practice.demo.dto.category.CategoryCreateRequest;
import com.practice.demo.service.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.practice.demo.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;
    @Mock
    CategoryService categoryService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void before(){
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void readAllTest () throws Exception{
        //given
        //when
        //then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());

        verify(categoryService).readAll();
    }

    @Test
    void createTest () throws Exception{
        //given
        CategoryCreateRequest request = createCategoryCreateRequest();

        //when
        //then
        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(categoryService).create(request);
    }

    @Test
    void deleteTest () throws Exception{
        //given
        Long id = 1L;
        //when
        //then
        mockMvc.perform(delete("/api/categories/{id}",id)).andExpect(status().isOk());

        verify(categoryService).delete(id);
    }

}
