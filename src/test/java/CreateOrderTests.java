import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.Ingredients;
import org.example.NewUserData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class CreateOrderTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    public void createOrderAuth() {
        NewUserData newUser = new NewUserData();
        String token = newUser.registerUserAndReturnTokens().get(1);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .when()
                        .get("/api/ingredients");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        List<String> allIngredientsIds = response.jsonPath().get("data._id");
        ArrayList<String>orderIngredients = new ArrayList<>();
        final Random random = new Random();
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        Ingredients ingredients = new Ingredients(orderIngredients);

        Response response_order =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .and()
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        response_order.then().assertThat().statusCode(200).and().body("success", equalTo(true)).and().body("order.price", notNullValue());

    }

    @Test
    public void createOrderNoAuth() {
        NewUserData newUser = new NewUserData();
        String token = newUser.registerUserAndReturnTokens().get(1);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .when()
                        .get("/api/ingredients");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        List<String> allIngredientsIds = response.jsonPath().get("data._id");
        ArrayList<String> orderIngredients = new ArrayList<>();
        final Random random = new Random();
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        Ingredients ingredients = new Ingredients(orderIngredients);

        Response response_order =
                given()
                        .header("Content-type", "application/json")
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        response_order.then().assertThat().statusCode(200).and().body("success", equalTo(true)).and().body("order.status", nullValue());
    }

    @Test
    public void createOrderNoIngredients() {
        NewUserData newUser = new NewUserData();
        String token = newUser.registerUserAndReturnTokens().get(1);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .when()
                        .get("/api/ingredients");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        Ingredients ingredients = new Ingredients(new ArrayList<>());

        Response response_order =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .and()
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        response_order.then().assertThat().statusCode(400).and().body("success", equalTo(false)).and().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWrongIngredientId() {
        NewUserData newUser = new NewUserData();
        String token = newUser.registerUserAndReturnTokens().get(1);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .when()
                        .get("/api/ingredients");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        List<String> allIngredientsIds = response.jsonPath().get("data._id");
        ArrayList<String>orderIngredients = new ArrayList<>();
        final Random random = new Random();
        orderIngredients.add(RandomStringUtils.randomAlphabetic(24));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        orderIngredients.add(allIngredientsIds.get(random.nextInt(allIngredientsIds.size())));
        Ingredients ingredients = new Ingredients(orderIngredients);

        Response response_order =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .and()
                        .body(ingredients)
                        .when()
                        .post("/api/orders");
        response_order.then().assertThat().statusCode(500);
    }



}
