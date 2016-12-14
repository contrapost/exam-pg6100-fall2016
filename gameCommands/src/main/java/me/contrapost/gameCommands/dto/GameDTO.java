package me.contrapost.gameCommands.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ApiModel("A game")
public class GameDTO {

    @XmlElement
    @ApiModelProperty("The id of the quiz")
    public String quizId;

    @XmlElement
    @ApiModelProperty("The question")
    public String question;

    @XmlElementWrapper(name = "answerList")
    @XmlElement(name = "answer")
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
