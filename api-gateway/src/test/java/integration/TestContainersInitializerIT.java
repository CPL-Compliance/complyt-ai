package integration;

import com.github.dockerjava.api.model.ContainerNetwork;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import integration.test_utils.TestUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public abstract class TestContainersInitializerIT {

    protected static final String HOSTNAME = "test-host";

    // Image versions
    protected static final String MONGO_IMAGE = "mongo:6.0.10";
    protected static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:21.0";
    protected static final String FAKE_GCS_IMAGE = "fsouza/fake-gcs-server:latest";

    // Services
    protected static final String DISCOVERY_SERVICE = "discovery-service";
    protected static final String SALES_TAX = "sales-tax";
    protected static final String SALES_TAX_RATES = "sales-tax-rates";
    protected static final String ADDRESS_VALIDATION = "address-validation";
    protected static final String FILES = "files";
    protected static final String AUTHENTICATION = "authentication";
    protected static final String API_GATEWAY = "api-gateway";

    // Containers
    protected static final MongoDBContainer MONGO_CONTAINER;
    protected static final GenericContainer DISCOVERY_CONTAINER;
    protected static final GenericContainer SALES_TAX_CONTAINER;
    protected static final GenericContainer SALES_TAX_RATES_CONTAINER;
    protected static final GenericContainer ADDRESS_VALIDATION_CONTAINER;
    protected static final GenericContainer FILES_CONTAINER;
    protected static final GenericContainer AUTHENTICATION_CONTAINER;
    protected static final GenericContainer API_GATEWAY_CONTAINER;
    protected static final KeycloakContainer KEYCLOAK_CONTAINER;

    protected static final GenericContainer FAKE_GCS_CONTAINER;


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

    protected static String fakeGcsExternalUrl;

    static {

        NETWORK = Network.newNetwork();

        // OAuth Server
        KEYCLOAK_CONTAINER = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withNetwork(NETWORK)
                .withNetworkAliases(HOSTNAME)
                .withRealmImportFile("realm-export.json")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("keycloak_container"));
        ;
        KEYCLOAK_CONTAINER.start();
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
                .withNetworkAliases(HOSTNAME)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("mongo_container"));
        ;
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(SALES_TAX), "/" + dumpPath(SALES_TAX), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(SALES_TAX_RATES), "/" + dumpPath(SALES_TAX_RATES), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(FILES), "/" + dumpPath(FILES), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(AUTHENTICATION), "/" + dumpPath(AUTHENTICATION), BindMode.READ_ONLY);
        MONGO_CONTAINER.addFileSystemBind("../mongodump/" + dumpPath(ADDRESS_VALIDATION), "/" + dumpPath(ADDRESS_VALIDATION), BindMode.READ_ONLY);

        startContainer(MONGO_CONTAINER);

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
                "java", "-Dspring.profiles.active=integration-test, complytTaxEngine, complytStubCurrency, stubVatValidation",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        SALES_TAX_CONTAINER.start();

        //Sales Tax Rates Container
        SALES_TAX_RATES_CONTAINER = initializeServiceContainer(SALES_TAX_RATES,
                "java", "-Dspring.profiles.active=integration-test, internalRatesSystemTestProfile",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        SALES_TAX_RATES_CONTAINER.start();

        //Address Validation Container
        ADDRESS_VALIDATION_CONTAINER = initializeServiceContainer(ADDRESS_VALIDATION,
                "java", "-Dspring.profiles.active=integration-test, stubHere",
                mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        ADDRESS_VALIDATION_CONTAINER.start();

        FAKE_GCS_CONTAINER = new GenericContainer<>(DockerImageName.parse(FAKE_GCS_IMAGE))
                .withNetwork(NETWORK)
                .withNetworkAliases(HOSTNAME)
                .withExposedPorts(4443)
                .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint(
                        "/bin/fake-gcs-server",
                        "-scheme", "http"
                ))
                .withCreateContainerCmdModifier(cmd -> cmd.withName("fake_gcs"));
        FAKE_GCS_CONTAINER.start();
        try {
            updateExternalUrlWithContainerUrl(FAKE_GCS_CONTAINER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        //Files Container
        FILES_CONTAINER = initializeServiceContainer(FILES,
                "java", "-Dspring.profiles.active=integration-test, fakeGoogleStorage",
                "-Dgoogle.storage.url=" + fakeGcsExternalUrl, mongoUriEntrypoint, discoveryUrlEntrypoint, oauthUriEntrypoint, discoveryHostEntrypoint, jwkUriEntrypoint,
                "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar");
        FILES_CONTAINER.start();

        // Restore Dump
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(SALES_TAX));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(FILES));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(SALES_TAX_RATES));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(AUTHENTICATION));
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=" + dumpPath(ADDRESS_VALIDATION));
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
                                .forHttp(TestUtilities.COMPLYT_GT_RATES_BASE_URL + "?country=Canada")
                                .withHeader("Authorization", "Bearer " + TOKEN))
                        .withStrategy(Wait
                                .forHttp(TestUtilities.FILES_BASE_URL)
                                .withHeader("Authorization", "Bearer " + TOKEN))
                        .withStrategy(Wait
                                .forHttp(TestUtilities.ADDRESS_VALIDATION_BASE_URL + "?country=USA&state=New%20York&city=New%20York&zip=10013&isPartial=true")
                                .withHeader("Authorization", "Bearer " + TOKEN))
                        .withStartupTimeout(Duration.ofSeconds(90)));
        API_GATEWAY_CONTAINER.start();

        WEB_TEST_CLIENT = WebTestClient.bindToServer().responseTimeout(Duration.ofSeconds(60)).baseUrl("http://localhost:" + API_GATEWAY_CONTAINER
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
                                .from("amazoncorretto:21-al2023")
                                .add(JAR_FILE_MAP.get(service), "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint(entrypoint)
                        ))
                .withNetwork(NETWORK)
                .withNetworkAliases(HOSTNAME)
                .withCreateContainerCmdModifier(cmd -> cmd.withName(service + "-container"));
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

    private static void startContainer(GenericContainer container) {
        container.start();
        container.followOutput(new Slf4jLogConsumer(log));
    }

    protected static void makeScriptRunnable(GenericContainer container, String... scriptAndArgs) throws IOException, InterruptedException {
        container.execInContainer("cp", scriptAndArgs[0] + ".origin", scriptAndArgs[0]);
        container.execInContainer("chmod", "+x", scriptAndArgs[0]);
    }

    private static void updateExternalUrlWithContainerUrl(GenericContainer fakeGcsContainer) throws Exception {
        String ip_addr = ((ContainerNetwork) fakeGcsContainer
                .getCurrentContainerInfo()
                .getNetworkSettings()
                .getNetworks()
                .values()
                .toArray()[0])
                .getIpAddress();
        int tc_port = fakeGcsContainer.getMappedPort(4443);

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + tc_port + "/storage/v1/b"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"test\"}"))
                    .build();
            HttpResponse<Void> response = HttpClient.newBuilder().build()
                    .send(req, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "error updating fake-gcs-server with external url, response status code " + response.statusCode() + " != 200");
            }
            fakeGcsExternalUrl = "http://" + ip_addr + ":4443";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}