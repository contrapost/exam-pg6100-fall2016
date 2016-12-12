package me.contrapost.quizImpl.ejb;

import me.contrapost.quizImpl.entities.Quiz;
import me.contrapost.quizImpl.entities.Subcategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Stateless
public class QuizEJB {

    @PersistenceContext
    protected EntityManager em;

    public long createQuiz(@NotNull String question,
                           @NotNull List<String> answers,
                           @NotNull int correctAnswerIndex,
                           @NotNull long parentCategoryId) {
        Subcategory category = em.find(Subcategory.class, parentCategoryId);
        if (category == null) throw new IllegalArgumentException("No subcategory with id: " + parentCategoryId);

        Quiz quiz = new Quiz();
        quiz.setQuestion(question);
        quiz.setAnswers(answers);
        quiz.setCorrectAnswerIndex(correctAnswerIndex);
        quiz.setSubcategoryId(parentCategoryId);

        em.persist(quiz);

        return quiz.getId();
    }

    public Quiz getQuiz(long id) {
        return em.find(Quiz.class, id);
    }

    public boolean deleteQuiz(@NotNull long id) {
        Quiz quiz = em.find(Quiz.class, id);
        if (quiz == null) return false;

        em.remove(quiz);
        return true;
    }

    public boolean updateQuizQuestion(@NotNull long quizId, @NotNull String newQuestionText) {
        Quiz quiz = em.find(Quiz.class, quizId);
        if (quiz == null) return false;
        quiz.setQuestion(newQuestionText);
        return true;
    }

    public boolean updateAnswersList(@NotNull long quizId, @NotNull String previousAnswer, @NotNull String newAnswer) {
        Quiz quiz = em.find(Quiz.class, quizId);
        if (quiz == null || !quiz.getAnswers().contains(previousAnswer))
            throw new IllegalArgumentException("Quiz doesn't exist or the previous answer doesn't match one in the map");
        int index = quiz.getAnswers().indexOf(previousAnswer);
        quiz.getAnswers().set(index, newAnswer);
        return true;
    }

    public boolean updateQuiz(@NotNull Long id,
                              @NotNull String newQuestion,
                              @NotNull List<String> newAnswerList,
                              @NotNull int newCorrectAnswerIndex,
                              @NotNull long newParentCategoryId) {
        Quiz quiz = em.find(Quiz.class, id);
        if (quiz == null) return false;
        quiz.setQuestion(newQuestion);
        quiz.setAnswers(newAnswerList);
        quiz.setCorrectAnswerIndex(newCorrectAnswerIndex);
        quiz.setSubcategoryId(newParentCategoryId);
        return true;
    }

    public List<Quiz> getAllQuizzes() {
        //noinspection unchecked
        return em.createNamedQuery(Quiz.GET_ALL_QUIZZES).getResultList();
    }

    public List<Long> getRandomQuizzes(int numberOfQuizzes) {
        List<Quiz> quizzes = getAllQuizzes();
        List<Long> ids = new ArrayList<>();
        while(ids.size() != numberOfQuizzes && quizzes.size() != 0) {
            ids.add(quizzes.remove(new Random().nextInt(quizzes.size())).getId());
        }
        return ids;
    }

    public Quiz getRandomQuizzes() {
        List<Quiz> quizzes = getAllQuizzes();

        return quizzes.get(new Random().nextInt(quizzes.size()));
    }
}
