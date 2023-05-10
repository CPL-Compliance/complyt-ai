package integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;

@Slf4j
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public abstract class TestContainersInitializerIT {

    protected static final String MONGO_IMAGE = "mongo:5.0.15";
    protected static final GenericContainer DISCOVERY_CONTAINER;
    protected static final GenericContainer API_GATEWAY_CONTAINER;
    protected static final MongoDBContainer MONGO_CONTAINER;
    protected static boolean isServiceRouted = true;
    protected static Network network;

    static {
        network = Network.newNetwork();

        // Discovery Container
        DISCOVERY_CONTAINER = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of("../discovery-service"))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add("target/discovery-service-0.11.0-SNAPSHOT.jar", "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint("java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"))
        ).withExposedPorts(8761)
                .withNetwork(network)
                .withNetworkAliases("discovery");
        DISCOVERY_CONTAINER.start();

        // API Gateway Container
        API_GATEWAY_CONTAINER = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withFileFromPath(".", Path.of("../api-gateway"))
                        .withDockerfileFromBuilder(builder -> builder
                                .from("amazoncorretto:17-al2023-jdk")
                                .add("target/api-gateway-0.11.0-SNAPSHOT.jar", "app.jar")
                                .run("sh -c 'touch app.jar'")
                                .entryPoint("java", "-Dspring.profiles.active=integration-test", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"))
        ).withExposedPorts(8765)
                .withNetwork(network);
        API_GATEWAY_CONTAINER.start();

        // Mongo Container
        MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
                .withExposedPorts(27017)
                .withClasspathResourceMapping("sales_tax.dump", "sales_tax.dump", BindMode.READ_ONLY);
        MONGO_CONTAINER.start();
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales_tax.dump");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
        registry.add("eureka.client.serviceUrl.defaultZone", () -> "http://localhost:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka");
    }
}