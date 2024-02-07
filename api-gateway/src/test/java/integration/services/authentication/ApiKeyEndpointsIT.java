package integration.services.authentication;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class ApiKeyEndpointsIT extends TestContainersInitializerIT {
    @Test
    public void authentication_apiKey_post_clientCredentialsExists_Returns201() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.getClientCredentialsJsonExample())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.clientId").exists()
                .jsonPath("$.clientSecret").exists();
    }

    @Test
    public void authentication_apiKey_post_noJwt_Returns401() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.getNonExistingClientCredentialsJsonExample())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void authentication_apiKey_post_notSuitableJwt_Returns403() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.getNonExistingClientCredentialsJsonExample())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void authentication_apiKey_delete_sentAsURLEncoded_clientApiKeyExists_SuccessfulDeletion_Returns204() {

        WEB_TEST_CLIENT
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(TestUtilities.apiKeyUrlEncodedExample())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void authentication_apiKey_delete_sentAsJson_clientApiKeyExists_SuccessfulDeletion_Returns204() {
        WEB_TEST_CLIENT
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(TestUtilities.apiKeyJsonExample())
                .exchange()
                .expectStatus().isNoContent();
    }


    @Test
    public void authentication_apiKey_delete_sentAsURLEncoded_noApiKeySent_ReturnsClientError() {

        WEB_TEST_CLIENT
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void authentication_apiKey_delete_sentAsJson_noApiKeySent_ReturnsClientError() {

        WEB_TEST_CLIENT
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError();
    }


    @Test
    public void authentication_apiKey_delete_NoContentTypeProvidedAndSentValidValueAsJson_noApiKeySent_ReturnsClientError() {

        WEB_TEST_CLIENT
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(TestUtilities.apiKeyJsonExample())
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void authentication_apiKey_delete_NoContentTypeProvidedAndSentValidValueAsURLEncoded_noApiKeySent_ReturnsClientError() {

        WEB_TEST_CLIENT
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(TestUtilities.apiKeyUrlEncodedExample())
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
