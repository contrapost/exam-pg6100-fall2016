package me.contrapost.gameCommands.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("A game")
public class GameDTO {

    @ApiModelProperty("The id of the quiz")
    public String quizId;

    @ApiModelProperty("The question")
    public String question;

    @ApiModelProperty("The set of answers marked with false and true")
    public List<String> answerList;

    public GameDTO() {
    }

    public GameDTO(String quizId, String question, List<String> answers) {
        this.quizId = quizId;
        this.question = question;
        answerList = answers;
    }
}
