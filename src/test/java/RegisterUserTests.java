import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.Credentials;
import org.example.NewUserData;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class RegisterUserTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    private String refreshToken;
    private String accessToken;

    @Test
    public void createUniqueUser() {
        NewUserData newUser = new NewUserData();
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(newUser.generateNewUserCredentials())
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");
    }

    @Test
    public void createExistedUser() {
        NewUserData newUser = new NewUserData();
        Credentials credentials = newUser.generateNewUserCredentials();
            Response response =
                    given()
                            .header("Content-type", "application/json")
                            .and()
                            .body(credentials)
                            .when()
                            .post("/api/auth/register");
            response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
            refreshToken = response.path("refreshToken");
            accessToken = response.path("accessToken");

                    given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credentials)
                        .when()
                        .post("/api/auth/register")
                            .then().assertThat().statusCode(403).and().body("success", equalTo(false));
            newUser.deleteUser(refreshToken, accessToken);
    }

    @Test
    public void createUserRequiredFields() {
        Credentials credentials = new Credentials(RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru", RandomStringUtils.randomAlphabetic(10));
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credentials)
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().statusCode(403).and().body("message", equalTo("Email, password and name are required fields"));
    }

}
