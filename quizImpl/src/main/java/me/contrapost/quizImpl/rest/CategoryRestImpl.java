package me.contrapost.quizImpl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.swagger.annotations.ApiParam;
import me.contrapost.quizAPI.api.CategoryRest;
import me.contrapost.quizAPI.dto.CategoryDTO;
import me.contrapost.quizAPI.dto.SubcategoryDTO;
import me.contrapost.quizImpl.ejb.CategoryEJB;
import me.contrapost.quizImpl.entities.Subcategory;
import me.contrapost.quizImpl.rest.converters.CategoryConverter;
import me.contrapost.quizImpl.rest.converters.SubcategoryConverter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.sql.SQLException;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CategoryRestImpl implements CategoryRest {

    @EJB
    private CategoryEJB categoryEJB;

    @Override
    public List<CategoryDTO> getAllCategories(Boolean expand) {

        if(expand == null) expand = false;

        return CategoryConverter.transform(categoryEJB.getAllCategories(expand), expand);
    }

    @Override
    public Long createCategory(@ApiParam("Title of a new category. Should not specify id.") CategoryDTO dto) {

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated root category", 400);
        }

        Long id;
        try {
            id = categoryEJB.createCategory(dto.title);
        } catch (Exception e) {
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public CategoryDTO getCategoryById(@ApiParam("Whether to retrieve or not subcategories for the given category")
                                       @DefaultValue("false") Boolean expand,
                                       @ApiParam("The numeric id of the category") Long id) {

        requireCategoryExists(id);

        if(expand == null) expand = false;
        return CategoryConverter.transform(categoryEJB.getCategory(id, expand), expand);
    }

    @Override
    public void deleteCategory(@ApiParam("The numeric id of the category") Long id) {

        requireCategoryExists(id);

        categoryEJB.deleteCategory(id);
    }

    @Override
    public void mergePatchCategory(@ApiParam("The unique id of the category") Long id,
                                   @ApiParam("The partial patch") String jsonPatch) {

        CategoryDTO dto = CategoryConverter.transform(categoryEJB.getCategory(id));

        if (dto == null) {
            throw new WebApplicationException("Cannot find category with id " + id, 404);
        }

        ObjectMapper jackson = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode.class);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid JSON data as input: " + e.getMessage(), 400);
        }

        if (jsonNode.has("id")) {
            throw new WebApplicationException(
                    "Cannot modify the root category id from " + id + " to " + jsonNode.get("id"), 409);
        }

        String newTitle = dto.title;

        if (jsonNode.has("title")) {
            JsonNode titleNode = jsonNode.get("title");
            if (titleNode.isNull()) {
                newTitle = null;
            } else if (titleNode.isTextual()) {
                newTitle = titleNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        categoryEJB.updateCategoryTitle(id, newTitle);
    }

    @Override
    public Response getAllSubCategoriesForRootCategory(@ApiParam("The unique id of the category") Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("categories/subcategories?parentId=" + id)
                        .build())
                .build();
    }

    @Override
    public Long createSubcategoriesForCategory(@ApiParam("Title of a new subcategory. Should not specify id.")
                                                       SubcategoryDTO dto,
                                               @ApiParam("The unique id of the category") Long parentId) {

        requireCategoryExists(parentId);

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated root category", 400);
        }

        Long id;
        try {
            id = categoryEJB.createSubcategory(dto.title, parentId);
        } catch (Exception e) {
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public List<SubcategoryDTO> getAllSubcategories(@ApiParam("Set of subcategories belonging to a category with specified id")
                                                            Long id) {
        List<Subcategory> list;

        if(id != null) {
            requireCategoryExists(id);
            list = categoryEJB.getAllSubcategoriesForParent(id);
        } else {
            list = categoryEJB.getAllSubcategories();
        }
        return SubcategoryConverter.transform(list);
    }

    @Override
    public SubcategoryDTO getSubcategoryById(@ApiParam("The numeric id of the subcategory") Long id) {
        if (categoryEJB.getSubcategory(id) == null) {
            throw new WebApplicationException("Cannot find category with id: " + id, 404);
        }

        return SubcategoryConverter.transform(categoryEJB.getSubcategory(id));
    }

    private WebApplicationException wrapException(Exception e) throws WebApplicationException {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        Throwable cause = Throwables.getRootCause(e);
        if (cause instanceof ConstraintViolationException || cause instanceof SQLException) {
            return new WebApplicationException("Invalid constraints on input: " + cause.getMessage(), 400);
        } else {
            return new WebApplicationException("Internal error", 500);
        }
    }

    private void requireCategoryExists(long id) throws WebApplicationException {
        if (categoryEJB.getCategory(id) == null) {
            throw new WebApplicationException("Cannot find category with id: " + id, 404);
        }
    }
}