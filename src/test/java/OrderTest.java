import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.nomoreparties.stellarburgers.RequestServices;
import site.nomoreparties.stellarburgers.Utilities;
import site.nomoreparties.stellarburgers.pojo.*;
import site.nomoreparties.stellarburgers.pojo.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;
import static site.nomoreparties.stellarburgers.LibraryAPI.BASE_URI;

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
        RestAssured.baseURI = BASE_URI;
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
    }

    @BeforeEach()
    public void getIngredients() {
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

    @ParameterizedTest(name = "Проверка создания заказа")
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

    @ParameterizedTest(name = "Проверка невозможности создания заказа без авторизации, ингредиентов. Заказ создается без авторизации")
    @MethodSource("provideInvalidDataForOrder")
    public void createOrderNegativeTest(String token, List<String> ingredients, Integer statusCode) {
        ValidatableResponse createOrder = services.placeOrder(token, ingredients);
        assertEquals(statusCode, createOrder.extract().statusCode());
    }
    @Test
    @DisplayName("Проверка получения списка заказов")
    public void getUserOrdersTestWithAuth() {
        ValidatableResponse initialResponse = services.readOrders(accessToken);
        List<String> ordersBefore = initialResponse.extract().path("orders");
        List<String> ingredients = new ArrayList<>(List
                .of(availableIngredients.get(index1), availableIngredients.get(index2)));
        services.placeOrder(accessToken, ingredients);
        ValidatableResponse afterAddingResponse = services.readOrders(accessToken);
        List<String> ordersAfter = afterAddingResponse.extract().path("orders");
        Boolean isSuccessful = afterAddingResponse.extract().path("success");
        assertAll(
                ()-> assertEquals(200, afterAddingResponse.extract().statusCode()),
                ()-> assertTrue(isSuccessful),
                ()-> assertNotNull(afterAddingResponse.extract().path("orders._id")),
                ()-> assertEquals(1, ordersAfter.size() - ordersBefore.size())
        );
    }
    @Test
    @DisplayName("Проверка получения списка заказов без авторизации")
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
