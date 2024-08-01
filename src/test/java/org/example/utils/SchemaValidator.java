package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Set;


public class SchemaValidator {
    public Set<ValidationMessage> validateSchema(String schemaFileName, JsonNode responseNode) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        JsonSchema jsonSchema = factory.getSchema(
                classloader.getResourceAsStream(schemaFileName));
        Set<ValidationMessage> errors = jsonSchema.validate(responseNode);
        return errors;
    }
}
