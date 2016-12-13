package me.contrapost.gameRest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/games" , description = "Handling of creating and retrieving games")
@Path("/games")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8; version=1")
public interface GameRest {

    @ApiOperation("Get a random game")
    @GET
    @Path("/random")
    Response getRandomGame();

    @ApiOperation("Get a game specified by id")
    @POST
    Response getAllGame(@ApiParam("Unique id of the quiz")
                        @QueryParam("quizId")
                                String id,
                        @ApiParam("Answer")
                        @QueryParam("answerIndex")
                                int index);
}
