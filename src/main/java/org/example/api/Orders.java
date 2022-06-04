package org.example.api;

import io.restassured.response.Response;
import org.example.jsontestdata.Ingredients;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;

public class Orders {
    public Response getIngredientDataAuth(String accessToken) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken)
                        .when()
                        .get("/api/ingredients");
        return response;
    }
    public Response getIngredientDataNoAuth() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/ingredients");
        return response;
    }
    public Response makeOrderAuth(String accessToken, Ingredients ingredients) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken.substring(7))
                        .and()
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        return response;
    }
    public Response makeOrderNoAuth(Ingredients ingredients) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        return response;
    }

    public Response getOrdersNoAuth() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/orders");
        return response;
    }

    public Response getOrdersAuth(String accessToken) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken.substring(7))
                        .when()
                        .get("/api/orders");
        return response;
    }

    public ArrayList<String> randomOrderData(Response response) {
        final Random random = new Random();
        List<String> allIngredientsIds = response.jsonPath().get("data._id");
        ArrayList<String> orderIngredients = new ArrayList<>();
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        return orderIngredients;
    }
}
