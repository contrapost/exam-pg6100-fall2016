package me.contrapost.gameCommands.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

    @XmlElement
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
