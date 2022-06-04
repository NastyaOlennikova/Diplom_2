import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.Users;
import org.example.jsontestdata.Credentials;
import org.example.jsontestdata.NewUserData;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class RegisterUserTests {

    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

    }

    private String refreshToken;
    private String accessToken;

    @Test
    @DisplayName("Register new user")
    public void createUniqueUser() {
        NewUserData newUser = new NewUserData();
        Credentials credentials = newUser.generateNewUserCredentials();
        Users users = new Users();
        Response response = users.registerUser(credentials);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");

        newUser.deleteUser(refreshToken, accessToken);
    }

    @Test
    @DisplayName("Attempt to create existed user")
    public void createExistedUser() {
        NewUserData newUser = new NewUserData();
        Credentials credentials = newUser.generateNewUserCredentials();
        Users users = new Users();
        Response response = users.registerUser(credentials);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");

        Response responseExistedUser = users.registerUser(credentials);
        responseExistedUser.then().assertThat().statusCode(403).and().body("success", equalTo(false));

        newUser.deleteUser(refreshToken, accessToken);
    }

    @Test
    @DisplayName("Attempt to create a user without filling all required fields")
    public void createUserRequiredFields() {
        Credentials credentials = new Credentials(RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru", RandomStringUtils.randomAlphabetic(10));
        Users users = new Users();
        Response response = users.registerUser(credentials);
        response.then().assertThat().statusCode(403).and().body("message", equalTo("Email, password and name are required fields"));
    }

}
