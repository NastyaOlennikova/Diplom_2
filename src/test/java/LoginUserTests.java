import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.Users;
import org.example.jsontestdata.Credentials;
import org.example.jsontestdata.NewUserData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String refreshToken;
    private String accessToken;
    NewUserData newUser = new NewUserData();
    ArrayList<String> emailPass = newUser.registerNewUserAndReturnCredentials();


    @Test
    @DisplayName("Successful user login")
    public void loginUserValidCredentials() {
        Credentials credentials = new Credentials(emailPass.get(0), emailPass.get(1));
        Users users = new Users();
        Response response = users.loginUser(credentials);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");
        newUser.deleteUser(refreshToken, accessToken);
    }


    @Test
    @DisplayName("Login with invalid password")
    public void loginUserInvalidPassword() {
        Credentials credentials = new Credentials(emailPass.get(0), RandomStringUtils.randomAlphabetic(10));
        Users users = new Users();
        Response response = users.loginUser(credentials);
        response.then().assertThat().statusCode(401).and().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Login with invalid email")
    public void loginUserInvalidEmail() {
        Credentials credentials = new Credentials(RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru", emailPass.get(1));
        Users users = new Users();
        Response response = users.loginUser(credentials);
        response.then().assertThat().statusCode(401).and().body("success", equalTo(false));
    }
}
