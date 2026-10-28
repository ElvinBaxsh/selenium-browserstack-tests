package io.github.elvinbaxsh.tests.api;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.elvinbaxsh.core.Config;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * automationexercise.com always answers with HTTP 200 and puts the real status in "responseCode".
 * It also sends JSON with a text/html header, so the body is parsed manually.
 */
public class ProductsApiTest {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = Config.API_URL;
    }

    private JsonPath json(Response response) {
        assertEquals(response.statusCode(), 200);
        return JsonPath.from(response.asString());
    }

    @Test(description = "GET /productsList returns all products")
    public void getAllProducts() {
        JsonPath body = json(given().get("/productsList"));
        assertEquals(body.getInt("responseCode"), 200);

        List<Map<String, Object>> products = body.getList("products");
        assertFalse(products.isEmpty());
        for (Map<String, Object> product : products) {
            assertTrue(product.keySet().containsAll(List.of("id", "name", "price", "brand", "category")), product.toString());
            assertTrue(String.valueOf(product.get("price")).matches("Rs\\. \\d+"), "Price format: " + product.get("price"));
        }
    }

    @Test(description = "POST /productsList is not supported")
    public void postProductsNotSupported() {
        JsonPath body = json(given().post("/productsList"));
        assertEquals(body.getInt("responseCode"), 405);
        assertEquals(body.getString("message"), "This request method is not supported.");
    }

    @Test(description = "GET /brandsList returns brands")
    public void getAllBrands() {
        JsonPath body = json(given().get("/brandsList"));
        assertEquals(body.getInt("responseCode"), 200);
        assertFalse(body.getList("brands").isEmpty());
        assertNotNull(body.getString("brands[0].brand"));
    }

    @Test(description = "POST /searchProduct finds matching products")
    public void searchProduct() {
        JsonPath body = json(given().formParam("search_product", "top").post("/searchProduct"));
        assertEquals(body.getInt("responseCode"), 200);

        List<String> names = body.getList("products.name");
        List<String> categories = body.getList("products.category.category");
        assertFalse(names.isEmpty());
        for (int i = 0; i < names.size(); i++) {
            String text = (names.get(i) + " " + categories.get(i)).toLowerCase();
            assertTrue(text.contains("top"), "Search result should match 'top': " + text);
        }
    }

    @Test(description = "POST /searchProduct without parameter returns 400")
    public void searchWithoutParameter() {
        JsonPath body = json(given().post("/searchProduct"));
        assertEquals(body.getInt("responseCode"), 400);
        assertEquals(body.getString("message"), "Bad request, search_product parameter is missing in POST request.");
    }

    @Test(description = "POST /verifyLogin with unknown user returns 404")
    public void verifyLoginUnknownUser() {
        JsonPath body = json(given()
                .formParam("email", "no-such-user@example.com")
                .formParam("password", "wrong")
                .post("/verifyLogin"));
        assertEquals(body.getInt("responseCode"), 404);
        assertEquals(body.getString("message"), "User not found!");
    }

    @Test(description = "POST /verifyLogin without email returns 400")
    public void verifyLoginWithoutEmail() {
        JsonPath body = json(given().formParam("password", "wrong").post("/verifyLogin"));
        assertEquals(body.getInt("responseCode"), 400);
        assertEquals(body.getString("message"), "Bad request, email or password parameter is missing in POST request.");
    }

    @Test(description = "DELETE /verifyLogin is not supported")
    public void deleteVerifyLoginNotSupported() {
        JsonPath body = json(given().delete("/verifyLogin"));
        assertEquals(body.getInt("responseCode"), 405);
        assertEquals(body.getString("message"), "This request method is not supported.");
    }
}
