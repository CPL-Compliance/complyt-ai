package integration.services.files;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.UUID;


public class ComplytFileMetadataEndpointsIT extends TestContainersInitializerIT implements ComplytFileMetadataEndpointsITTemplate {

    @Override
    @Test
    public void deleteByComplytId_ValidMetdata_Returns200() {
        //First create a file then delete it
        LinkedHashMap complytFileMetadataAsMap = WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .bodyValue(TestUtilities.complytFileSaveFileExample().build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(LinkedHashMap.class)
                .returnResult()
                        .getResponseBody();

        //Now delete it
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL + "/{complytId}")
                        .build(complytFileMetadataAsMap.get("complytId")))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .exchange()
                .expectStatus().isOk();
    }

    @Override
    @Test
    public void deleteByComplytId_InvalidMetadata_Returns404() {
        //First create a file then delete it
        LinkedHashMap complytFileMetadataAsMap = WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .bodyValue(TestUtilities.complytFileSaveFileExample().build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(LinkedHashMap.class)
                .returnResult()
                .getResponseBody();

        //Now delete it
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL + "/{complytId}")
                        .build(UUID.randomUUID().toString()))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .exchange()
                .expectStatus().isNotFound();
        }

    @Override
    @Test
    public void getAll_Exists_Returns200() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk();
    }

    @Override
    @Test
    public void getByAll_DeletedFiles_Returns200EmptyList() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL)
                        .queryParam("status","deleted")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk();
    }
}
