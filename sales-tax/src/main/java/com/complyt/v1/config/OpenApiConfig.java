package com.complyt.v1.config;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.OptionalAddressDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPIConfiguration() {
        return new OpenAPI()
                .info(new Info().title("Sales-Tax API")
                        .description("Sales-Tax API")
                        .version("v0.0.1")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .components(getCustomSchemas());

    }

    private Components getCustomSchemas() {
        return new Components()
                .addSchemas("internalTimestamps", getExistingSchema(TimestampsDto.class).description(FieldsDescriptions.internal_timestamps))
                .addSchemas("externalTimestamps", getExistingSchema(TimestampsDto.class).description(FieldsDescriptions.external_timestamps))
                .addSchemas("billingAddress", getExistingSchema(OptionalAddressDto.class).description(FieldsDescriptions.billing_address))
                .addSchemas("addressOfCustomer", getExistingSchema(OptionalAddressDto.class).description(FieldsDescriptions.address_of_customer))
                .addSchemas("shippingAddress", getExistingSchema(MandatoryAddressDto.class).description(FieldsDescriptions.shipping_address));
    }

    private Schema getExistingSchema(Class className) {
        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(
                        new AnnotatedType(className).resolveAsRef(false));
        return resolvedSchema.schema;
    }
}