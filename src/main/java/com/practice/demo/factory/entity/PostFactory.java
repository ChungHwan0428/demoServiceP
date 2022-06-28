package com.practice.demo.factory.entity;

import com.practice.demo.entity.category.Category;
import com.practice.demo.entity.member.Member;
import com.practice.demo.entity.post.Image;
import com.practice.demo.entity.post.Post;

import java.util.List;

import static com.practice.demo.factory.entity.CategoryFactory.createCategory;
import static com.practice.demo.factory.entity.MemberFactory.createMember;

public class PostFactory {

    public static Post createPost(){
        return createPost(createMember(),createCategory());
    }

    public static Post createPost(Member member, Category category){
        return new Post("title","content",1000L,member,category, List.of());
    }

    public static Post createPostWithImages(Member member, Category category, List<Image> images) {
        return new Post("title", "content", 1000L, member, category, images);
    }

    public static Post createPostWithImages(List<Image> images) {
        return new Post("title", "content", 1000L, createMember(), createCategory(), images);
    }
}
