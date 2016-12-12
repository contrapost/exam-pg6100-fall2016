package me.contrapost.quizImpl.entities;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Size(max = 100)
    private String title;

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parentCategory")
    private Map<Long, Subcategory> subcategories;

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<Long, Subcategory> getSubcategories() {
        if (subcategories == null) subcategories = new HashMap<>();
        return subcategories;
    }

    public void setSubcategories(Map<Long, Subcategory> subCategories) {
        this.subcategories = subCategories;
    }
}
