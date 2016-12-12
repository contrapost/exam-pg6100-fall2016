package me.contrapost.quizAPI.api;

import io.swagger.annotations.*;
import io.swagger.jaxrs.PATCH;
import me.contrapost.quizAPI.dto.CategoryDTO;
import me.contrapost.quizAPI.dto.SubcategoryDTO;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(description = "Handling of creating and retrieving categories and subcategories")
@Produces(Formats.JSON_V1)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public interface CategoryRest {

    @ApiOperation("Get all categories")
    @GET
    @Path("/categories")
    List<CategoryDTO> getAllCategories(@ApiParam("Whether to retrieve or not subcategories for the given category")
                                                  @QueryParam("expand")
                                                  @DefaultValue("false")
                                                          Boolean expand);

    @ApiOperation("Create a new category")
    @POST
    @Path("/categories")
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created category")
    Long createCategory(
            @ApiParam("Title of a new category. Should not specify id.")
                    CategoryDTO dto);

    @ApiOperation("Get a single category specified by id")
    @GET
    @Path("categories/{id}")
    CategoryDTO getCategoryById(@ApiParam("Whether to retrieve or not subcategories for the given category")
                                        @QueryParam("expand")
                                        @DefaultValue("false")
                                                Boolean expand,
                                        @ApiParam("The numeric id of the category")
                                        @PathParam("id")
                                                Long id);

    @ApiOperation("Delete a category with the given id")
    @DELETE
    @Path("/categories/{id}")
    void deleteCategory(@ApiParam("The numeric id of the category")
                                @PathParam("id")
                                Long id);

    @ApiOperation("Modify the category using JSON Merge Patch")
    @Path("/categories/{id}")
    @PATCH
    @Consumes(Formats.JSON_MERGE_V1)
    void mergePatchCategory(@ApiParam("The unique id of the category")
                                @PathParam("id")
                                        Long id,
                                @ApiParam("The partial patch")
                                        String jsonPatch);

    @ApiOperation("Get all subcategories of the category specified by id")
//    @ApiResponses({
//            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
//    })
    @GET
    @Path("/categories/{id}/subcategories")
 //   @Deprecated
    Response deprecatedGetAllSubCategoriesForRootCategory(@ApiParam("The unique id of the category")
                                                          @PathParam("id")
                                                                  Long id);

    @ApiOperation("Create a subcategory of the category specified by id")
    @POST
    @Path("/categories/{id}/subcategories")
    Response createSubcategoriesForCategory(@ApiParam("The unique id of the category")
                                                          @PathParam("id")
                                                                  Long id);

    @ApiOperation("Get all subcategories")
    @GET
    @Path("/subcategories")
    List<SubcategoryDTO> getAllSubcategories(@ApiParam("Set of subcategories belonging to a category with specified id")
                                             @QueryParam("parentId")
                                                     Long id);

    @ApiOperation("Get a single category specified by id")
    @GET
    @Path("categories/{id}")
    SubcategoryDTO getSubcategoryById(@ApiParam("The numeric id of the subcategory")
                                                @PathParam("id")
                                                 Long id);
}
