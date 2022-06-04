package org.example;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;


public class NewUserData {
    String userEmail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";;
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userName = RandomStringUtils.randomAlphabetic(10);

    public Credentials generateNewUserCredentials() {
        return new Credentials(userEmail, userPassword, userName);
    }
    public ArrayList<String> registerNewUserAndReturnCredentials(){

        String userEmail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);

        ArrayList<String> emailPass = new ArrayList<>();

        String registerRequestBody = "{\"email\":\"" + userEmail + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";

        Response response =  given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");


        if (response.statusCode() == 200) {
            emailPass.add(userEmail);
            emailPass.add(userPassword);
        }

        return emailPass;

    }
    public List<String> registerUserAndReturnTokens() {
        NewUserData newUser = new NewUserData();
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(newUser.generateNewUserCredentials())
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        String refreshToken = response.path("refreshToken");
        String accessToken = response.path("accessToken");
        List<String> tokens  = new ArrayList<>();
        tokens.add(refreshToken);
        tokens.add(accessToken);
        return tokens;
    }

    public void deleteUser(String refreshToken, String accessToken) {
        given()
                .header("Content-type", "application/json")
                .body("\"token\": \""+ refreshToken + "\"")
                .post("/api/auth/token");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken.substring(7, accessToken.length()))
                        .delete("/api/auth/user");
        response.then().assertThat().statusCode(202);
    }
}
