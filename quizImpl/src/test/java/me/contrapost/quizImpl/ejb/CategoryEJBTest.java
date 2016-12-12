package me.contrapost.quizImpl.ejb;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@Ignore
@SuppressWarnings("unused")
@RunWith(Arquillian.class)
public class CategoryEJBTest extends EjbTestBase {

    @Test
    public void testCreateCategory() {
        long subCatId = createSubcategory("Sub", createCategory("Category"));

        assertNotNull(categoryEJB.getSubcategory(subCatId));

    }

    @Test
    public void testDeleteCategory() {

        long categoryId = createCategory("Cat");
        categoryEJB.deleteCategory(categoryId);

        assertNull(categoryEJB.getCategory(categoryId));
    }

    @Test
    public void testCascadeDeleteOfAllSubCategoriesWhenRootIsDeleted() {
        long categoryId = createCategory("Cat");
        long subCatId = createSubcategory("Sub", categoryId);
        long subCatId2 = createSubcategory("Sub2", categoryId);

        categoryEJB.deleteCategory(categoryId);

        assertNull(categoryEJB.getSubcategory(subCatId));
        assertNull(categoryEJB.getSubcategory(subCatId2));
    }

    @Test
    public void testDeleteSubCategory() {
        long categoryId = createCategory("Cat");
        long subCatId = createSubcategory("Sub", categoryId);

        assertTrue(categoryEJB.deleteSubcategory(subCatId));
        assertNull(categoryEJB.getSubcategory(subCatId));
    }

    @Test
    public void testUpdateCategoryTitle() {
        long categoryId = createCategory("Cat");
        long subCatId = createSubcategory("Sub", categoryId);

        String newCategoryName = "New root name";
        assertTrue(categoryEJB.updateCategoryTitle(categoryId, newCategoryName));
        assertEquals(newCategoryName, categoryEJB.getCategory(categoryId).getTitle());
        assertEquals(newCategoryName, categoryEJB.getSubcategory(subCatId).getParentCategory().getTitle());
    }
}
