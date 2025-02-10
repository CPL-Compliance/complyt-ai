package integration;

import com.google.cloud.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public abstract class TestContainersInitializerIT {

    protected static final Logger LOGGER = log;

    protected static final String MONGO_IMAGE = "mongo:5.0.15";


    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
            .withExposedPorts(27017);


    protected static Storage storageClient;
    protected static WebTestClient WEB_TEST_CLIENT;

    static {
        // Bind BSON dump folder inside the container
        MONGO_CONTAINER.addFileSystemBind("../dump/files",
                "/dump/files",
                BindMode.READ_ONLY);

        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(log));

        try {
            // Restore MongoDB from BSON files
            MONGO_CONTAINER.execInContainer(
                    "mongorestore",
                    "--db", "files",
                    "--dir", "/dump/files"
            );

            log.info("✅ MongoDB restored from BSON files successfully.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to restore MongoDB from BSON files", e);
        }
    }
}
