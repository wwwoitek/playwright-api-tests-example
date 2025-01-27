package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import org.example.dataclasses.BookingData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingApiTests extends BaseTest {

    private int bookingId;

    @Test
    @DisplayName("Check list of bookings can be fetched")
    void listOfBookingsCanBeGet() throws Exception{
//        Example test method
        APIResponse bookings = request.get("/booking");
        assertTrue(bookings.ok());
        assertNotNull(bookings);
        assertEquals(200, bookings.status());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingsNode = null;

        bookingsNode = mapper.readTree(bookings.text());
        assertTrue(bookingsNode.isArray());
    }

//    Tests can be parametrized to perform data driven testing. In this example booking ids are not deterministic
//    so the test is flaky, but it's added as an example
    @ParameterizedTest
    @DisplayName("Check booking data can be fetched using id")
//    In this example values are declared static but there can be data provider method implemented @MethodSource,
//    csv file as a source @CsvFileSource, etc. (more to be found in Junit documentation)
    @ValueSource(ints = {686, 1027, 377})
    void bookingCanBeGetById(int bookingId) {
//        Example test method
        APIResponse booking = request.get("/booking/" + bookingId);

        assertTrue(booking.ok());
        assertNotNull(booking);
        assertEquals(200, booking.status());
    }

    @Order(1)
    @Test
    @DisplayName("Booking can be created with POST method. Response contains data sent in request")
    void bookingCanBeCreatedWithFullData() throws Exception{
        BookingData bookingData = BookingData.getBookingDataFromFile("test-data/proper-booking.json");

//        Send POST request to create booking
        APIResponse booking = request.post("/booking", RequestOptions.create().setData(bookingData));
//        Assert response is not null and proper response code received
        assertTrue(booking.ok());
        assertEquals(200, booking.status());


//      Convert response data to JSON object
        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingNode;
        JsonNode bookingDataNode = mapper.readTree(booking.text());

//        Assert bookingid is not null and convert
        assertNotNull(bookingDataNode.get("bookingid"));
        bookingId = Integer.parseInt(bookingDataNode.get("bookingid").toString());
//        Convert booking node to JSON object
        bookingNode = bookingDataNode.get("booking");

        assertEquals(bookingDataNode,bookingNode);

//        Another alternative using external library (json-unit-assertj) for asserting JSON nodes
        assertThatJson(bookingNode).isEqualTo(bookingData);
    }

    @Test
    @Order(2)
    @DisplayName("Booking data can be updated")
    public void bookingCanBeUpdated() throws Exception{
        BookingData bookingData = BookingData.getBookingDataFromFile("test-data/proper-booking.json");

        bookingData.setFirstName("UpdatedName");
        bookingData.setAdditionalNeeds("Updated additional needs");

        APIResponse booking = request.put("/booking/" + bookingId, RequestOptions.create()
                .setHeader("Cookie", "token=" + authToken).setData(bookingData));

        assertTrue(booking.ok());
        assertEquals(200, booking.status());
    }

    @Test
    @DisplayName("Booking data can be updated with partial data")
    public void bookingCanBePartiallyUpdated() throws Exception{
        BookingData bookingData = BookingData.getBookingDataFromFile("test-data/proper-booking.json");

        bookingData.setTotalPrice(999);
        APIResponse booking = request.patch("/booking/" + bookingId, RequestOptions.create()
                .setData("{ \"totalprice\": " + bookingData.getTotalPrice())
                .setHeader("Cookie", "token=" + authToken));

        assertTrue(booking.ok());
        assertEquals(200, booking.status());
    }

    @Test
    @Order(4)
    @DisplayName("Booking data can be deleted")
    public void bookingCanBeDeleted() throws Exception{

        BookingData bookingData = BookingData.getBookingDataFromFile("test-data/proper-booking.json");

//        Send POST request to create booking
        APIResponse bookingCreateResponse = request.post("/booking", RequestOptions.create().setData(bookingData));
//        Assert response is not null and proper response code received
        assertTrue(bookingCreateResponse.ok());
        assertEquals(200, bookingCreateResponse.status());

        //      Avoiding test dependency with creating data to be deleted and delete it within one test.
        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingDataNode = mapper.readTree(bookingCreateResponse.text());
        int bookingId = bookingDataNode.get("bookingid").asInt();

        APIResponse booking = request.delete("/booking/" + bookingId, RequestOptions.create()
                .setHeader("Cookie", "token=" + authToken));

        assertTrue(booking.ok());
        assertEquals(201, booking.status());
    }
}
