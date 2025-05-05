package integration;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import test_utils.BaseTestClass;

@Slf4j
public abstract class TestContainersInitializerIT extends BaseTestClass {
    protected static final String MONGO_IMAGE = "mongo:6.0.10";
    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
            .withExposedPorts(27017);

    static {
        // Bind BSON dump folder inside the container
        MONGO_CONTAINER.addFileSystemBind("../dump/address_validation",
                "/dump/address_validation",
                BindMode.READ_ONLY);

        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(log));

        try {
            // Restore MongoDB from BSON files
            MONGO_CONTAINER.execInContainer(
                    "mongorestore",
                    "--db", "address_validation",
                    "--dir", "/dump/address_validation"
            );

            log.info("✅ MongoDB restored from BSON files successfully.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to restore MongoDB from BSON files", e);
        }
    }
}
