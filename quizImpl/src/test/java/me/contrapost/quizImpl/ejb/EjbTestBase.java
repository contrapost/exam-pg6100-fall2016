package me.contrapost.quizImpl.ejb;

import me.contrapost.quizImpl.entities.Category;
import me.contrapost.quizImpl.entities.Subcategory;
import me.contrapost.quizImpl.util.DeleterEJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;


public abstract class EjbTestBase {

    @SuppressWarnings("unused")
    @Deployment
    public static JavaArchive createDeployment() {

        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "me.contrapost.quizImpl")
                .addClass(DeleterEJB.class)
                .addPackages(true, "com.google")
                .addPackages(true, "org.apache.commons.codec")
                .addAsResource("META-INF/persistence.xml");
    }

    @EJB
    protected QuizEJB quizEJB;

    @EJB
    protected CategoryEJB categoryEJB;

    @EJB
    protected DeleterEJB deleterEJB;


    @Before
    @After
    public void emptyDatabase(){
        deleterEJB.deleteQuizzes();

        deleterEJB.deleteEntities(Subcategory.class);
        deleterEJB.deleteEntities(Category.class);
    }

    protected long createCategory(String title) {
        return categoryEJB.createCategory(title);
    }

    protected long createSubcategory(String title, long parentCategoryId) {
        return categoryEJB.createSubcategory(title, parentCategoryId);
    }

    protected long createQuiz(String categoryTitle, String subcategoryTitle,
                              String question, List<String> answers, int correctAnswerIndex) {
        long subcategoryId = createSubcategory(subcategoryTitle, createCategory(categoryTitle));
        return quizEJB.createQuiz(question, answers, correctAnswerIndex, subcategoryId);
    }

    protected long createQuiz(String categoryTitle, String subcategoryTitle, String question) {

        return createQuiz(categoryTitle, subcategoryTitle, question, getAnswers(), 1);
    }

    protected long createQuiz(String question) {

        return createQuiz("Category", "Subcategory",  question);
    }

    protected long createQuiz(String question, long subcategoryId) {
        return quizEJB.createQuiz(question, getAnswers(), 1, subcategoryId);
    }

    public List<String> getAnswers() {
        List<String> answers = new ArrayList<>();
        answers.add("Right answer");
        for (int i = 1; i < 4; i++) {
            answers.add("Wrong #" + i);
        }

        return answers;
    }
}
