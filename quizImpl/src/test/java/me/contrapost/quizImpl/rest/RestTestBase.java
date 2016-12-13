package me.contrapost.quizImpl.rest;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import me.contrapost.quizAPI.dto.CategoryDTO;
import me.contrapost.quizAPI.dto.SubcategoryDTO;
import me.contrapost.quizAPI.dto.collection.ListDTO;
import me.contrapost.quizImpl.rest.util.JBossUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class RestTestBase {

    @BeforeClass
    public static void initClass() {
        JBossUtil.waitForJBoss(10);

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Before
    @After
    public void clean() {

        /*
           Recall, as Wildfly is running as a separated process, changed
           in the database will impact all the tests.
           Here, we read each resource (GET), and then delete them
           one by one (DELETE)
         */
        List<CategoryDTO> list = Arrays.asList(given().accept(ContentType.JSON).get("/categories")
                .then()
                .statusCode(200)
                .extract().as(CategoryDTO[].class));


        /*
            Code 204: "No Content". The server has successfully processed the request,
            but the return HTTP response will have no body.
         */
        list.forEach(dto ->
                given().pathParam("id", dto.id)
                        .delete("/categories/{id}")
                        .then().statusCode(204));

        get("/categories").then().statusCode(200).body("size()", is(0));

        int total = Integer.MAX_VALUE;

        /*
            as the REST API does not return the whole state of the database (even,
            if I use an infinite "limit") I need to keep doing queries until the totalSize is 0
         */

        while (total > 0) {

            //seems there are some limitations when handling generics
            ListDTO<?> listDto = given()
                    .queryParam("limit", Integer.MAX_VALUE)
                    .get("/quizzes")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ListDTO.class);

            listDto.list.stream()
                    //the "NewsDto" get unmarshalled into a map of fields
                    .map(n -> ((Map) n).get("id"))
                    .forEach(id ->
                            given().delete("/quizzes/" + id)
                                    .then()
                                    .statusCode(204)
                    );

            total = listDto.totalSize - listDto.list.size();
        }
    }

    public String createSubcategory(String title, String categoryId) {
        return given().contentType(ContentType.JSON)
                .body(new SubcategoryDTO(null, title, categoryId))
                .post("/categories/" + categoryId + "/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    public String createCategory(String title) {
        return given().contentType(ContentType.JSON)
                .body(new CategoryDTO(null, title, null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();
    }
}
