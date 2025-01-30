package com.cargurus;

import com.cargurus.utils.SchemaValidator;
import com.cargurus.utils.ValidationAssertion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingApiSchemaValidationTests extends BaseTest{

    //    Schema validation test
    @Test
    @DisplayName("Validate response schema for fetched booking")
    public void bookingDataFetchedWithProperSchema() throws Exception{
        String bookingId = getRandomBookingId();
        APIResponse booking = request.get("/booking/" + bookingId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingDataNode = mapper.readTree(booking.text());

//        Validate response schema
        SchemaValidator schemaValidator = new SchemaValidator();
        Set<ValidationMessage> errors = schemaValidator
                .validateSchema("schemas/booking-data.json", bookingDataNode);
//        Assert validation did not return any error messages and log if any
        assertTrue(errors.isEmpty(), "Schema validation failed - " + errors);
    }

    //    Schema validation test with custom assertion
    @Test
    @DisplayName("Validate response schema for fetched booking - custom assertion")
    public void bookingDataFetchedWithProperSchemaCustomAssertion() throws Exception{
        String bookingId = getRandomBookingId();
        APIResponse booking = request.get("/booking/" + bookingId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingDataNode = mapper.readTree(booking.text());

//        Validate response schema with custom assertion
        ValidationAssertion.assertValidSchema(bookingDataNode, "schemas/booking-data.json");
    }
}
