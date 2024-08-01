package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    private String USER_NAME = "admin";
    private String USER_PASSWORD = "password123";
    private Properties properties;
    protected String authToken = "";
    protected APIRequestContext request;
    protected Playwright playwright;
    public String BASE_URL = "https://restful-booker.herokuapp.com/";

    @BeforeAll
    void beforeAll() {
//        Setup test - create Playwright instance and API request context with base url and required headers
        createPlaywright();
        createApiRequestContext();
//        Getting authentication token. Can be replaced with any other applicable auth method
        getAuthToken();
    }

    @AfterAll
    void afterAll() {
//        Clean after running test - dispose API request context and close Playwright instance
        disposeAPIRequestContext();
        closePlaywright();
    }

    void createPlaywright() {
        playwright = Playwright.create();
    }

    void createApiRequestContext() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(BASE_URL)
                .setExtraHTTPHeaders(headers));
    }

    void disposeAPIRequestContext() {
        if (request != null) {
            request.dispose();
            request = null;
        }
    }

    void closePlaywright() {
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    void getAuthToken(){
        RequestOptions options = RequestOptions.create();
        options.setHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        String jsonCredentials = "a";

        ObjectNode credentials = mapper.createObjectNode();
        credentials.put("username", USER_NAME);
        credentials.put("password", USER_PASSWORD);

        try {
            jsonCredentials = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credentials);
            APIResponse token = request.post("/auth", options.setData(jsonCredentials));
            JsonNode tokenNode = mapper.readTree(token.text());
            authToken = tokenNode.get("token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public int getRandomBookingId(){
        APIResponse bookings = request.get("/booking");
        assertTrue(bookings.ok());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingsNode = null;
        bookingsNode = mapper.readTree(bookings.text());

        Random rand = new Random();
        int randomId = bookingsNode.get(rand.nextInt(bookingsNode.size())).get("bookingid").asInt();

        return randomId;
    }
}
