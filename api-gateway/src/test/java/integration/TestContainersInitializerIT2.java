//package integration;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.Testcontainers;
//import org.testcontainers.containers.BindMode;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.images.builder.ImageFromDockerfile;
//import org.testcontainers.utility.DockerImageName;
//
//import java.nio.file.Path;
//
//@Slf4j
//@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
//public abstract class TestContainersInitializerIT2 {
//
//    protected static final String MONGO_IMAGE = "mongo:5.0.15";
//    protected static final MongoDBContainer MONGO_CONTAINER;
//    protected static GenericContainer DISCOVERY_CONTAINER;
//    protected static GenericContainer SALES_TAX_CONTAINER;
//    protected static boolean isSalesTaxRegistered;
//    protected static Network network;
//
//    static {
//        network = Network.newNetwork();
//
//        //Discovery Container
//        DISCOVERY_CONTAINER = new GenericContainer<>(
//                new ImageFromDockerfile()
//                        .withFileFromPath(".", Path.of("../discovery-service"))
//                        .withDockerfileFromBuilder(builder -> builder
//                                .from("amazoncorretto:17-al2023-jdk")
//                                .add("target/discovery-service-0.11.0-SNAPSHOT.jar", "app.jar")
//                                .run("sh -c 'touch app.jar'")
//                                .entryPoint("java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"))
//        ).withExposedPorts(8761)
//                .withNetwork(network)
//                .withNetworkAliases("discovery");
//        DISCOVERY_CONTAINER.start();
//        Testcontainers.exposeHostPorts(DISCOVERY_CONTAINER.getMappedPort(8761));
//
//        // Mongo Container
//        MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
//                .withExposedPorts(27017)
//                .withClasspathResourceMapping("sales_tax.dump", "sales_tax.dump", BindMode.READ_ONLY)
//                .withNetwork(network)
//                .withNetworkAliases("mongodb");
//        MONGO_CONTAINER.start();
//
//        //Sales Tax Container
//        SALES_TAX_CONTAINER = new GenericContainer<>(
//                new ImageFromDockerfile()
//                        .withFileFromPath(".", Path.of("../sales-tax/target"))
//                        .withDockerfileFromBuilder(builder -> builder
//                                .from("amazoncorretto:17-al2023-jdk")
//                                .add("sales-tax-0.15.0-SNAPSHOT.jar", "app.jar")
//                                .run("sh -c 'touch app.jar'")
//                                .entryPoint("java", "-Dspring.profiles.active=integration-test, stubFastTax",
//                                        "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar")
//                        )).withExposedPorts(9898)
//                .withAccessToHost(true)
//        .withNetwork(network);
//        SALES_TAX_CONTAINER.start();
//
//        // Restore Dump
//        try {
//            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales_tax.dump");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("eureka.client.serviceUrl.defaultZone", () -> "http://localhost:" + DISCOVERY_CONTAINER.getMappedPort(8761) + "/eureka");
//    }
//}