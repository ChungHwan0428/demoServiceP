package com.practice.demo.dto.category;

import com.practice.demo.entity.category.Category;
import com.practice.demo.helper.NestedConvertHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    private String name;
    private List<CategoryDto> children;

    public static List<CategoryDto> toDoList(List<Category> categories){
        NestedConvertHelper helper = NestedConvertHelper.newInstance(
                categories,
                c->new CategoryDto(c.getId(),c.getName(),new ArrayList<>()),
                c->c.getParent(),
                c->c.getId(),
                c->c.getChildren());

        return helper.convert();
    }
}
