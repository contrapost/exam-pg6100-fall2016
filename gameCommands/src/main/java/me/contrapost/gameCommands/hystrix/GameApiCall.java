package me.contrapost.gameCommands.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;


public class GameApiCall extends HystrixCommand<Response> {

    private final String address;

    @SuppressWarnings("WeakerAccess")
    public GameApiCall(String address) {
        super(HystrixCommandGroupKey.Factory.asKey("Interactions with QuizApi"));
        this.address = address;
    }

    @Override
    protected Response run() throws Exception {

        URI uri = UriBuilder
                .fromUri(address)
                .build();
        Client client = ClientBuilder.newClient();

        Response response = client.target(uri)
                .request("application/json")
                .get();

        return response;
    }

    @Override
    protected Response getFallback() {
        //this is what is returned in case of exceptions or timeouts
        return null;
    }
}