package com.practice.demo.entity.category;

import com.practice.demo.entity.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.REMOVE)
    private List<Category> children;

    @OneToMany(mappedBy = "category",cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<Post> posts;

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }
}
