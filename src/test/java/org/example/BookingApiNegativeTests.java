package org.example;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingApiNegativeTests extends BaseTest{

    private int bookingId;

    @SneakyThrows
    @BeforeAll
    public void beforeAll() {
        super.beforeAll();
        bookingId = getRandomBookingId();
    }

    @Test
    @DisplayName("Check 404 is thrown with error message for improper endpoint")
    void statusCode404ForImproperNodeId(){
        APIResponse booking = request.get("/bookingImproper");

//        Assert response is not successful
        assertFalse(booking.ok());
//        Assert proper status code (404) and error message (Not Found) received
        assertEquals(404, booking.status());
        assertEquals("Not Found", booking.text());
    }

    @Test
    @DisplayName("Booking data cannot be updated with improper token")
    public void cannotUpdateBooking_invalidToken() throws Exception{
        APIResponse booking = request.patch("/booking/" + bookingId, RequestOptions.create()
                .setData("{ \"username\": \"Updateduser\"").setHeader("Cookie", "token=invalid_" + authToken));

        assertFalse(booking.ok());
        assertEquals(403, booking.status());
    }
}
