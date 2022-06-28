package com.practice.demo.dto.category;

import com.practice.demo.entity.category.Category;
import com.practice.demo.exception.CategoryNotFoundException;
import com.practice.demo.repository.category.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 명을 입력해주세요")
    @Size(min = 2,max = 20,message = "카테고리 명의 길이는 2글자에서 30글자 입니다.")
    private String name;

    private Long parentId;

    public static Category toEntity(CategoryCreateRequest request, CategoryRepository categoryRepository){
        return new Category(request.getName(),
                Optional.ofNullable(request.getParentId())
                        .map(id -> categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new))
                        .orElse(null));
    }
}
