package me.contrapost.gameSoap;

import me.contrapost.gameCommands.dto.AnswerDTO;
import me.contrapost.gameCommands.dto.GameDTO;
import me.contrapost.gameCommands.hystrix.GameApiCall;
import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.QuizWithCorrectAnswerDTO;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@WebService(
        endpointInterface = "me.contrapost.gameSoap.GameSoapApi"
)
public class GameSoapImpl implements GameSoapApi {

    @Context
    UriInfo uriInfo;

    private final String quizApiWebAddress;

    public GameSoapImpl() {

        quizApiWebAddress = System.getProperty("quizApiAddress", "quizApi.com/quiz/api/");
    }

    @Override
    public GameDTO getRandomGame() {
        String address = "http://" + quizApiWebAddress + "/quizzes/random";

        Response response = new GameApiCall(address).execute();

        QuizDTO quizDTO = response.readEntity(QuizDTO.class);

        return new GameDTO(quizDTO.id, quizDTO.question, quizDTO.answerList);
    }

    @Override
    public AnswerDTO checkAnswer(String quizId, String answerIndex) {
        String address = "http://" + quizApiWebAddress + "/quizzes/answer?quizId=" + quizId;

        Response response = new GameApiCall(address).execute();

        QuizWithCorrectAnswerDTO quizDto = response.readEntity(QuizWithCorrectAnswerDTO.class);

        boolean answer = false;

        int indexAsNumber;
        try {
            indexAsNumber = Integer.parseInt(answerIndex);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Index of correct answer isn't numeric", 400);
        }

        if(indexAsNumber == quizDto.indexOfCorrectAnswer) answer = true;

        return new AnswerDTO(answer);
    }
}
