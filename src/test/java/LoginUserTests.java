import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.Credentials;
import org.example.NewUserData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String refreshToken;
    private String accessToken;

    @Test
    @DisplayName("Successful user login")
    public void loginUserValidCredentials() {
        NewUserData newUser = new NewUserData();
        ArrayList<String> emailPass = newUser.registerNewUserAndReturnCredentials();
        Credentials credentials = new Credentials(emailPass.get(0), emailPass.get(1));
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .post("/api/auth/login");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");
        newUser.deleteUser(refreshToken, accessToken);
    }


    @Test
    @DisplayName("Login with invalid password")
    public void loginUserInvalidPassword() {
        NewUserData newUser = new NewUserData();
        ArrayList<String> emailPass = newUser.registerNewUserAndReturnCredentials();
        Credentials credentials = new Credentials(emailPass.get(0), RandomStringUtils.randomAlphabetic(10));
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .post("/api/auth/login");
        response.then().assertThat().statusCode(401).and().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Login with invalid email")
    public void loginUserInvalidEmail() {
        NewUserData newUser = new NewUserData();
        ArrayList<String> emailPass = newUser.registerNewUserAndReturnCredentials();
        Credentials credentials = new Credentials(RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru", emailPass.get(1));
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .post("/api/auth/login");
        response.then().assertThat().statusCode(401).and().body("success", equalTo(false));
    }
}
