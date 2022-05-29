import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Credentials;
import org.example.NewUserData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserDataTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Update user's data, authorised")
    public void updateUserDataAuth() {
        NewUserData newUser = new NewUserData();
        List<String> tokens = newUser.registerUserAndReturnTokens();
        Credentials credentials = newUser.generateNewUserCredentials();
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(tokens.get(1).substring(7))
                .and()
                .body(credentials)
                .when()
                .patch("/api/auth/user")
        .then().assertThat().statusCode(200).and().body("success", equalTo(true));
        newUser.deleteUser(tokens.get(0), tokens.get(1));
    }

    @Test
    @DisplayName("Update user's data, unauthorised")
    public void updateUserDataNoAuth() {
        NewUserData newUser = new NewUserData();
        List<String> tokens = newUser.registerUserAndReturnTokens();
        Credentials credentials = newUser.generateNewUserCredentials();
        given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(401).and().body("message", equalTo("You should be authorised"));
        newUser.deleteUser(tokens.get(0), tokens.get(1));
    }
}
