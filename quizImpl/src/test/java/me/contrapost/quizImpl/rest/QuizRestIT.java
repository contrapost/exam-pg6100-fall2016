package me.contrapost.quizImpl.rest;

import io.restassured.http.ContentType;
import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.QuizWithCorrectAnswerDTO;
import me.contrapost.quizAPI.dto.collection.ListDTO;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class QuizRestIT extends RestTestBase {

    @Test
    public void testCleanDB() {

        get("/quizzes").then()
                .statusCode(200)
                .body("list.size()", is(0));
    }

    @Test
    public void testCreateAndGetQuiz() {

        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct);

        get("/quizzes").then().statusCode(200).body("list.size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizzes").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("quizzes/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("subcategoryId", is(subcategoryId))
                .body("answerList", is(answers))
                .body("question", is(question));
    }

    @Test
    public void testCreateQuizWithId() {
        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO("1", question, subcategoryId, answers, correct);

        get("/quizzes").then().statusCode(200).body("list.size()", is(0));

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/quizzes")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateQuizWithWrongParentId() {
        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO(null, question, subcategoryId + "X", answers, correct);

        get("/quizzes").then().statusCode(200).body("list.size()", is(0));

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/quizzes")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCheckAnswer() {
        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct);

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        QuizWithCorrectAnswerDTO answerDTO = given().queryParam("quizId", id)
                .get("/quizzes/answer")
                .then()
                .statusCode(200)
                .extract().as(QuizWithCorrectAnswerDTO.class);

        assertEquals(answerDTO.indexOfCorrectAnswer, 0);
    }

    @Test
    public void testGetRandom() {

        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizzes/random")
                .then()
                .statusCode(200)
                .body("id", is(id));
    }

    @Test
    public void testPatchChangeQuestion() {

        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        QuizDTO originalQuiz = given()
                .accept(ContentType.JSON)
                .get("/quizzes/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(QuizDTO.class);

        assertTrue(originalQuiz.answerList.stream().anyMatch(answer -> answer.equals("Answer 1")));

        String newQuestion = "Quiz question v.2";

        given().contentType("application/merge-patch+json")
                .body("{\"question\":\"" + newQuestion + "\"}")
                .patch("/quizzes/" + id)
                .then()
                .statusCode(204);

        QuizDTO readBack = given()
                .accept(ContentType.JSON)
                .get("/quizzes/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(QuizDTO.class);

        assertEquals(newQuestion, readBack.question);
        assertTrue(readBack.answerList.stream().anyMatch(answer -> answer.equals("Answer 1")));
        assertEquals(id, readBack.id); // should had stayed the same
    }

    @Test
    public void testPatchChangeQuestionWithNewId() {
        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        QuizDTO originalQuiz = given()
                .accept(ContentType.JSON)
                .get("/quizzes/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(QuizDTO.class);

        String newId = "13654";

        given().contentType("application/merge-patch+json")
                .body("{\"id\":\"" + newId + "\"}")
                .patch("/quizzes/" + id)
                .then()
                .statusCode(409);
    }

    @Test
    public void testPatchChangeQuestionWithWrongId() {
        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        QuizDTO originalQuiz = given()
                .accept(ContentType.JSON)
                .get("/quizzes/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(QuizDTO.class);

        String newQuestion = "Quiz question v.2";

        given().contentType("application/merge-patch+json")
                .body("{\"question\":\"" + newQuestion + "\"}")
                .patch("/quizzes/" + Integer.MAX_VALUE)
                .then()
                .statusCode(404);
    }

    @Test
    public void testSelfLink() {

        for (int i = 0; i < 15; i++) createQuizWithDifferentCategories("" + i, i);

        get("/quizzes").then().statusCode(200).body("list.size()", is(10));

        ListDTO<?> dto = get("/quizzes")
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, dto.totalSize.intValue());
        assertNotNull(dto._links.self.href);

        ListDTO<?> sameDto = get(dto._links.self.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(10, sameDto.list.size());
        assertEquals(15, sameDto.totalSize.intValue());
    }

    @Test
    public void testNextLink() {
        for (int i = 0; i < 15; i++) createQuizWithDifferentCategories("" + i, i);

        ListDTO<?> dto = given().queryParam("limit", 6).
                get("/quizzes")
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, dto.totalSize.intValue());
        assertEquals(6, dto.list.size());

        ListDTO<?> nextDto = given().queryParam("limit", 5).
                get(dto._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, nextDto.totalSize.intValue());
        assertEquals(5, nextDto.list.size());

        ListDTO<?> lastDto = given().queryParam("limit", 5).
                get(nextDto._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, lastDto.totalSize.intValue());
        assertEquals(4, lastDto.list.size());
    }

    @Test
    public void textPreviousLink() {
        for (int i = 0; i < 15; i++) createQuizWithDifferentCategories("" + i, i);

        ListDTO<?> dto = given().queryParam("limit", 6).
                get("/quizzes")
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, dto.totalSize.intValue());
        assertEquals(6, dto.list.size());


        ListDTO<?> nextDto = get(dto._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, dto.totalSize.intValue());
        assertEquals(6, dto.list.size());

        ListDTO<?> originalDto = get(nextDto._links.previous.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, originalDto.totalSize.intValue());
        assertEquals(6, originalDto.list.size());
    }

    @Test
    public void testFilter() {

        String categoryId = createCategory("Category");
        String subcategoryId = createSubcategory("Sub", categoryId);

        String question = "Question";
        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizzes").then().statusCode(200).body("list.size()", is(1));

        given().queryParam("filter", subcategoryId)
                .get("/quizzes")
                .then()
                .statusCode(200)
                .body("list.size()", is(1));

        given().queryParam("filter", Integer.MAX_VALUE)
                .get("/quizzes")
                .then()
                .statusCode(200)
                .body("list.size()", is(0));
    }

    private void createQuizWithDifferentCategories(String question, int number) {
        String categoryId = createCategory("Category" + number);
        String subcategoryId = createSubcategory("Sub" + number, categoryId);

        List<String> answers = Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4");
        int correct = 0;

        given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, subcategoryId, answers, correct))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();
    }
}
