package me.contrapost.gameRest.rest;


import io.swagger.annotations.ApiParam;
import me.contrapost.gameCommands.dto.AnswerDTO;
import me.contrapost.gameCommands.dto.GameDTO;
import me.contrapost.gameCommands.hystrix.GameApiCall;
import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.QuizWithCorrectAnswerDTO;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class GameRestImpl implements GameRest{

    @Context
    UriInfo uriInfo;

    private final String quizApiWebAddress;

    public GameRestImpl() {

        quizApiWebAddress = System.getProperty("quizApiAddress", "quizApi.com/quiz/api/");
    }

    @Override
    public synchronized Response getRandomGame() {

        String address = "http://" + quizApiWebAddress + "/quizzes/random";

        Response response = new GameApiCall(address).execute();

        QuizDTO quizDTO = response.readEntity(QuizDTO.class);

        GameDTO gameDTO = new GameDTO(quizDTO.id, quizDTO.question, quizDTO.answerList);

        return Response.status(200)
                .entity(gameDTO)
                .build();
    }

    @Override
    public synchronized Response checkAnswer(@ApiParam("Unique id of the quiz") String id,
                                             @ApiParam("Answer") String index) {

        String address = "http://" + quizApiWebAddress + "/quizzes/answer?quizId=" + id;

        Response response = new GameApiCall(address).execute();

        QuizWithCorrectAnswerDTO quizDto = response.readEntity(QuizWithCorrectAnswerDTO.class);

        boolean answer = false;

        int indexAsNumber;
        try {
            indexAsNumber = Integer.parseInt(index);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Index of correct answer isn't numeric", 400);
        }

        if(indexAsNumber == quizDto.indexOfCorrectAnswer) answer = true;

        AnswerDTO answerDTO = new AnswerDTO(answer);

        return Response.status(201)
                .entity(answerDTO)
                .build();
    }
}
