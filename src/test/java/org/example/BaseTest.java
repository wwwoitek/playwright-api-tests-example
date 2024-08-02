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

    protected String authToken = "";
    protected APIRequestContext request;
    protected Playwright playwright;
    public String BASE_URL;
    private JsonNode CONFIG;

    BaseTest(){
        CONFIG = getConfigData();

        BASE_URL = CONFIG.get("baseUrl").asText();
    }

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

    private void createPlaywright() {
        playwright = Playwright.create();
    }

    private void createApiRequestContext() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

//        Get base url from env variables if exists or the declared one
        String envUrl = System.getenv("PLAYWRIGHT_BASE_URL");
        BASE_URL = (envUrl != null) ? envUrl : BASE_URL;

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(BASE_URL)
                .setExtraHTTPHeaders(headers));
    }

    private void disposeAPIRequestContext() {
        if (request != null) {
            request.dispose();
            request = null;
        }
    }

    private void closePlaywright() {
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
        credentials.put("username", CONFIG.get("valid_user").get("username").asText());
        credentials.put("password", CONFIG.get("valid_user").get("password").asText());

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

    @SneakyThrows
    public JsonNode getConfigData(){
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        JsonNode config = mapper.readTree(classloader.getResourceAsStream("test-data/config.json"));

        return config;
    }
}
