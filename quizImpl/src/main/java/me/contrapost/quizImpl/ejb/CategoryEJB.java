package me.contrapost.quizImpl.ejb;

import me.contrapost.quizImpl.entities.Category;
import me.contrapost.quizImpl.entities.Subcategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    public boolean updateCategoryTitle(@NotNull long categoryId, @NotNull String newTitle) {
        Category category = em.find(Category.class, categoryId);
        if (category == null) return false;
        category.setTitle(newTitle);
        return true;
    }

    public Category getCategory(@NotNull long id){
        return em.find(Category.class, id);
    }

    public Category getCategory(@NotNull long id, boolean expand){
        Category categories = getCategory(id);
        categories.getSubcategories().size();
        return categories;
    }

    public Subcategory getSubcategory(@NotNull long id){
        return em.find(Subcategory.class, id);
    }


    @SuppressWarnings("unchecked")
    public List<Category> getAllCategories(Boolean expand) {
        List<Category> list = em.createQuery("select c from Category c").getResultList();

        if(expand) list.forEach(category -> category.getSubcategories().size());

        return list;
    }

    @SuppressWarnings("unchecked")
    public List<Subcategory> getAllSubcategories() {
        return em.createQuery("select s from Subcategory s").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Subcategory> getAllSubcategoriesForParent(Long parentId) {

        return em.createQuery("select s from Subcategory s where s.parentCategory.id = :id")
                .setParameter("id", parentId)
                .getResultList();
    }
}