package me.contrapost.quizAPI.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("A quiz")
public class QuizDTO {

    @ApiModelProperty("The id of the quiz")
    public String id;

    @ApiModelProperty("The quiz question")
    public String question;

    @ApiModelProperty("Id of the specifying category the quiz belongs to")
    public String subcategoryId;

    @ApiModelProperty("The set of answers marked with false and true")
    public List<String> answerList;

    public QuizDTO() {
    }

    public QuizDTO(String id, String question, String categoryId, List<String> answers) {
        this.id = id;
        this.question = question;
        subcategoryId = categoryId;
        answerList = answers;
    }
}
