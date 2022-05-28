import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.NewUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class RegisterUserTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    NewUserData newUser = new NewUserData();
    private String refreshToken;
    private String accessToken;

    @Test
    public void createUniqueUser() {
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


  @After
    public void deleteTestData() {
        newUser.deleteUser(refreshToken, accessToken);
  }


}
