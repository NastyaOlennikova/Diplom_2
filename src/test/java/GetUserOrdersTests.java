import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.NewUserData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrdersTests {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Get all orders made by user, no auth")
    public void getUserOrdersNoAuth() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/orders");
        response.then().assertThat().statusCode(401).and().body("message", equalTo("You should be authorised"));
    }
    @Test
    @DisplayName("Get all orders made by user, token auth")
    public void getUserOrdersAuth() {
        NewUserData newUser = new NewUserData();
        List<String> tokens = newUser.registerUserAndReturnTokens();
        String refreshToken = tokens.get(0);
        String accessToken = tokens.get(1);

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken.substring(7))
                        .when()
                        .get("/api/orders");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        newUser.deleteUser(refreshToken, accessToken);
    }
}
