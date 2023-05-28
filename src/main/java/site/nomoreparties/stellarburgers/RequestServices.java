package site.nomoreparties.stellarburgers;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
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

    public ValidatableResponse createUser(String login, String password, String name){
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(new User(login, password, name))
                .when()
                .post(REGISTER_PATH)
                .then();
    }
}
