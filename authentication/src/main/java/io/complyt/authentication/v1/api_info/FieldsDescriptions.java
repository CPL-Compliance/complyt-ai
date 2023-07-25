package io.complyt.authentication.v1.api_info;

public interface FieldsDescriptions {

    /**
     * The rules for specifying descriptions for fields in our openAPI implementation are:
     * 1. if it's a single-value field (primitive types, UUID etc.), define description in the fields @Schema annotation directly.
     * 2. if it's a complex object and all objects of same class have same description, define the description in the class @Schema annotation.
     * 3. if it's an object and each instance have a different descriptions, define the description in the OpenApiConfig file
     */

    // Resources
    String TOKEN = "Contains the data to create an access token";

}
