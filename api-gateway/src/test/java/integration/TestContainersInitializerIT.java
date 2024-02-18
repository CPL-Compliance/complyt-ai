package integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import integration.test_utils.TestUtilities;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class TestContainersInitializerIT {

    protected static final String HOSTNAME = "test-host";

    // Image versions
    protected static final String MONGO_IMAGE = "mongo:5.0.15";
    protected static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:21.0";

    // Services
    protected static final String DISCOVERY_SERVICE = "discovery-service";
    protected static final String SALES_TAX = "sales-tax";
    protected static final String SALES_TAX_RATES = "sales-tax-rates";
    protected static final String FILES = "files";
    protected static final String AUTHENTICATION = "authentication";
    protected static final String API_GATEWAY = "api-gateway";

    // Containers
    protected static final MongoDBContainer MONGO_CONTAINER;
    protected static final GenericContainer DISCOVERY_CONTAINER;
    protected static final GenericContainer SALES_TAX_CONTAINER;
    protected static final GenericContainer SALES_TAX_RATES_CONTAINER;
    protected static final GenericContainer FILES_CONTAINER;
    protected static final GenericContainer AUTHENTICATION_CONTAINER;
    protected static final GenericContainer API_GATEWAY_CONTAINER;
    protected static final KeycloakContainer KEYCLOAK_CONTAINER;

    // Tokens
    protected static String TOKEN_COMPLYT_ADMIN;
    protected static String TOKEN;
    protected static String TOKEN_NO_SCOPES;
    protected static String TOKEN_DIFFERENT_TENANT;

    // Miscellaneous
    protected static final WebTestClient ACCESS_TOKEN_CLIENT;
    protected static final WebTestClient WEB_TEST_CLIENT;
    protected static final Map<String, String> JAR_FILE_MAP = new HashMap<>();
    protected static final Network NETWORK;

    static {

        NETWORK = Network.newNetwork();

        // OAuth Server
        KEYCLOAK_CONTAINER = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withNetwork(NETWORK)
                .withNetworkAliases(HOSTNAME)
                .withRealmImportFile("realm-export.json");
        KEYCLOAK_CONTAINER.start();
        System.out.println(KEYCLOAK_CONTAINER.getLogs());
        ACCESS_TOKEN_CLIENT = WebTestClient.bindToServer().baseUrl("http://localhost:" + KEYCLOAK_CONTAINER.getMappedPort(8080) + "/").build();

        String mongoUriEntrypoint = "-Dspring.data.mongodb.uri=mongodb://" + HOSTNAME + ":27017";
        String discoveryUrlEntrypoint = "-Deureka.client.serviceUrl.defaultZone=http://" + HOSTNAME + ":8761/eureka/";
        String discoveryHostEntrypoint = "-Deureka.instance.hostname=" + HOSTNAME;
        String oauthUriEntrypoint = "-Dspring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:" + KEYCLOAK_CONTAINER.getMappedPort(8080) + "/realms/test-realm";
        String jwkUriEntrypoint = "-Dspring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://" + HOSTNAME + ":8080/realms/test-realm/protocol/openid-connect/certs";

        //Discovery Container
        DISCOVERY_CONTAINER = initializeServiceContainer(DISCOVERY_SERVICE,
                "java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=single-server", "-jar", "app.jar");
        DISCOVERY_CONTAINER.start();

        // Mongo Container
        MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
                .withNetwork(NETWORK)
                .withNetworkAliases(HOSTNAME);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(SALES_TAX), "/" + dumpPath(SALES_TAX), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(SALES_TAX_RATES), "/" + dumpPath(SALES_TAX_RATES), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(FILES), "/" + dumpPath(FILES), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(AUTHENTICATION), "/" + dumpPath(AUTHENTICATION), BindMode.READ_ONLY);

        MONGO_CONTAINER.start();

        // Retrieve Tokens
        getToken("complyt-admin", "complyt-admin-test-user",
                receivedToken -> TOKEN_COMPLYT_ADMIN = receivedToken);
        getToken("test-client", "test-user",
                receivedToken -> TOKEN = receivedToken);
        getToken("test-client-no-scope", "test-user",
                receivedToken -> TOKEN_NO_SCOPES = receivedToken);
        getToken("test-client", "test-user-different-tenant",
                receivedToken -> TOKEN_DIFFERENT_TENANT = receivedToken);

        // Authentication Container
        AUTHENTICATION_CONTAINER = initializeServiceContainer(AUTHENTICATION,
                "java", "-Dspring.profiles.active=integration-test, stubAuth0",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        AUTHENTICATION_CONTAINER.start();

        //Sales Tax Container
        SALES_TAX_CONTAINER = initializeServiceContainer(SALES_TAX,
                "java", "-Dspring.profiles.active=integration-test, complytTaxEngine",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        SALES_TAX_CONTAINER.start();

        //Sales Tax Rates Container
        SALES_TAX_RATES_CONTAINER = initializeServiceContainer(SALES_TAX_RATES,
                "java", "-Dspring.profiles.active=integration-test, stubFastTax",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        SALES_TAX_RATES_CONTAINER.start();

        //Files Container
        FILES_CONTAINER = initializeServiceContainer(FILES,
                "java", "-Dspring.profiles.active=integration-test",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        FILES_CONTAINER.start();

        // Restore Dump
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(SALES_TAX));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(FILES));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(SALES_TAX_RATES));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(AUTHENTICATION));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //API Gateway Container
        API_GATEWAY_CONTAINER = initializeServiceContainer(API_GATEWAY,
                "java", "-Dspring.profiles.active=integration-test",
                discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar")
                .withExposedPorts(8765)
                .waitingFor(new WaitAllStrategy()
                        .withStrategy(Wait
                                .forHttp(TestUtilities.TRANSACTION_BASE_URL)
                                .withHeader("Authorization", "Bearer " + TOKEN))
                        .withStrategy(Wait
                                .forHttp(TestUtilities.SALES_TAX_RATES_BASE_URL + "?state=CA&zip=90210&isPartial=true")
                                .withHeader("Authorization", "Bearer " + TOKEN))
                        .withStrategy(Wait
                                .forHttp(TestUtilities.FILES_BASE_URL)
                                .withHeader("Authorization", "Bearer " + TOKEN))
                        .withStartupTimeout(Duration.ofSeconds(60)));
        API_GATEWAY_CONTAINER.start();

        WEB_TEST_CLIENT = WebTestClient.bindToServer().responseTimeout(Duration.ofSeconds(30)).baseUrl("http://localhost:" + API_GATEWAY_CONTAINER
                .getMappedPort(8765) + "/").build();
    }

    private static void fetchJarFile(String service) {
        try {
            Files.newDirectoryStream(Paths.get(targetPath(service)), "*.jar")
                    .forEach(path -> JAR_FILE_MAP.put(service, path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String targetPath(String service) {
        String currentDir = Paths.get("").toAbsolutePath().toString();
        String directory = currentDir.trim();

        /*
        In case that the integration tests are running as part of release:peform command,
        It will try reaching target directory from /home/circleci/complyt_work_directory/service/target/checkout/service
        Therefore will need to move 4 directories back
        */

        if (directory.contains("target/checkout")) {
            return "../../../../" + service + "/target";
        }
        return "../" + service + "/target";
    }

    private static String dumpPath(String service) {
        return service + ".dump";
    }

    private static GenericContainer initializeServiceContainer(String service, String... entrypoint) {
        fetchJarFile(service);

        return new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of(targetPath(service)))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add(JAR_FILE_MAP.get(service), "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint(entrypoint)
                        ))
                .withNetwork(NETWORK)
                .withNetworkAliases(HOSTNAME);
    }

    private static void getToken(String clientId, String username, Consumer<String> tokenConsumer) {
        ACCESS_TOKEN_CLIENT
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