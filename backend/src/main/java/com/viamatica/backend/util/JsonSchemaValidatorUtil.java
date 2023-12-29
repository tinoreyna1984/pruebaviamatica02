package com.viamatica.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class JsonSchemaValidatorUtil {

    private final JsonSchemaFactory schemaFactory;
    private final ObjectMapper objectMapper;

    public JsonSchemaValidatorUtil() {
        schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        objectMapper = new ObjectMapper();
    }

    public boolean validateJson(String json, String schemaFileName) {
        try {
            InputStream schemaInputStream = new ClassPathResource("schemas/" + schemaFileName).getInputStream();
            JsonSchema jsonSchema = schemaFactory.getSchema(schemaInputStream);

            JsonNode jsonNode = objectMapper.readTree(json);
            Set<ValidationMessage> validationResult = jsonSchema.validate(jsonNode);
            if(!validationResult.isEmpty()){
                for(ValidationMessage vm: validationResult){
                    System.out.println(vm);
                }
            }

            return validationResult.isEmpty();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}
