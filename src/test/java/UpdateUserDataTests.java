import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.api.Users;
import org.example.jsontestdata.Credentials;
import org.example.jsontestdata.NewUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserDataTests {
    NewUserData newUser;
    List<String> tokens;
    Credentials credentials;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        newUser = new NewUserData();
        tokens = newUser.registerUserAndReturnTokens();
        credentials = newUser.generateNewUserCredentials();
    }

    @Test
    @DisplayName("Update user's data, authorized")
    public void updateUserDataAuth() {
        Users users = new Users();
        Response response = users.updateUserInfoAuth(tokens, credentials);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Update user's data, unauthorized")
    public void updateUserDataNoAuth() {
        Users users = new Users();
        Response response = users.updateUserInfoNoAuth(credentials);
        response.then().assertThat().statusCode(401).and().body("message", equalTo("You should be authorised"));

    }

    @After
    public void deleteTestUser() {
        newUser.deleteUser(tokens.get(0), tokens.get(1));
    }
}
