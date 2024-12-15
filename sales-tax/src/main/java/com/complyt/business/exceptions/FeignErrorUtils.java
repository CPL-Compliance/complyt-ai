package com.complyt.business.exceptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

import com.complyt.annotations.Generated;

@Generated
public class FeignErrorUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String extractErrorMessage(FeignException feignException) {
        try {
            // Parse the response body to extract the "message"
            String responseBody = feignException.contentUTF8();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.path("message").asText("Error occurred");
        } catch (Exception e) {
            // Return a default message in case of an error during parsing
            return "Error occurred while processing the response.";
        }
    }
}