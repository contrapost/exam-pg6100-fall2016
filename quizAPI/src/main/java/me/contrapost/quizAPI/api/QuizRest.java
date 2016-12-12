package me.contrapost.quizAPI.api;

import io.swagger.annotations.*;
import io.swagger.jaxrs.PATCH;
import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.QuizWithCorrectAnswerDTO;
import me.contrapost.quizAPI.dto.collection.ListDTO;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Api(description = "Handling of creating and retrieving categories and subcategories")
@Produces(Formats.JSON_V1)
@Path("/quizzes")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public interface QuizRest {

    @ApiOperation("Get all quizzes")
    @GET
    @Produces(Formats.HAL_V1)
    ListDTO<QuizDTO> getAllQuizzes(@ApiParam("The numeric id of the subcategory all quizzes belongs to")
                                   @QueryParam("filter")
                                           Long id,
                                   @ApiParam("Offset in the list of news")
                                   @QueryParam("offset")
                                   @DefaultValue("0")
                                           Integer offset,
                                   @ApiParam("Limit of news in a single retrieved page")
                                   @QueryParam("limit")
                                   @DefaultValue("10")
                                           Integer limit);

    @ApiOperation("Create a new quiz")
    @POST
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created specifying category")
    Long createQuiz(
            @ApiParam("Question, set of answers as a List<String> and " +
                    "id of subcategory the quiz belongs to. Should not specify quiz id.")
                    QuizWithCorrectAnswerDTO dto);

    @ApiOperation("Get a single quiz specified by id")
    @GET
    @Path("/{id}")
    QuizDTO getQuizById(
            @ApiParam("The numeric id of the quiz")
            @PathParam("id")
                    Long id);

    @ApiOperation("Delete a quiz with the given id")
    @DELETE
    @Path("/{id}")
    void deleteQuiz(
            @ApiParam("The numeric id of the quiz")
            @PathParam("id")
                    Long id);

    @ApiOperation("Modify the quiz using JSON Merge Patch")
    @Path("/{id}")
    @PATCH
    @Consumes(Formats.JSON_MERGE_V1)
    void mergePatchQuiz(@ApiParam("The numeric id of the quiz")
                        @PathParam("id")
                                Long id,
                        @ApiParam("The partial patch")
                                String jsonPatch);

    @ApiOperation("Get a random quiz.")
    @ApiResponses({
            @ApiResponse(code = 307, message = "Temporary redirect."),
            @ApiResponse(code = 404, message = "There are no quizzes yet.")
    })
    @GET
    @Path(("/random"))
    Response getRandomQuiz();
}
