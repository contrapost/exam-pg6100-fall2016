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

    public long createCategory(@NotNull String title) {
        Category category = new Category();
        category.setTitle(title);

        em.persist(category);

        return category.getId();
    }

    public boolean deleteCategory(@NotNull long id) {
        if (em.find(Category.class, id) == null) return false;
        em.remove(em.find(Category.class, id));
        return true;
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

    public boolean deleteSubcategory(@NotNull long id){
        Subcategory subCategory = em.find(Subcategory.class, id);
        if (subCategory == null) return false; // Or cast exception
        Category rootCategory = em.find(Category.class, subCategory.getParentCategory().getId());
        rootCategory.getSubcategories().remove(id);
        return true;
    }

    public boolean updateCategoryTitle(@NotNull long categoryId, @NotNull String newTitle) {
        Category category = em.find(Category.class, categoryId);
        if (category == null) return false;
        category.setTitle(newTitle);
        return true;
    }

    public Category getCategory(@NotNull long id){
        return em.find(Category.class, id);
    }

    public Subcategory getSubcategory(@NotNull long id){
        return em.find(Subcategory.class, id);
    }


}
