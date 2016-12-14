package me.contrapost.gameSoap;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import me.contrapost.soap.client.GameSoap;
import me.contrapost.soap.client.GameSoapImplService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.ws.BindingProvider;
import java.io.UnsupportedEncodingException;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

@Ignore
public class GameSoapApiIT {

    private static WireMockServer wireMockServer;
    private static GameSoap ws;

    @BeforeClass
    public static void initClass() {

        GameSoapImplService service = new GameSoapImplService();
        ws = service.getGameSoapImplPort();

        String url = "http://localhost:8080/gamesoap/GameSaopImpl";

        ((BindingProvider)ws).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);


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
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><gameDTO><quizId>1</quizId>" +
                        "<question>Question</question><answerList><answer>Answer 1</answer>" +
                        "<answer>Answer 2</answer><answer>Answer 3</answer><answer>Answer 4</answer></answerList>" +
                        "</gameDTO>";


        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*/quizzes/random*"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "xml")
//                                .withHeader("Content-Length", "" + response.getBytes("utf-8").length)
                                .withBody(response)));


        me.contrapost.soap.client.GameDTO gameDTO = ws.getRandomGame();

        assertEquals("1", gameDTO.getQuizId());
        assertEquals("question", gameDTO.getQuestion());
    }

    @Test
    public void testGetRandomFail() throws UnsupportedEncodingException {

        String response =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><gameDTO><quizId>1</quizId>" +
                        "<question>Question</question><answerList><answer>Answer 1</answer>" +
                        "<answer>Answer 2</answer><answer>Answer 3</answer><answer>Answer 4</answer></answerList>" +
                        "</gameDTO>";


        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*/quizzes/random*"))

                        .willReturn(WireMock.aResponse()
                                .withFixedDelay(2500)
                                .withHeader("Content-Type", "application/xml; charset=utf-8")
                                .withHeader("Content-Length", "" + response.getBytes("utf-8").length)
                                .withBody(response))
        );

        me.contrapost.soap.client.GameDTO gameDTO = ws.getRandomGame();
    }
}
