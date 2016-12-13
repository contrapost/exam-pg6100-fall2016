package me.contrapost.gameCommands.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("An answer for the quiz")
public class AnswerDTO {

    @ApiModelProperty("The boolean result reflects if the answer was correct")
    public Boolean isCorrect;

    public AnswerDTO() {
    }

    public AnswerDTO(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
