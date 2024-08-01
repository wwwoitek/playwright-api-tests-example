package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.networknt.schema.ValidationMessage;
import org.example.utils.SchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.example.utils.ValidationAssertion.assertValidSchema;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingApiSchemaValidationTests extends BaseTest{

    //    Schema validation test
    @Test
    @DisplayName("Validate response schema for fetched booking")
    public void schemaValidation() throws Exception{
        int bookingId = getRandomBookingId();
        APIResponse booking = request.get("/booking/" + bookingId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingDataNode = mapper.readTree(booking.text());

//        Validate response schema
        SchemaValidator schemaValidator = new SchemaValidator();
        Set<ValidationMessage> errors = schemaValidator
                .validateSchema("schemas/booking-data.json", bookingDataNode);
//        Assert validation did not return any error messages and log if any
        assertTrue(errors.isEmpty(), "Schema validation failed - " + errors.toString());
    }

    //    Schema validation test with custom assertion
    @Test
    @DisplayName("Validate response schema for fetched booking - custom assertion")
    public void schemaValidation_CustomAssertion() throws Exception{
        int bookingId = getRandomBookingId();
        APIResponse booking = request.get("/booking/" + bookingId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bookingDataNode = mapper.readTree(booking.text());

//        Validate response schema with custom assertion
        assertValidSchema(bookingDataNode, "schemas/booking-data.json");
    }
}
