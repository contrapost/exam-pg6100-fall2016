package me.contrapost.quizAPI.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("A quiz with specified correct answer")
public class QuizWithCorrectAnswerDTO {

    @ApiModelProperty("The id of the quiz")
    public String id;

    @ApiModelProperty("The quiz question")
    public String question;

    @ApiModelProperty("Id of the specifying category the quiz belongs to")
    public String specifyingCategoryId;

    @ApiModelProperty("The set of answers marked with false and true")
    public List<String> answerList;

    @ApiModelProperty("The index of correct answer (0-3)")
    public int indexOfCorrectAnswer;

    public QuizWithCorrectAnswerDTO() {
    }

    public QuizWithCorrectAnswerDTO(String id, String question, String categoryId, List<String> answers, int indexOfCorrectAnswer) {
        this.id = id;
        this.question = question;
        specifyingCategoryId = categoryId;
        answerList = answers;
        this.indexOfCorrectAnswer = indexOfCorrectAnswer;
    }
}
