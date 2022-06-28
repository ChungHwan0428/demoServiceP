package com.practice.demo.controller.post;

import com.practice.demo.aop.AssignMemberId;
import com.practice.demo.controller.response.Response;
import com.practice.demo.dto.post.PostCreateRequest;
import com.practice.demo.dto.post.PostUpdateRequest;
import com.practice.demo.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId
    public Response create(@Valid @ModelAttribute PostCreateRequest request){
        return Response.success(postService.create(request));
    }

    @GetMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@PathVariable Long id) {
        return Response.success(postService.read(id));
    }

    @DeleteMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@PathVariable Long id){
        postService.delete(id);
        return Response.success();
    }

    @PutMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(
            @PathVariable Long id,
            @Valid @ModelAttribute PostUpdateRequest req) {
        return Response.success(postService.update(id, req));
    }
}
