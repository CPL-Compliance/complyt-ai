package integration;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;

@Slf4j
public abstract class TestContainersInitializerIT {

    protected static final String MONGO_IMAGE = "mongo:5.0.15";
    protected static final MongoDBContainer MONGO_CONTAINER;
    protected static GenericContainer DISCOVERY_CONTAINER;
    protected static GenericContainer SALES_TAX_CONTAINER;
    protected static GenericContainer SALES_TAX_RATES_CONTAINER;
    protected static KeycloakContainer KEYCLOAK_CONTAINER;
    protected static boolean IS_SALES_TAX_REGISTERED;
    protected static String TOKEN;
    protected static String TOKEN_NO_SCOPES;
    protected static String TOKEN_DIFFERENT_TENANT;

    static {
        // OAuth Server
        KEYCLOAK_CONTAINER = new KeycloakContainer()
                .withExposedPorts(8080)
                .withCreateContainerCmdModifier(cmd -> cmd
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(8080), new ExposedPort(8080))))
                .withRealmImportFile("realm-export.json");
        KEYCLOAK_CONTAINER.start();

        //Discovery Container
        DISCOVERY_CONTAINER = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of("../discovery-service"))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add("target/discovery-service-0.11.0-SNAPSHOT.jar", "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint("java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"))
        ).withExposedPorts(8761);
        DISCOVERY_CONTAINER.start();

        // Mongo Container
        MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
                .withExposedPorts(27017)
                .withClasspathResourceMapping("sales_tax.dump", "sales_tax.dump", BindMode.READ_ONLY);
        MONGO_CONTAINER.start();

        //Sales Tax Container
        SALES_TAX_CONTAINER = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of("../sales-tax/target"))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add("sales-tax-0.15.1-SNAPSHOT.jar", "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint("java", "-Dspring.profiles.active=integration-test, complytStubTax",
                                        "-Dspring.data.mongodb.uri=mongodb://host.docker.internal:" + MONGO_CONTAINER.getMappedPort(27017),
                                        "-Deureka.client.serviceUrl.defaultZone=http://host.docker.internal:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka/",
                                        "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar")
                        )).withExposedPorts(9898)
                .withAccessToHost(true)
                .withCreateContainerCmdModifier(cmd -> cmd
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(9898), new ExposedPort(9898)))
                );
        SALES_TAX_CONTAINER.start();

        //Sales Tax Rates Container
        SALES_TAX_RATES_CONTAINER = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of("../sales-tax-rates/target"))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add("sales-tax-rates-0.0.2-SNAPSHOT.jar", "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint("java", "-Dspring.profiles.active=integration-test, stubFastTax",
                                        "-Dspring.data.mongodb.uri=mongodb://host.docker.internal:" + MONGO_CONTAINER.getMappedPort(27017),
                                        "-Deureka.client.serviceUrl.defaultZone=http://host.docker.internal:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka/",
                                        "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar")
                        )).withExposedPorts(9870)
                .withAccessToHost(true)
                .withCreateContainerCmdModifier(cmd -> cmd
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(9870), new ExposedPort(9870)))
                );
        SALES_TAX_RATES_CONTAINER.start();

        // Restore Dump
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales_tax.dump");
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales_tax_rates.dump");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Retrieve Token
        WebTestClient getTokenClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080/").build();
        getTokenClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder.path("/realms/test-realm/protocol/openid-connect/token")
                                .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", "test-client")
                        .with("username", "test-user")
                        .with("password", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").value(receivedToken ->
                        TOKEN = receivedToken.toString());

        // Retrieve Token With No Scopes
        getTokenClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder.path("/realms/test-realm/protocol/openid-connect/token")
                                .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", "test-client-no-scope")
                        .with("username", "test-user")
                        .with("password", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").value(receivedToken ->
                        TOKEN_NO_SCOPES = receivedToken.toString());

        // Retrieve Token With Different Tenant
        getTokenClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder.path("/realms/test-realm/protocol/openid-connect/token")
                                .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", "test-client")
                        .with("username", "test-user-different-tenant")
                        .with("password", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").value(receivedToken ->
                        TOKEN_DIFFERENT_TENANT = receivedToken.toString());
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.serviceUrl.defaultZone", () -> "http://localhost:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka");
    }
}