package com.cargurus.dataclasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;

@Data
public class BookingData {
    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

    @Data
    private static class BookingDates {
        private String checkin;
        private String checkout;
    }

    public static BookingData getBookingDataFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

//        Read test data from json file
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return mapper.readValue(classloader.getResourceAsStream(filePath),
            BookingData.class);
    }
}
