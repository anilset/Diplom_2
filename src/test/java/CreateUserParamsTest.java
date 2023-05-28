import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.nomoreparties.stellarburgers.RequestServices;
import site.nomoreparties.stellarburgers.Utilities;
import site.nomoreparties.stellarburgers.pojo.AuthResponse;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;

public class CreateUserParamsTest {
    private static RequestServices services;
    private static ValidatableResponse createUser;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        services = new RequestServices();
    }

    private static Stream<Arguments> provideDataForUser() {
        return Stream.of(
                of(Utilities.getRandomLogin(), Utilities.getRandomPwd(), ""),
                of(Utilities.getRandomLogin(), "", Utilities.getRandomString(9)),
                of("", Utilities.getRandomPwd(), Utilities.getRandomString(7))
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForUser")
    public void createUserNegativeTest(String login, String password, String name) {
        createUser = services.createUser(login, password, name);
        AuthResponse response = createUser.extract().body().as(AuthResponse.class);
        System.out.println(createUser.extract().body().asPrettyString());
        assertAll(
                ()-> assertEquals(403, createUser.extract().statusCode()),
                ()-> assertFalse(response.isSuccessful()),
                ()-> assertEquals("Email, password and name are required fields", response.getMessage())
        );
    }
}
