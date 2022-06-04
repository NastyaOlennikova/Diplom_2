package org.example.api;

import io.restassured.response.Response;
import org.example.jsontestdata.Credentials;

import java.util.List;

import static io.restassured.RestAssured.given;

public class Users {

    public Response loginUser(Credentials credentials) {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .post("/api/auth/login");
        return response;
    }

    public Response registerUser(Credentials credentials) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .post("/api/auth/register");
        return response;
    }

    public Response updateUserInfoAuth(List<String> tokens, Credentials credentials) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(tokens.get(1).substring(7))
                        .and()
                        .body(credentials)
                        .when()
                        .patch("/api/auth/user");
        return response;
    }

    public Response updateUserInfoNoAuth(List<String> tokens, Credentials credentials) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .patch("/api/auth/user");
        return response;
    }
}
