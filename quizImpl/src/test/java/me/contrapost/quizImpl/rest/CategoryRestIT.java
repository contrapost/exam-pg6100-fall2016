package me.contrapost.quizImpl.rest;

import io.restassured.http.ContentType;
import me.contrapost.quizAPI.dto.CategoryDTO;
import me.contrapost.quizAPI.dto.SubcategoryDTO;
import me.contrapost.quizImpl.rest.util.HttpUtil;
import org.junit.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CategoryRestIT extends RestTestBase {

    @Test
    public void testCleanDB() {

        get("/categories").then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    public void testCreateCategory() {

        String title = "Title";

        CategoryDTO dto = new CategoryDTO(null, title, null);

        get("/categories").then().statusCode(200).body("list.size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("categories/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testCreateCategoryRawHttp() throws Exception {

        String id = given().contentType(ContentType.JSON)
                .body(new CategoryDTO(null, "økologi", null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().statusCode(200).body("list.size()", is(1));

        String message = "GET /quiz/api/categories/" + id + " HTTP/1.1\n";
        message += "Host:localhost:8080\n";
        message += "\n";

        String response = HttpUtil.executeHttpCommand("localhost", 8080, message, "UTF-8");

        String type = HttpUtil.getHeaderValue("Content-Type", response);

        String body = HttpUtil.getBodyBlock(response);

        assertTrue(body.contains("økologi"));
    }

    @Test
    public void testCreateTwoCategories() {

        String id1 = given().contentType(ContentType.JSON)
                .body(new CategoryDTO(null, "First", null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        String id2 = given().contentType(ContentType.JSON)
                .body(new CategoryDTO(null, "Second", null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().statusCode(200).body("list.size()", is(2));
    }

    @Test
    public void testCreateSubCategory() {

        String categoryId = createCategory("Category");

        String title = "Subcategory";

        SubcategoryDTO dto = new SubcategoryDTO(null, title, categoryId);

        get("categories/subcategories").then().statusCode(200).body("list.size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/categories/" + categoryId + "/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("categories/subcategories").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("categories/subcategories/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testExpand() {
        String categoryId = createCategory("Category");

        String title = "Subcategory";

        SubcategoryDTO dto = new SubcategoryDTO(null, title, categoryId);

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/categories/" + categoryId + "/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("categories/subcategories").then().statusCode(200).body("list.size()", is(1));

        given().queryParam("expand", false)
                .get("/categories/" + categoryId)
                .then()
                .statusCode(200)
                .body("subcategories", nullValue());

        given().queryParam("expand", false)
                .get("/categories")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].subcategories", nullValue());

        given().queryParam("expand", true)
                .get("/categories/" + categoryId)
                .then()
                .statusCode(200)
                .body("subcategories.id", hasItems(id));

        given().queryParam("expand", true)
                .get("/categories")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].subcategories.id", hasItems(id));
    }

    @Test
    public void testUpdateCategory() {

        String categoryId = createCategory("Category");

        String newTitle = "RootCategory v.2";

        given().contentType("application/merge-patch+json")
                .body("{\"title\":\"" + newTitle + "\"}")
                .patch("/categories/" + categoryId)
                .then()
                .statusCode(204);

        CategoryDTO readBack = given().port(8080)
                .baseUri("http://localhost")
                .accept(ContentType.JSON)
                .get("/categories/" + categoryId)
                .then()
                .statusCode(200)
                .extract()
                .as(CategoryDTO.class);

        assertEquals(newTitle, readBack.title);
        assertEquals(categoryId, readBack.id); // should had stayed the same
    }

    @Test
    public void testGetSubCategories() {

        String categoryId1 = createCategory("Category1");
        String categoryId2 = createCategory("Category2");

        String subcategoryId1 = createSubcategory("Sub1", categoryId1);
        String subcategoryId2 = createSubcategory("Sub2", categoryId2);
        String subcategoryId3 = createSubcategory("Sub3", categoryId2);

        get("categories/subcategories")
                .then()
                .statusCode(200)
                .body("list.size()", is(3));

        given().queryParam("parentId", categoryId1)
                .get("categories/subcategories")
                .then()
                .statusCode(200)
                .body("size", is(1));

        get("categories/" + categoryId2 + "/subcategories")
                .then()
                .statusCode(200)
                .body("size", is(2));
    }


}
