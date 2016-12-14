package me.contrapost.gameRest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import me.contrapost.gameCommands.dto.AnswerDTO;
import me.contrapost.quizAPI.api.Formats;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;

public class GameApplicationTestBase {

    private static WireMockServer wireMockServer;

    static {
        System.setProperty("quizApiAddress", "localhost:8099");
    }

    @BeforeClass
    public static void initRestAssured() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/game/api/games";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        wireMockServer = new WireMockServer(
                wireMockConfig().port(8099).notifier(new ConsoleNotifier(true))
        );
        wireMockServer.start();
    }

    @AfterClass
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetRandom() throws UnsupportedEncodingException {
        String response =
                "{\"id\": \"1\",\"question\": \"Question\",\"subcategoryId\": \"1\",\"answerList\": [\"answer 1\",\"answer 2\",\"answer 3\",\"answer 4\"]}";


        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*/quizzes/random*"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + response.getBytes("utf-8").length)
                                .withBody(response)));


        get("/random")
                .then()
                .statusCode(200)
                .body("quizId", is("1"))
                .body("answerList.size", is(4))
                .body("question", is("Question"));
    }

    @Test
    public void testGetRandomFail() throws UnsupportedEncodingException {

        String response =
                "{\"id\": \"1\",\"question\": \"Question\",\"subcategoryId\": \"1\",\"answerList\": [\"answer 1\",\"answer 2\",\"answer 3\",\"answer 4\"]}";


        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*/quizzes/random*"))

                        .willReturn(WireMock.aResponse()
                                .withFixedDelay(2500)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + response.getBytes("utf-8").length)
                                .withBody(response))
        );

        given().get("/random")
                .then()
                .statusCode(500);

    }

    @Test
    public void testPlayGameCorrect() throws UnsupportedEncodingException {

        String response =
                "{\"id\": \"1\",\"question\": \"Question\",\"subcategoryId\": \"1\",\"answerList\": [\"answer 1\"," +
                        "\"answer 2\",\"answer 3\",\"answer 4\"], \"indexOfCorrectAnswer\": 0}";

        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*/quizzes/answer.*"))
                        .withQueryParam("quizId", WireMock.matching("\\d+"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + response.getBytes("utf-8").length)
                                .withBody(response)));

        AnswerDTO answer = given().contentType(Formats.JSON_V1).queryParam("quizId", "1")
                .queryParam("answerIndex", 0)
                .post()
                .then()
                .statusCode(201).extract().as(AnswerDTO.class);

        assertTrue(answer.isCorrect);
    }

    @Test
    public void testPlayGameWrong() throws UnsupportedEncodingException {

        String response =
                "{\"id\": \"1\",\"question\": \"Question\",\"subcategoryId\": \"1\",\"answerList\": [\"answer 1\"," +
                        "\"answer 2\",\"answer 3\",\"answer 4\"], \"indexOfCorrectAnswer\": 0}";

        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*/quizzes/answer.*"))
                        .withQueryParam("quizId", WireMock.matching("\\d+"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + response.getBytes("utf-8").length)
                                .withBody(response)));

        AnswerDTO answer = given().contentType(Formats.JSON_V1).queryParam("quizId", "1")
                .queryParam("answerIndex", 2)
                .post()
                .then()
                .statusCode(201).extract().as(AnswerDTO.class);

        assertFalse(answer.isCorrect);
    }
}
