package com.practice.demo.dto.post;

import com.practice.demo.entity.post.Image;
import com.practice.demo.entity.post.Post;
import com.practice.demo.exception.CategoryNotFoundException;
import com.practice.demo.exception.MemberNotFoundException;
import com.practice.demo.repository.category.CategoryRepository;
import com.practice.demo.repository.member.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "게시글 제목을 입력하세요")
    private String title;

    @NotBlank(message = "게시글 본문을 입력하세요")
    private String content;

    @NotNull(message = "가격을 입력하세요")
    @PositiveOrZero(message = "0원 이상을 입력하세요")
    private Long price;

    @Null
    private Long memberId;

    @NotNull(message = "카테고리 아이디를 입력하세요")
    @PositiveOrZero(message = "올바른 카테고리 아이디를 입력해주세요")
    private Long categoryId;

    private List<MultipartFile> images = new ArrayList<>();

    public static Post toEntity(PostCreateRequest request, MemberRepository memberRepository, CategoryRepository categoryRepository){
        return new Post(request.title,
                request.content,
                request.price,
                memberRepository.findById(request.getMemberId()).orElseThrow(MemberNotFoundException::new),
                categoryRepository.findById(request.getMemberId()).orElseThrow(CategoryNotFoundException::new),
                request.images.stream().map(i->new Image(i.getOriginalFilename())).collect(Collectors.toList()));
    }

}
