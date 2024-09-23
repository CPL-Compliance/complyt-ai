package io.complyt.files.v1.api_info;

import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.FileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RouterOperations({
        @RouterOperation(
                method = RequestMethod.PUT,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Save a complyt files",
                        operationId = "put",
                        tags = "file",
                        responses = {
                                @ApiResponse(
                                        responseCode = "201",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                        schema = @Schema(implementation = ComplytFileDto.class),
                                                        examples = {
                                                                @ExampleObject(value = SaveFilesApiInfo.complytFileMetadataExample)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "400",
                                        description = "Something is wrong with your request"
                                ),
                                @ApiResponse(
                                        responseCode = "401",
                                        description = "Unauthorized"
                                ),
                                @ApiResponse(
                                        responseCode = "403",
                                        description = "Forbidden"
                                ),
                                @ApiResponse(
                                        responseCode = "404",
                                        description = "File not found"
                                ),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface SaveFilesApiInfo {
    String complytFileMetadataExample = "{\n" +
            "        \"complytId\": \"143431c4-cdd6-4d01-95bc-cd5b17eedfc8\",\n" +
            "        \"metadata\": {\n" +
            "            \"display_name\": \"Certificate.pdf\",\n" +
            "            \"type\": \"exemption\",\n" +
            "            \"status\": \"active\"\n" +
            "        },\n" +
            "        \"tenantId\": \"org_SttAcBkK7b32w7kA\",\n" +
            "        \"updateTime\": \"2024-09-18T07:27:40.377Z\",\n" +
            "        \"createTime\": \"2024-09-18T07:27:40.377Z\",\n" +
            "        \"link\": \"/v1/complyt_files/143431c4-cdd6-4d01-95bc-cd5b17eedfc8\"\n" +
            "    }\n";
}
