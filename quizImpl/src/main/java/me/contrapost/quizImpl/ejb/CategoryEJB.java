package me.contrapost.quizImpl.ejb;

import me.contrapost.quizImpl.entities.Category;
import me.contrapost.quizImpl.entities.Subcategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

@Stateless
public class CategoryEJB {

    @PersistenceContext
    protected EntityManager em;

    public long createRootCategory(@NotNull String title) {
        Category category = new Category();
        category.setTitle(title);

        em.persist(category);

        return category.getId();
    }

    public long createSubcategory(@NotNull String title, @NotNull long parentCategoryId) {
        Category parentCategory = em.find(Category.class, parentCategoryId);
        if (parentCategory == null) throw new IllegalArgumentException("No such category: " + parentCategoryId);

        Subcategory subCategory = new Subcategory();
        subCategory.setTitle(title);
        subCategory.setParentCategory(parentCategory);

        em.persist(subCategory);

        parentCategory.getSubcategories().put(subCategory.getId(), subCategory);

        return subCategory.getId();
    }

    public Category getCategory(@NotNull long id){
        return em.find(Category.class, id);
    }

    public Subcategory getSubcategory(@NotNull long id){
        return em.find(Subcategory.class, id);
    }


}
