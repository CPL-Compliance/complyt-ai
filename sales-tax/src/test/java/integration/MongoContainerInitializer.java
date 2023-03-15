package integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public abstract class MongoContainerInitializer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MongoContainerInitializer.class);
    protected static final String MONGO_IMAGE = "mongo:5.0.15";

    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
            .withExposedPorts(27017)
            .withClasspathResourceMapping("sales_tax.dump", "sales_tax.dump", BindMode.READ_ONLY);

    static {
        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(LOGGER));
        try {
            LOGGER.info("restoring database: " + MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales_tax.dump").getStdout());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
