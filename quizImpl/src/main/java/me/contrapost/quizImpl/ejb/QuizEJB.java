package me.contrapost.quizImpl.ejb;

import me.contrapost.quizImpl.entities.Quiz;
import me.contrapost.quizImpl.entities.Subcategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"WeakerAccess"})
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

    public boolean updateQuiz(@NotNull Long id,
                              @NotNull String newQuestion,
                              @NotNull List<String> newAnswerList,
                              @NotNull int newCorrectAnswerIndex) {
        Quiz quiz = em.find(Quiz.class, id);
        if (quiz == null) return false;
        quiz.setQuestion(newQuestion);
        quiz.setAnswers(newAnswerList);
        quiz.setCorrectAnswerIndex(newCorrectAnswerIndex);
        return true;
    }

    public List<Quiz> getAllQuizzes() {
        //noinspection unchecked
        return em.createNamedQuery(Quiz.GET_ALL_QUIZZES).getResultList();
    }

    public List<Quiz> getAllQuizzes(int max) {
        //noinspection unchecked
        return em.createNamedQuery(Quiz.GET_ALL_QUIZZES).setMaxResults(max).getResultList();
    }

    public Long getRandomQuiz() {
        List<Quiz> quizzes = getAllQuizzes();

        return quizzes.get(new Random().nextInt(quizzes.size())).getId();
    }

    public List<Quiz> getAllQuizForSubcategory(int maxFromDb, Long id) {
        //noinspection unchecked
        return em.createQuery("select q from Quiz q where q.subcategoryId = :id")
                .setParameter("id", id)
                .setMaxResults(maxFromDb)
                .getResultList();
    }
}