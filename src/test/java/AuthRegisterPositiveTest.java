import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.nomoreparties.stellarburgers.RequestServices;
import site.nomoreparties.stellarburgers.Utilities;
import site.nomoreparties.stellarburgers.pojo.AuthResponse;
import site.nomoreparties.stellarburgers.pojo.User;

import static org.junit.jupiter.api.Assertions.*;


public class AuthRegisterPositiveTest {
    private RequestServices services;
    private String login;
    private String pwd;
    private String name;
    private ValidatableResponse createUser;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        services = new RequestServices();
        login = Utilities.getRandomLogin();
        pwd = Utilities.getRandomPwd();
        name = Utilities.getRandomString(3);
        createUser = services.createUser(login, pwd, name);
    }

    @Test
    public void createUserTest() {
        AuthResponse response = createUser.extract().body().as(AuthResponse.class);
        User user = response.getUser();
        assertAll(
                ()-> assertEquals(200, createUser.extract().statusCode()),
                ()-> assertTrue(response.isSuccessful()),
                ()-> assertTrue(login.equalsIgnoreCase(user.getEmail())),
                ()-> assertEquals(name, user.getName()),
                ()-> assertNotNull(response.getAccessToken()),
                ()-> assertNotNull(response.getRefreshToken())
        );
    }

    @Test
    public void loginTest() {
        ValidatableResponse response = services.login(login, pwd);
        AuthResponse loginByNewUser = response.extract().body().as(AuthResponse.class);
        User user = loginByNewUser.getUser();
        assertAll(
                ()-> assertEquals(200, response.extract().statusCode()),
                ()-> assertTrue(loginByNewUser.isSuccessful()),
                ()-> assertTrue(login.equalsIgnoreCase(user.getEmail())),
                ()-> assertEquals(name, user.getName()),
                ()-> assertNotNull(loginByNewUser.getAccessToken()),
                ()-> assertNotNull(loginByNewUser.getRefreshToken())
        );
    }

}
