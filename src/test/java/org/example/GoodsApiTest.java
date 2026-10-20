package org.example;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@Tag("api")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GoodsApiTest {
    private static final String BASE_URL = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080");
    private static String username;
    private static String password;
    private final Set<Long> createdIds = new LinkedHashSet<>();

    @BeforeAll
    static void configure() {
        username = System.getenv("API_USERNAME");
        password = System.getenv("API_PASSWORD");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("Set API_USERNAME and API_PASSWORD before running apiTest.");
        }
    }

    private static RequestSpecification request() {
        return given()
                .baseUri(BASE_URL)
                .auth().preemptive().basic(username, password)
                .accept(ContentType.JSON);
    }

    private static String newProductName() {
        return "homework-" + UUID.randomUUID();
    }

    private static Map<String, Object> product(String name, double price) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("price", price);
        return body;
    }

    private static long createProduct(String name) {
        long id = request().contentType(ContentType.JSON)
                .body(product(name, 1.25))
                .when().post("/goods/add")
                .then().statusCode(200)
                .extract().jsonPath().getLong("data.id");
        return id;
    }

    private long createAndTrackProduct(String name) {
        long id = createProduct(name);
        createdIds.add(id);
        return id;
    }

    @AfterEach
    void removeProductsCreatedByThisTest() {
        for (long id : createdIds) {
            request().pathParam("id", id).when().delete("/goods/{id}")
                    .then().statusCode(anyOf(is(200), is(404)));
        }
        createdIds.clear();
    }

    @Test
    @Order(1)
    void goodsListWithGivenWhenThenHasSuccessStatusAndEmptyBody() {
        given().baseUri(BASE_URL).auth().preemptive().basic(username, password)
                .when().get("/goods/list")
                .then().statusCode(200).body("goods", empty());
    }

    @Test
    @Order(2)
    void goodsListWithRequestSpecificationHasSuccessStatusAndEmptyBody() {
        RequestSpecification specification = request();
        given().spec(specification)
                .when().get("/goods/list")
                .then().statusCode(200).body("goods", empty());
    }

    @Test
    @Order(3)
    void addedProductIsReturnedByGoodsListWithRestAssuredMatcher() {
        String name = newProductName();
        createAndTrackProduct(name);

        request().queryParam("page", 0).queryParam("size", 100)
                .when().get("/goods/list")
                .then().statusCode(200).body("goods.name", hasItem(name));
    }

    @Test
    @Order(4)
    void addedProductIsReturnedByGoodsListWithAssertJ() {
        String name = newProductName();
        createAndTrackProduct(name);

        String response = request().queryParam("page", 0).queryParam("size", 100)
                .when().get("/goods/list")
                .then().statusCode(200).extract().asString();
        assertThat(response).as("GET /goods/list response should contain the created product name")
                .contains(name);
    }

    @Test
    @Order(5)
    void addProductReturnsBadRequestForBlankName() {
        request().contentType(ContentType.JSON).body(product("", 2.0))
                .when().post("/goods/add")
                .then().statusCode(400);
    }

    @Test
    @Order(6)
    void getProductReturnsSuccessForExistingId() {
        long id = createAndTrackProduct(newProductName());
        request().pathParam("id", id).when().get("/goods/{id}")
                .then().statusCode(200);
    }

    @Test
    @Order(7)
    void getProductReturnsNotFoundForUnknownId() {
        request().pathParam("id", 0L).when().get("/goods/{id}")
                .then().statusCode(404);
    }

    @Test
    @Order(8)
    void patchProductReturnsSuccessForExistingId() {
        String name = newProductName();
        long id = createAndTrackProduct(name);
        request().pathParam("id", id).contentType(ContentType.JSON)
                .body(product(name, 2.50)).when().patch("/goods/{id}")
                .then().statusCode(200);
    }

    @Test
    @Order(9)
    void patchProductReturnsBadRequestForBlankName() {
        long id = createAndTrackProduct(newProductName());
        request().pathParam("id", id).contentType(ContentType.JSON)
                .body(product("", 2.50)).when().patch("/goods/{id}")
                .then().statusCode(400);
    }

    @Test
    @Order(10)
    void patchProductReturnsNotFoundForUnknownId() {
        request().pathParam("id", 0L).contentType(ContentType.JSON)
                .body(product(newProductName(), 2.50)).when().patch("/goods/{id}")
                .then().statusCode(404);
    }

    @Test
    @Order(11)
    void deleteProductReturnsSuccessForExistingId() {
        long id = createAndTrackProduct(newProductName());
        request().pathParam("id", id).when().delete("/goods/{id}")
                .then().statusCode(200);
    }

    @Test
    @Order(12)
    void deleteProductReturnsNotFoundForUnknownId() {
        request().pathParam("id", 0L).when().delete("/goods/{id}")
                .then().statusCode(404);
    }

    @Test
    @Order(13)
    void goodsListReturnsSuccessWithPaginationParameters() {
        request().queryParam("page", 0).queryParam("size", 10)
                .when().get("/goods/list").then().statusCode(200);
    }
}
