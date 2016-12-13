package me.contrapost.gameCommands.rest;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.swagger.annotations.ApiParam;
import me.contrapost.gameCommands.dto.GameDTO;
import me.contrapost.gameRest.GameRest;
import me.contrapost.quizAPI.dto.QuizDTO;

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

        Response response = new CallGameApi(address, Requests.GET_RANDOM_GAME).execute();

        QuizDTO quizDTO = response.readEntity(QuizDTO.class);

        GameDTO gameDTO = new GameDTO(quizDTO.id, quizDTO.question, quizDTO.answerList);

        return null;
    }

    @Override
    public synchronized Response checkAnswer(@ApiParam("Unique id of the quiz") String id, @ApiParam("Answer") int index) {
        return null;
    }

    private class CallGameApi extends HystrixCommand<Response> {

        private final String address;
        private final Requests request;

        @SuppressWarnings("WeakerAccess")
        protected CallGameApi(String address, Requests request) {
            super(HystrixCommandGroupKey.Factory.asKey("Interactions with QuizApi"));
            this.address = address;
            this.request = request;
        }

        @Override
        protected Response run() throws Exception {

            URI uri = UriBuilder
                    .fromUri(address)
                    .build();
            Client client = ClientBuilder.newClient();

            Response response = null;
            switch (request) {
                case GET_RANDOM_GAME:
                    response = client.target(uri)
                            .request("application/json")
                            .post(null);
                    break;
                case CHECK_ANSWER:
                    response = client.target(uri)
                            .request("application/json")
                            .get();
                    break;
            }
            return response;
        }

        @Override
        protected Response getFallback() {
            //this is what is returned in case of exceptions or timeouts
            return null;
        }
    }
}
