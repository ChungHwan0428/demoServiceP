package com.practice.demo.service.category;

import com.practice.demo.dto.category.CategoryCreateRequest;
import com.practice.demo.dto.category.CategoryDto;
import com.practice.demo.exception.CategoryNotFoundException;
import com.practice.demo.factory.entity.CategoryFactory;
import com.practice.demo.repository.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.practice.demo.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static com.practice.demo.factory.entity.CategoryFactory.createCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks CategoryService categoryService;
    @Mock
    CategoryRepository categoryRepository;

    @Test
    void readAllTest (){
        //given
        given(categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc()).willReturn(
                List.of(CategoryFactory.createCategoryWithName("name1"),
                        CategoryFactory.createCategoryWithName("name2")
                        )
        );

        //when
        List<CategoryDto> result = categoryService.readAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("name1");
        assertThat(result.get(1).getName()).isEqualTo("name2");

    }

    @Test
    void createTest (){
        //given
        CategoryCreateRequest request = createCategoryCreateRequest();

        //when
        categoryService.create(request);

        //then
        verify(categoryRepository).save(any());
    }

    @Test
    void deleteTest (){
        //given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));

        //when
        categoryService.delete(1L);

        //then
        verify(categoryRepository).delete(any());
    }

    @Test
    void deleteExceptionByCategoryNotFoundTest (){
        //given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        //when
        //then
        assertThatThrownBy(()->categoryService.delete(1L)).isInstanceOf(CategoryNotFoundException.class);
    }

}
