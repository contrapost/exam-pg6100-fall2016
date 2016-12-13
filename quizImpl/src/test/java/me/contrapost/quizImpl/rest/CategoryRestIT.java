package me.contrapost.quizImpl.rest;

import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.get;
import static org.hamcrest.core.Is.is;

public class CategoryRestIT extends CategoryRestTestBase{

    @Test
    public void testCleanDB() {

        get("/categories").then()
                .statusCode(200)
                .body("size()", is(0));
    }
}
