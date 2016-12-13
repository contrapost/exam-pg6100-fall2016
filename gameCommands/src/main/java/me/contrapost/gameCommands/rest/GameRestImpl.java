package me.contrapost.gameCommands.rest;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.swagger.annotations.ApiParam;
import me.contrapost.gameCommands.dto.AnswerDTO;
import me.contrapost.gameCommands.dto.GameDTO;
import me.contrapost.gameRest.GameRest;
import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.QuizWithCorrectAnswerDTO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

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

        Response response = new CallGameApi(address).execute();

        QuizDTO quizDTO = response.readEntity(QuizDTO.class);

        GameDTO gameDTO = new GameDTO(quizDTO.id, quizDTO.question, quizDTO.answerList);

        return Response.status(200)
                .entity(gameDTO)
                .build();
    }

    @Override
    public synchronized Response checkAnswer(@ApiParam("Unique id of the quiz") String id,
                                             @ApiParam("Answer") int index) {

        String address = "http://" + quizApiWebAddress + "/quizzes/answer/?quizId=" + id;

        Response response = new CallGameApi(address).execute();

        QuizWithCorrectAnswerDTO quizDto = response.readEntity(QuizWithCorrectAnswerDTO.class);

        boolean answer = false;

        if(index == quizDto.indexOfCorrectAnswer) answer = true;

        AnswerDTO answerDTO = new AnswerDTO(answer);

        return Response.status(201)
                .entity(answerDTO)
                .build();
    }

    private class CallGameApi extends HystrixCommand<Response> {

        private final String address;

        @SuppressWarnings("WeakerAccess")
        protected CallGameApi(String address) {
            super(HystrixCommandGroupKey.Factory.asKey("Interactions with QuizApi"));
            this.address = address;
        }

        @Override
        protected Response run() throws Exception {

            URI uri = UriBuilder
                    .fromUri(address)
                    .build();
            Client client = ClientBuilder.newClient();

            Response response = client.target(uri)
                    .request("application/json")
                    .get();;

            return response;
        }

        @Override
        protected Response getFallback() {
            //this is what is returned in case of exceptions or timeouts
            return null;
        }
    }
}
