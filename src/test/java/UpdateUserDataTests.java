import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.Credentials;
import org.example.NewUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserDataTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String refreshToken;
    private String accessToken;
    NewUserData newUser = new NewUserData();

    @Test
    public void updateUserDataAuth() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(newUser.generateNewUserCredentials())
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");

        Credentials credentials = newUser.generateNewUserCredentials();
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.substring(7, accessToken.length()))
                .and()
                .body(credentials)
                .when()
                .patch("/api/auth/user");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    public void updateUserDataNoAuth() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(newUser.generateNewUserCredentials())
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");

        Credentials credentials = newUser.generateNewUserCredentials();
        given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(401).and().body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteTestData() {
        newUser.deleteUser(refreshToken, accessToken);
    }


}
