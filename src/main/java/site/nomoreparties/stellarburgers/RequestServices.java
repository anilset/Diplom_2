package site.nomoreparties.stellarburgers;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.pojo.Auth;
import site.nomoreparties.stellarburgers.pojo.AuthResponse;
import site.nomoreparties.stellarburgers.pojo.User;

import static io.restassured.RestAssured.*;

import static site.nomoreparties.stellarburgers.LibraryAPI.*;

public class RequestServices {
    public ValidatableResponse login(String login, String password){
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(new User(login, password))
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    public ValidatableResponse updateToken(String refreshToken){
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(new Auth(refreshToken))
                .when()
                .post(TOKEN_UPDATE_PATH)
                .then();
    }

    public ValidatableResponse logout(String refreshToken){
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(new Auth(refreshToken))
                .when()
                .post(LOGOUT_PATH)
                .then();
    }

    public ValidatableResponse createUser(String login, String password, String name){
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(new User(login, password, name))
                .when()
                .post(REGISTER_PATH)
                .then();
    }
    public ValidatableResponse readUser(String accessToken){
        return given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get(USER_PATH)
                .then();
    }

    public ValidatableResponse updateUser(String accessToken, User user){
        return given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .patch(USER_PATH)
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken){
        return given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete(USER_PATH)
                .then();
    }

    public static String getAccessToken(ValidatableResponse response) {
        AuthResponse authResponse = response.extract().body().as(AuthResponse.class);
        String[] bearerToken = authResponse.getAccessToken().split(" ");
        return bearerToken[1];
    }

    public static String getRefreshToken(ValidatableResponse response) {
        AuthResponse authResponse = response.extract().body().as(AuthResponse.class);
        return authResponse.getRefreshToken();
    }
}
