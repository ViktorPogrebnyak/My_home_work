package org.example;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/** API helpers used only for creating and cleaning up UI test data. */
abstract class UiApiSupport {
    protected static final String BASE_URL = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080");

    private final Set<Long> createdProductIds = new LinkedHashSet<>();

    protected long createProduct(String name, int price) {
        Map<String, Object> product = new LinkedHashMap<>();
        product.put("name", name);
        product.put("price", price);

        long id = given()
                .baseUri(BASE_URL)
                .auth().preemptive().basic(apiUsername(), apiPassword())
                .contentType(ContentType.JSON)
                .body(product)
                .when().post("/goods/add")
                .then().statusCode(200)
                .extract().jsonPath().getLong("data.id");
        createdProductIds.add(id);
        return id;
    }

    protected void trackProduct(long id) {
        createdProductIds.add(id);
    }

    /** Finds an item just created through the admin page and schedules it for deletion. */
    protected void trackProductByName(String name) {
        List<?> goods = given()
                .baseUri(BASE_URL)
                .auth().preemptive().basic(apiUsername(), apiPassword())
                .accept(ContentType.JSON)
                .when().get("/goods/list?page=0&size=100")
                .then().statusCode(200)
                .extract().jsonPath().getList("goods");

        Long id = goods.stream()
                .filter(Map.class::isInstance)
                .map(product -> (Map<?, ?>) product)
                .filter(product -> name.equals(product.get("name")))
                .map(product -> ((Number) product.get("id")).longValue())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Created product was not found in the API list: " + name));
        trackProduct(id);
    }

    protected static String adminUsername() {
        return requiredEnvironmentVariable("UI_ADMIN_USERNAME", "API_USERNAME");
    }

    protected static String adminPassword() {
        return requiredEnvironmentVariable("UI_ADMIN_PASSWORD", "API_PASSWORD");
    }

    private static String apiUsername() {
        return requiredEnvironmentVariable("API_USERNAME");
    }

    private static String apiPassword() {
        return requiredEnvironmentVariable("API_PASSWORD");
    }

    private static String requiredEnvironmentVariable(String... names) {
        for (String name : names) {
            String value = System.getenv(name);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        throw new IllegalStateException("Set " + String.join(" or ", names) + " before running UI tests.");
    }

    @AfterEach
    void deleteProductsCreatedByTest() {
        for (long id : new ArrayList<>(createdProductIds)) {
            given()
                    .baseUri(BASE_URL)
                    .auth().preemptive().basic(apiUsername(), apiPassword())
                    .pathParam("id", id)
                    .when().delete("/goods/{id}")
                    .then().statusCode(anyOf(is(200), is(404)));
        }
        createdProductIds.clear();
    }
}
