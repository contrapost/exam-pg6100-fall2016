package me.contrapost.quizImpl.rest.converters;

import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.collection.ListDTO;
import me.contrapost.quizImpl.entities.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuizConverter {

    private QuizConverter() {}

    public static QuizDTO transform(Quiz quiz) {
        Objects.requireNonNull(quiz);

        QuizDTO dto = new QuizDTO();
        dto.id = String.valueOf(quiz.getId());
        dto.question = quiz.getQuestion();
        dto.specifyingCategoryId = String.valueOf(quiz.getSubcategoryId());
        dto.answerList = new ArrayList<>(quiz.getAnswers());

        return dto;
    }

    public static ListDTO<QuizDTO> transform(List<Quiz> quizzes, int offset,
                                             int limit){

        List<QuizDTO> dtoList = null;
        if(quizzes != null){
            dtoList = quizzes.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(QuizConverter::transform)
                    .collect(Collectors.toList());
        }

        ListDTO<QuizDTO> dto = new ListDTO<>();
        dto.list = dtoList;
        dto._links = new ListDTO.ListLinks();
        dto.rangeMin = offset;
        assert dtoList != null;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = quizzes.size();

        return dto;
    }
}
