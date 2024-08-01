package org.example.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.Set;

public class ValidationAssertion {
    public static void assertValidSchema(JsonNode node, String schemaFile) throws Exception {
        try {
            SchemaValidator schemaValidator = new SchemaValidator();
            Set<ValidationMessage> errors = schemaValidator
                    .validateSchema(schemaFile, node);
//        Assert validation did not return any error messages and log if any
            Assertions.assertTrue(errors.isEmpty(), "Schema validation failed - " + errors.toString());
        } catch (AssertionError e) {
            filterStackTrace(e);
            throw e;
        }
    }

    private static void filterStackTrace(AssertionError error) {
        StackTraceElement[] stackTrace = error.getStackTrace();
        if (null != stackTrace) {
            ArrayList<StackTraceElement> filteredStackTrace = new ArrayList<StackTraceElement>();
            for (StackTraceElement e : stackTrace) {
                if (!"org.cargurus.utils.ValidationAssertion".equals(e.getClassName())) {
                    filteredStackTrace.add(e);
                }
            }
            error.setStackTrace(filteredStackTrace.toArray(new StackTraceElement[0]));
        }
    }
}
