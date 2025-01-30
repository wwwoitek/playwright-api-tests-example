package com.cargurus.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Set;


public class SchemaValidator {
    public Set<ValidationMessage> validateSchema(String schemaFileName, JsonNode responseNode) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        JsonSchema jsonSchema = factory.getSchema(
                classloader.getResourceAsStream(schemaFileName));

        return jsonSchema.validate(responseNode);
    }
}
