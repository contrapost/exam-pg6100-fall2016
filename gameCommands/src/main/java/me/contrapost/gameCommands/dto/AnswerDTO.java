package me.contrapost.gameCommands.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ApiModel("An answer for the quiz")
public class AnswerDTO {

    @XmlElement
    @ApiModelProperty("The boolean result reflects if the answer was correct")
    public Boolean isCorrect;

    public AnswerDTO() {
    }

    public AnswerDTO(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
