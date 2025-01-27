package org.example.dataclasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;

@Data
public class BookingData {
    @JsonProperty(value = "firstname")
    private String firstName;
    @JsonProperty(value = "lastname")
    private String lastName;
    @JsonProperty(value = "totalprice")
    private int totalPrice;
    @JsonProperty(value = "depositpaid")
    private boolean depositPaid;
    @JsonProperty(value = "bookingdates")
    private BookingDates bookingDates;
    @JsonProperty(value = "additionalneeds")
    private String additionalNeeds;

    @Data
    private static class BookingDates {
        @JsonProperty(value = "checkin")
        private String checkIn;
        @JsonProperty(value = "checkout")
        private String checkOut;
    }

    public static BookingData getBookingDataFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

//        Read test data from json file
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return mapper.readValue(classloader.getResourceAsStream(filePath),
            BookingData.class);
    }
}
