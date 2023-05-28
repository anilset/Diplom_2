import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.nomoreparties.stellarburgers.RequestServices;
import site.nomoreparties.stellarburgers.Utilities;
import site.nomoreparties.stellarburgers.pojo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderTest {
    private static RequestServices services;
    private static String login;
    private static String pwd;
    private static String name;
    private static ValidatableResponse createUser;
    private static String accessToken;
    private static List<String> availableIngredients = new ArrayList<>();
    private static Random random = new Random();
    private static int index1;
    private static int index2;

    @BeforeAll
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        services = new RequestServices();
        login = Utilities.getRandomLogin();
        pwd = Utilities.getRandomPwd();
        name = Utilities.getRandomString(3);
        createUser = services.createUser(login, pwd, name);
        accessToken = services.getAccessToken(createUser);
        List <Data> ingredientsData = services.getIngredients().extract().body()
                .as(OrderResponse.class).getData();
        for(Data item : ingredientsData){
            availableIngredients.add(item.get_id());
        }
        index1 = random.nextInt(availableIngredients.size());
        index2 = random.nextInt(availableIngredients.size());
    }

    private static Stream<Arguments> provideIngredientsForOrder() {
        return Stream.of(
                of(List.of(availableIngredients.get(index1))),
                of(List.of(availableIngredients.get(index1), availableIngredients.get(index2))),
                of(List.of(availableIngredients.get(index2)))
        );
    }

    @ParameterizedTest
    @MethodSource("provideIngredientsForOrder")
    public void createOrderPositiveTest(List<String> ingredients){
        ValidatableResponse createOrder = services.placeOrder(accessToken, ingredients);
        OrderResponse orderResponse = createOrder.extract().body().as(OrderResponse.class);
        Order order = orderResponse.getOrder();
        Owner owner = order.getOwner();
        assertAll(
                ()-> assertEquals(200, createOrder.extract().statusCode()),
                ()-> assertTrue(orderResponse.isSuccessful()),
                ()-> assertEquals(login, owner.getEmail()),
                ()-> assertNotNull(order.get_id()),
                ()-> assertNotNull(order.getPrice()),
                ()-> assertEquals(ingredients.size(), order.getIngredients().size())
        );
    }

    private static Stream<Arguments> provideInvalidDataForOrder() {
        return Stream.of(
                of("", List.of(availableIngredients.get(index1)), 401),
                of(accessToken, List.of(), 400),
                of(accessToken, List.of(Utilities.getRandomHash()), 500),
                of("", List.of(), 400)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDataForOrder")
    public void createOrderNegativeTest(String token, List<String> ingredients, Integer statusCode) {
        ValidatableResponse createOrder = services.placeOrder(token, ingredients);
        assertEquals(statusCode, createOrder.extract().statusCode());
    }
    @Test
    public void getUserOrdersTestWithAuth() {
        ValidatableResponse orders = services.readOrders(accessToken);
        Boolean isSuccessful = orders.extract().path("success");
        assertAll(
                ()-> assertEquals(200, orders.extract().statusCode()),
                ()-> assertTrue(isSuccessful)
        );
    }

    @Test
    public void getUserOrdersTestWithoutToken() {
        ValidatableResponse orders = services.readOrders("");
        AuthResponse response = orders.extract().body().as(AuthResponse.class);
        assertAll(
                ()-> assertEquals(401, orders.extract().statusCode()),
                ()-> assertEquals("You should be authorised", response.getMessage())
        );
    }

    @AfterAll
    public void deleteTestUser() {
        services.deleteUser(accessToken);
    }
}
