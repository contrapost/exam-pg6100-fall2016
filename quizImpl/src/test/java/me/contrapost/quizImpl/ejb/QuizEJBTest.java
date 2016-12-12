package me.contrapost.quizImpl.ejb;

import com.google.common.base.Throwables;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBException;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@RunWith(Arquillian.class)
public class QuizEJBTest extends EjbTestBase {

    @Test
    public void testCreateQuiz() {

        String question = "Question";

        createQuiz(question);

        assertEquals(question, quizEJB.getAllQuizzes().get(0).getQuestion());
    }

    @Test
    public void testDeleteQuiz() {
        long quizId = createQuiz("Question");

        assertTrue(quizEJB.deleteQuiz(quizId));
        assertNull(quizEJB.getQuiz(quizId));
        assertTrue(quizEJB.getAllQuizzes().size() == 0);
    }

    @Test
    public void testUpdateQuizQuestion() {
        long quizId = createQuiz("Original question");

        String updatedQuestion = "Updated question text";

        assertTrue(quizEJB.updateQuizQuestion(quizId, updatedQuestion));
        assertEquals(quizEJB.getQuiz(quizId).getQuestion(), updatedQuestion);
    }

    @Test
    public void testUpdateAnswerList() {
        List<String> answers = new ArrayList<>();
        answers.add("Right answer");
        for (int i = 1; i < 4; i++) {
            answers.add("Wrong #" + i);
        }

        long quizId = createQuiz("Category", "Subcategory", "Question", answers, 1);


        String updatedAnswer = "Wrong Updated";
        assertTrue(quizEJB.updateAnswersList(quizId, "Wrong #2", updatedAnswer));
        assertTrue(quizEJB.getQuiz(quizId).getAnswers().contains(updatedAnswer));
        assertFalse(quizEJB.getQuiz(quizId).getAnswers().contains("Wrong # 2"));
    }

    @Test
    public void testAnswersListWithInvalidNumberOfAnswers() {
        List<String> answers = new ArrayList<>();
        answers.add("Right");
        answers.add("Wrong"); // Should be 2 more wrong answers


        try {
            createQuiz("Root", "Sub", "Question", answers, 1);
        } catch (EJBException e) {
            Throwable cause = Throwables.getRootCause(e);
            assertTrue("Cause: " + cause, cause instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testAnswersListWithWrongCorrectINdex() {
        List<String> answers = new ArrayList<>();
        answers.add("Right answer");
        for (int i = 1; i < 4; i++) {
            answers.add("Wrong #" + i);
        }

        try {
            createQuiz("Root", "Sub", "Question", answers, 5);
        } catch (EJBException e) {
            Throwable cause = Throwables.getRootCause(e);
            assertTrue("Cause: " + cause, cause instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testCreateTwoQuizzesWithSameQuestion() {
        String quizName = "Super duper quiz";

        createQuiz(quizName);

        try {
            createQuiz(quizName);
        } catch (EJBException e) {
            Throwable cause = Throwables.getRootCause(e);
            assertTrue("Cause: " + cause, cause instanceof SQLException);
        }
    }

    @Test
    public void testUpdateAnswerListForNonexistentQuiz() {
        try {
            quizEJB.updateAnswersList(0L, "New answer", "Previous answer");
        } catch (EJBException e) {
            Throwable cause = Throwables.getRootCause(e);
            assertTrue("Cause: " + cause, cause instanceof IllegalArgumentException);
        }
    }
}
