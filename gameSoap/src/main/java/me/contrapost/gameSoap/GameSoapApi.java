package me.contrapost.gameSoap;

import me.contrapost.gameCommands.dto.AnswerDTO;
import me.contrapost.gameCommands.dto.GameDTO;

import javax.jws.WebService;

@WebService(name = "GameSoap")
public interface GameSoapApi {

    GameDTO getRandomGame();

    AnswerDTO checkAnswer(String quizId, String answerIndex);
}
