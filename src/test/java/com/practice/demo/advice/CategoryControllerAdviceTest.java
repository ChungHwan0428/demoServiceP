package com.practice.demo.advice;

import com.practice.demo.controller.category.CategoryController;
import com.practice.demo.exception.CannotConvertNestedStructureException;
import com.practice.demo.exception.CategoryNotFoundException;
import com.practice.demo.service.category.CategoryService;
import org.hibernate.cfg.CannotForceNonNullableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerAdviceTest {

    @InjectMocks
    CategoryController categoryController;
    @Mock
    CategoryService categoryService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).setControllerAdvice(new ExceptionAdvice()).build();
    }

    @Test
    void readAllTest () throws Exception{
        //given
        given(categoryService.readAll()).willThrow(CannotConvertNestedStructureException.class);

        //when
        //then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(-1011));
    }

    @Test
    void deleteTest () throws Exception{
        //given
        //return이 있는지에 따라 테스트 작성 방식이 달라진다.
//        given(categoryService.delete(anyLong())).willThrow(CategoryNotFoundException.class);
        doThrow(CategoryNotFoundException.class).when(categoryService).delete(anyLong());

        //when
        //then
        mockMvc.perform(delete("/api/categories/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1010));
    }
}
