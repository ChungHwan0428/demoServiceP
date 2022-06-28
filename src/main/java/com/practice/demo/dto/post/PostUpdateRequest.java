package com.practice.demo.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {
    @NotBlank(message = "게시글 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "게시글 본문을 입력해주세요.")
    private String content;

    @NotNull(message = "가격을 입력해주세요.")
    @PositiveOrZero(message = "0원 이상을 입력해주세요")
    private Long price;

    private List<MultipartFile> addedImages = new ArrayList<>();

    private List<Long> deletedImages = new ArrayList<>();

}
