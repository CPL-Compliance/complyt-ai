package integration;


import com.nimbusds.jwt.JWTParser;
import io.complyt.apigateway.ApiGatewayApplication;
import io.complyt.apigateway.security.AudienceValidator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.LinkedHashMap;

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class ExampleIT extends TestContainersInitializerIT {

    @MockBean
    JwtDecoder jwtDecoder;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
//        when(jwtDecoder.decode(any())).thenReturn(Jwt.withTokenValue("token")
//                .header("typ", "JWT")
//                .issuer("https://localhost")
//                .claim("tenant_id", "it_tenant")
//                .claim("scope", "create:customer delete:customer read:customer " +
//                        "update:customer create:transaction read:transaction " +
//                        "update:transaction delete:transaction read:state " +
//                        "create:exemption update:exemption delete:exemption " +
//                        "read:exemption create:nexus read:nexus delete:nexus update:nexus read:link").build());
        //        webTestClient = WebTestClient.bindToServer().baseUrl(String.format(
//                "http://%s:%d/",
//                "localhost",
//                8765
//        )).apply(mockUser())
//                .build();
    }

    @Order(-1)
    @Test
    public void checkConnection() {
        while (!isSalesTaxRegistered) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/customers")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> isSalesTaxRegistered = status != 503);
        }
    }

    //@WithMockUser(username = "mock-user", password = "integration-test")
    @Order(1)
    @Test
    public void test1() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/customers")
                        .build())
                //.header(HttpHeaders.AUTHORIZATION, "null")
                //.headers(headers -> headers
                //.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjVLSmpnQlVGa2hGVktrVUhzTGg0bCJ9.eyJpc3MiOiJodHRwczovL2ludGVncmF0aW9uLXRlc3QtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiRUljb0hDM2xFQm1LRlhvN2ZIcXNoYnRsR1dMR1pVTGlAY2xpZW50cyIsImF1ZCI6ImNvbXBseXQtaW50ZWdyYXRpb24tdGVzdCIsImlhdCI6MTY4NDgzNTY2OCwiZXhwIjoxNjg0OTIyMDY4LCJhenAiOiJFSWNvSEMzbEVCbUtGWG83Zkhxc2hidGxHV0xHWlVMaSIsInNjb3BlIjoicmVhZDpjdXN0b21lciBjcmVhdGU6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIiwiZ3R5IjoiY2xpZW50LWNyZWRlbnRpYWxzIn0.j1L5auymR4UUVKH9u21T2cOTgE_Pbmzn4oEEMdaWuRbY2owYsllqHxbb3W1gysWzlNDDT9X5EJ2-L66AKOD8jG-cwJTdyVOVfvW0XxjtkkbsTmaoMGlekq8RFuPoGaaA0VAYrBoaFNHRTKJLmbLO-sABgXQrhsGzAEIkaYEfpml5wWZQ4zJh_VaImyIrOpLGOM8CvU3dBj2Rso2z9AtLZi6iCQnfO8z5WznTcJI5b3a0Lq7-XoIA86y0NfNwV_gUZVEz8j1Qq5H37mWVMFu8RJGtvIFGTLS7IuhKxN21PjwRoIGW9zG5n3_PVAUvs2ep4FmZDszLhJqnZkUoUn7J2Q"))
                //.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.NHVaYe26MbtOYhSKkoKYdFVomg4i8ZJd8_-RU8VNbftc4TSMb4bXP3l3YlNWACwyXPGffz5aXHc6lty1Y2t4SWRqGteragsVdZufDn5BlnJl9pdR_kdVFUsra2rWKEofkZeIC4yWytE58sMIihvo9H1ScmmVwBcQP6XETqYd0aSHp1gOa9RdUPDvoXQ5oqygTqVtxaDr6wUFKrKItgBMzWIdNZ6y7O9E0DhEPTbE9rfBo6KTFsHAZnMg4k68CDp2woYIaXbmYTWcvbzIuHO7_37GT79XdIwkm95QJ7hYC9RiwrV7mesbY4PAahERJawntho0my942XheVLmGwLMBkQ"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    System.out.println(map.toString());
                    //assertEquals("", map.get("externalId"));
                });

    }

    @Test
    @Order(4)
    public void hold() {
        while (true) ;
    }
}
