package me.contrapost.gameSoap;

import me.contrapost.soap.client.GameSoap;
import me.contrapost.soap.client.GameSoapImplService;
import org.junit.BeforeClass;

import javax.xml.ws.BindingProvider;

public class GameSoapApiIT {

    private static GameSoap ws;

    @BeforeClass
    public static void initClass() {

        GameSoapImplService service = new GameSoapImplService();
        ws = service.getGameSoapImplPort();

        String url = "http://localhost:8080/newssoap/NewsSoapImpl";

        ((BindingProvider)ws).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
    }
}
