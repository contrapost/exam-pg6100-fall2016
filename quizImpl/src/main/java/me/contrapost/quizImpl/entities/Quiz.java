package me.contrapost.quizImpl.entities;

import me.contrapost.quizImpl.Validation.AnswerIndex;
import me.contrapost.quizImpl.Validation.AnswerList;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Quiz {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long subcategoryId;

    @NotBlank
    @Column(unique = true)
    @Size(max = 100)
    private String question;

    @NotNull
    @AnswerList
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> answers;

    @NotNull
    @AnswerIndex
    private int correctAnswerIndex;

    public Quiz() {
    }

    public Long getId() {        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }
}
