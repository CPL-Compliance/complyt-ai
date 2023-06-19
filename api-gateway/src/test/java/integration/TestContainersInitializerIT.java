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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public abstract class TestContainersInitializerIT {

    // Image versions
    protected static final String MONGO_IMAGE = "mongo:5.0.15";
    protected static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:21.0";

    // Services
    protected static final String DISCOVERY_SERVICE = "discovery-service";
    protected static final String SALES_TAX = "sales-tax";
    protected static final String SALES_TAX_RATES = "sales-tax-rates";

    // Containers
    protected static final MongoDBContainer MONGO_CONTAINER;
    protected static GenericContainer DISCOVERY_CONTAINER;
    protected static GenericContainer SALES_TAX_CONTAINER;
    protected static GenericContainer SALES_TAX_RATES_CONTAINER;
    protected static KeycloakContainer KEYCLOAK_CONTAINER;

    // Tokens
    protected static String TOKEN;
    protected static String TOKEN_NO_SCOPES;
    protected static String TOKEN_DIFFERENT_TENANT;


    // Miscellaneous
    protected static boolean IS_SALES_TAX_REGISTERED;
    protected static boolean IS_SALES_TAX_RATES_REGISTERED;
    protected static WebTestClient getTokenClient;
    protected static Map<String, String> jarFileMap = new HashMap<>();

    static {

        getTokenClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080/").build();
        fetchJarFile(DISCOVERY_SERVICE);
        fetchJarFile(SALES_TAX);
        fetchJarFile(SALES_TAX_RATES);

        // OAuth Server
        KEYCLOAK_CONTAINER = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withExposedPorts(8080)
                .withCreateContainerCmdModifier(cmd -> cmd
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(8080), new ExposedPort(8080))))
                .withRealmImportFile("realm-export.json");
        KEYCLOAK_CONTAINER.start();

        //Discovery Container
        DISCOVERY_CONTAINER = initializeServiceContainer(DISCOVERY_SERVICE, 8761,
                "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        DISCOVERY_CONTAINER.start();

        // Mongo Container
        MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
                .withExposedPorts(27017)
                .withClasspathResourceMapping(dumpPath(SALES_TAX), dumpPath(SALES_TAX), BindMode.READ_ONLY)
                .withClasspathResourceMapping(dumpPath(SALES_TAX_RATES), dumpPath(SALES_TAX_RATES), BindMode.READ_ONLY);
        MONGO_CONTAINER.start();

        //Sales Tax Container
        SALES_TAX_CONTAINER = initializeServiceContainer(SALES_TAX, 9898,
                "java", "-Dspring.profiles.active=integration-test, complytStubTax",
                "-Dspring.data.mongodb.uri=mongodb://host.docker.internal:" + MONGO_CONTAINER.getMappedPort(27017),
                "-Deureka.client.serviceUrl.defaultZone=http://host.docker.internal:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka/",
                //"-Dadd-host", "host.docker.internal=host-gateway",
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        SALES_TAX_CONTAINER.start();

        //Sales Tax Rates Container
        SALES_TAX_RATES_CONTAINER = initializeServiceContainer(SALES_TAX_RATES, 9870,
                "java", "-Dspring.profiles.active=integration-test, stubFastTax",
                "-Dspring.data.mongodb.uri=mongodb://host.docker.internal:" + MONGO_CONTAINER.getMappedPort(27017),
                "-Deureka.client.serviceUrl.defaultZone=http://host.docker.internal:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka/",
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        SALES_TAX_RATES_CONTAINER.start();

        // Restore Dump
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(SALES_TAX));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(SALES_TAX_RATES));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Retrieve Tokens
        getToken("test-client", "test-user",
                receivedToken -> TOKEN = receivedToken);
        getToken("test-client-no-scope", "test-user",
                receivedToken -> TOKEN_NO_SCOPES = receivedToken);
        getToken("test-client", "test-user-different-tenant",
                receivedToken -> TOKEN_DIFFERENT_TENANT = receivedToken);
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.serviceUrl.defaultZone", () -> "http://localhost:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka");
    }

    private static void fetchJarFile(String service) {
        try {
            Files.newDirectoryStream(Paths.get(targetPath(service)), "*.jar")
                    .forEach(path -> jarFileMap.put(service, path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String targetPath(String service) {
        return "../" + service + "/target";
    }

    private static String dumpPath(String service) {
        return service + ".dump";
    }

    private static GenericContainer initializeServiceContainer(String service, int port, String... entryPoint) {
        return new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of(targetPath(service)))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add(jarFileMap.get(service), "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint(entryPoint)
                        )).withExposedPorts(port)
                .withExtraHost("host.docker.internal","host-gateway")
                .withAccessToHost(true)
                .withCreateContainerCmdModifier(cmd -> cmd
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port)))
                );
    }

    private static void getToken(String clientId, String username, Consumer<String> tokenConsumer) {
        getTokenClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder.path("/realms/test-realm/protocol/openid-connect/token")
                                .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("username", username)
                        .with("password", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").value(tokenConsumer);
    }
}