import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.Orders;
import org.example.jsontestdata.Ingredients;
import org.example.jsontestdata.NewUserData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

    }

    @Test
    @DisplayName("Create order for authorized user")
    public void createOrderAuth() {
        NewUserData newUser = new NewUserData();
        List<String> tokens = newUser.registerUserAndReturnTokens();
        String refreshToken = tokens.get(0);
        String accessToken = tokens.get(1);
        Orders ingredientList = new Orders();
        Response response = ingredientList.getIngredientDataAuth(accessToken);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        Orders randomOrder = new Orders();
        ArrayList<String>orderIngredients = randomOrder.randomOrderData(response);
        Ingredients ingredients = new Ingredients(orderIngredients);
        Orders order = new Orders();
        Response responseOrder = order.makeOrderAuth(accessToken, ingredients);
        responseOrder.then().assertThat().statusCode(200).and().body("success", equalTo(true)).and().body("order.price", notNullValue());
        newUser.deleteUser(refreshToken, accessToken);
    }

    @Test //тест падает, так как приходит 200-ый код для неавторизованного пользователя, неправильное поведение
    @DisplayName("Create order for unauthorized user")
    public void createOrderNoAuth() {
        Orders ingredientList = new Orders();
        Response response = ingredientList.getIngredientDataNoAuth();
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        Orders randomOrder = new Orders();
        ArrayList<String>orderIngredients = randomOrder.randomOrderData(response);
        Ingredients ingredients = new Ingredients(orderIngredients);
        Orders order = new Orders();
        Response responseOrder = order.makeOrderNoAuth(ingredients);
        responseOrder.then().assertThat().statusCode(401).and().body("success", equalTo(false)).and().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void createOrderNoIngredients() {
        NewUserData newUser = new NewUserData();
        List<String> tokens = newUser.registerUserAndReturnTokens();
        String refreshToken = tokens.get(0);
        String accessToken = tokens.get(1);
        Orders ingredientList = new Orders();
        Response response = ingredientList.getIngredientDataAuth(accessToken);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        Ingredients ingredients = new Ingredients(new ArrayList<>());
        Orders order = new Orders();
        Response responseOrder = order.makeOrderAuth(accessToken, ingredients);
        responseOrder.then().assertThat().statusCode(400).and().body("success", equalTo(false)).and().body("message", equalTo("Ingredient ids must be provided"));
        newUser.deleteUser(refreshToken, accessToken);
    }

    @Test
    @DisplayName("Create order with wrong ingredient hash by authorized user")
    public void createOrderAuthWrongIngredientId() {
        NewUserData newUser = new NewUserData();
        List<String> tokens = newUser.registerUserAndReturnTokens();
        String refreshToken = tokens.get(0);
        String accessToken = tokens.get(1);
        Orders ingredientList = new Orders();
        Response response = ingredientList.getIngredientDataAuth(accessToken);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        Orders randomOrder = new Orders();
        ArrayList<String>orderIngredients = randomOrder.randomOrderData(response);
        orderIngredients.add(RandomStringUtils.randomAlphabetic(24));
        Ingredients ingredients = new Ingredients(orderIngredients);
        Orders order = new Orders();
        Response responseOrder = order.makeOrderAuth(accessToken, ingredients);
        responseOrder.then().assertThat().statusCode(500);
        newUser.deleteUser(refreshToken, accessToken);
    }

    @Test
    @DisplayName("Create order with wrong ingredient hash by unauthorized user")
    public void createOrderNoAuthWrongIngredientId() {
        Orders ingredientList = new Orders();
        Response response = ingredientList.getIngredientDataNoAuth();
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));

        Orders randomOrder = new Orders();
        ArrayList<String>orderIngredients = randomOrder.randomOrderData(response);
        orderIngredients.add(RandomStringUtils.randomAlphabetic(24));
        Ingredients ingredients = new Ingredients(orderIngredients);
        Orders order = new Orders();
        Response responseOrder = order.makeOrderNoAuth(ingredients);
        responseOrder.then().assertThat().statusCode(500);
    }
}
