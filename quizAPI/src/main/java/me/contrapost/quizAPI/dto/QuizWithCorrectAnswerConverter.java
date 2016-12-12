package me.contrapost.quizAPI.dto;

import me.contrapost.quizImpl.entities.Quiz;

import java.util.ArrayList;
import java.util.Objects;

public class QuizWithCorrectAnswerConverter {

    private QuizWithCorrectAnswerConverter() {}

    public static QuizWithCorrectAnswerDTO transform(Quiz quiz) {
        Objects.requireNonNull(quiz);

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO();
        dto.id = String.valueOf(quiz.getId());
        dto.question = quiz.getQuestion();
        dto.specifyingCategoryId = String.valueOf(quiz.getSubcategoryId());
        dto.answerList = new ArrayList<>(quiz.getAnswers());
        dto.indexOfCorrectAnswer = quiz.getCorrectAnswerIndex();

        return dto;
    }
}
