package com.practice.demo.service.post;

import com.practice.demo.dto.post.*;
import com.practice.demo.entity.post.Image;
import com.practice.demo.entity.post.Post;
import com.practice.demo.exception.PostNotFoundException;
import com.practice.demo.repository.category.CategoryRepository;
import com.practice.demo.repository.member.MemberRepository;
import com.practice.demo.repository.post.PostRepository;
import com.practice.demo.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Transactional
    public PostCreateResponse create(PostCreateRequest request){
        Post post = postRepository.save(PostCreateRequest.toEntity(request,memberRepository,categoryRepository));
        uploadImage(post.getImages(), request.getImages());
        return new PostCreateResponse(post.getId());
    }

    @Transactional
    public void delete(Long id){
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        deleteImages(post.getImages());
        postRepository.delete(post);
    }

    @Transactional
    public PostUpdateResponse update(Long id, PostUpdateRequest req) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Post.ImageUpdatedResult result = post.update(req);
        uploadImages(result.getAddedImages(), result.getAddedImageFiles());
        deleteImages(result.getDeletedImages());
        return new PostUpdateResponse(id);
    }

    private void uploadImages(List<Image> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deleteImages(List<Image> images) {
        images.stream().forEach(i -> fileService.delete(i.getUniqueName()));
    }

    private void uploadImage(List<Image> images, List<MultipartFile> fileImages){
        IntStream.range(0,images.size()).forEach(i->fileService.upload(fileImages.get(i),images.get(i).getUniqueName()));
    }

    public PostDto read(Long id){
        return PostDto.toDto(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
    }

}
