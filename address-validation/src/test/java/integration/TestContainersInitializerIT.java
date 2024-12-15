package integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public abstract class TestContainersInitializerIT {

    protected static final String MONGO_IMAGE = "mongo:6.0.10";
    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
            .withExposedPorts(27017);

    static {
        MONGO_CONTAINER.addFileSystemBind("../mongodump/address-validation.dump", "/address-validation.dump", BindMode.READ_ONLY);
        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(log));

        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=address-validation.dump");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}