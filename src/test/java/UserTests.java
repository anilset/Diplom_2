import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.nomoreparties.stellarburgers.RequestServices;
import site.nomoreparties.stellarburgers.Utilities;
import site.nomoreparties.stellarburgers.pojo.AuthResponse;
import site.nomoreparties.stellarburgers.pojo.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;
import static site.nomoreparties.stellarburgers.LibraryAPI.BASE_URI;


public class UserTests {
    private static RequestServices services;
    private static String login;
    private static String pwd;
    private static String name;
    private static ValidatableResponse createUser;
    private static String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        services = new RequestServices();
        login = Utilities.getRandomLogin();
        pwd = Utilities.getRandomPwd();
        name = Utilities.getRandomString(3);
        createUser = services.createUser(login, pwd, name);
        accessToken = services.getAccessToken(createUser);
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    public void createUserPositiveTest() {
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
    @DisplayName("Проверка повторного создания пользователя с той же почтой/логином")
    public void createUserWithSimilarLoginTest() {
        ValidatableResponse createSimilarUser = services
                .createUser(login, Utilities.getRandomPwd(), Utilities.getRandomString(11));
        AuthResponse sameLogin = createSimilarUser.extract().body().as(AuthResponse.class);
        assertAll(
                ()-> assertEquals(403, createSimilarUser.extract().statusCode()),
                ()-> assertFalse(sameLogin.isSuccessful()),
                ()-> assertEquals("User already exists", sameLogin.getMessage())
        );
    }

    @Test
    @DisplayName("Проверка входа в аккаунт с валидными credentials")
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

    private static Stream<Arguments> provideInvalidCredentials(){
        return Stream.of(
                of(login, ""),
                of("", pwd),
                of("", ""),
                of(login, Utilities.getRandomPwd()),
                of(Utilities.getRandomLogin(), pwd),
                of(Utilities.getRandomLogin(), Utilities.getRandomPwd())
        );
    }

    @ParameterizedTest (name = "Проверка логина без одного из/ без заполнения обоих полей и с невалидными credentials")
    @MethodSource("provideInvalidCredentials")
    public void invalidLoginTest(String login, String pwd){
        ValidatableResponse response = services.login(login, pwd);
        AuthResponse invalidLogin = response.extract().body().as(AuthResponse.class);
        assertAll(
                ()-> assertEquals(401, response.extract().statusCode()),
                ()-> assertFalse(invalidLogin.isSuccessful()),
                ()-> assertEquals("email or password are incorrect", invalidLogin.getMessage())
        );
    }

    @Test
    @DisplayName("Проверка получения информации об аккаунте с полученным после регистрации токеном")
    public void accessTokenAndGetUserCreatedTest() {
        ValidatableResponse getUserInfo = services.readUser(accessToken);
        AuthResponse userInfo = getUserInfo.extract().body().as(AuthResponse.class);
        User user = userInfo.getUser();
        assertAll(
                ()-> assertEquals(200, getUserInfo.extract().statusCode()),
                ()-> assertTrue(userInfo.isSuccessful()),
                ()-> assertTrue(login.equalsIgnoreCase(user.getEmail())),
                ()-> assertEquals(name, user.getName())
        );
    }

    @Test
    @DisplayName("Проверка выхода из аккаунта с refreshToken, подтверждение, что accessToken перестает работать после выхода из аккаунта")
    @Description("Выпущенный при регистрации токен продолжает действовать после выхода из аккаунта")
    public void refreshTokenTest() {
        String refreshToken = services.getRefreshToken(createUser);
        ValidatableResponse logout = services.logout(refreshToken);
        ValidatableResponse getUserInfo = services.readUser(accessToken);
        assertAll(
                ()-> assertEquals(200, logout.extract().statusCode()),
                ()-> assertEquals(401, getUserInfo.extract().statusCode())
        );
    }

    @Test
    @DisplayName("Проверка выпуска новой пары токенов и что прежний токен перестает действовать после нового логина")
    @Description("Выпущенный при регистрации токен продолжает действовать после нового логина")
    public void tokensReIssueAfterNewLoginTest() {
        String refreshToken1 = services.getRefreshToken(createUser);
        services.logout(refreshToken1);
        ValidatableResponse newLogin =  services.login(login, pwd);
        AuthResponse authResponse = newLogin.extract().body().as(AuthResponse.class);
        String newToken = services.getAccessToken(newLogin);
        ValidatableResponse getUserInfo = services.readUser(accessToken);
        assertAll(
                ()-> assertNotEquals(accessToken, newToken),
                ()-> assertNotEquals(refreshToken1, authResponse.getRefreshToken()),
                ()-> assertEquals(401, getUserInfo.extract().statusCode())
        );
    }

    @Test
    @DisplayName("Проверка доступа к аакаунту без авторизации")
    public void accessToAccountWithoutTokenTest() {
        ValidatableResponse getUserInfo = services.readUser("");
        assertEquals(401, getUserInfo.extract().statusCode());
    }


    @Test
    @DisplayName("Проверка обновления токена запросом на обновление с refreshToken в теле")
    @Description("Прежний токен продолжает работать после обновления токенов")
    public void tokensReIssueAfterTokenUpdateTest() {
        String refreshToken1 = services.getRefreshToken(createUser);
        ValidatableResponse updateToken =  services.updateToken(refreshToken1);
        ValidatableResponse getUserInfo = services.readUser(accessToken);
        assertAll(
                ()-> assertEquals(200, updateToken.extract().statusCode()),
                ()-> assertEquals(401, getUserInfo.extract().statusCode())
        );
    }

    @Test
    @DisplayName("Проверка редактирования пользователя: поля имя и email")
    public void editUser() {
        String newName = Utilities.getRandomString(15);
        String newLogin = Utilities.getRandomLogin().toLowerCase();
        User user = new User(newLogin).setName(newName);
        ValidatableResponse editUser = services.updateUser(accessToken, user);
        AuthResponse updatedUser = editUser.extract().body().as(AuthResponse.class);
        User userEdited = updatedUser.getUser();
        assertAll(
                ()-> assertEquals(200, editUser.extract().statusCode()),
                ()-> assertTrue(updatedUser.isSuccessful()),
                ()-> assertEquals(newLogin, userEdited.getEmail()),
                ()-> assertEquals(newName, userEdited.getName())
        );
    }

    @Test
    @DisplayName("Проверка редактирования пользователя: изменение пароля")
    public void editUserPwd() {
        String newPwd = Utilities.getRandomPwd();
        User user = new User().setPassword(newPwd);
        ValidatableResponse editUser = services.updateUser(accessToken, user);
        ValidatableResponse loginWithNewPwd = services.login(login, newPwd);
        ValidatableResponse loginWithOldPwd = services.login(login, pwd);
        assertAll(
                ()-> assertEquals(200, editUser.extract().statusCode()),
                ()-> assertEquals(200, loginWithNewPwd.extract().statusCode()),
                ()-> assertEquals(401, loginWithOldPwd.extract().statusCode())
        );
    }

    private static Stream<Arguments> provideInvalidTokens(){
        return Stream.of(
                of("", 401),
                of(Utilities.getFakedToken(), 403)
        );
    }

    @ParameterizedTest (name = "Проверка редактирования пользователя без токена и с невалидным токеном")
    @MethodSource("provideInvalidTokens")
    public void editUserWithInvalidToken(String token, int statusCode) {
        String newName = "newName";
        User user = new User().setName(newName);
        ValidatableResponse editUser = services.updateUser(token, user);
        AuthResponse updatedUser = editUser.extract().body().as(AuthResponse.class);
        assertAll(
                () -> assertEquals(statusCode, editUser.extract().statusCode()),
                () -> assertFalse(updatedUser.isSuccessful())
        );
    }

    @AfterEach
    public void deleteTestUser() {
        services.deleteUser(accessToken);
    }

}
