package integration;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public abstract class TestContainersInitializerIT {

    protected static final Logger LOGGER = log;

    protected static final String MONGO_IMAGE = "mongo:5.0.15";

    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
            .withExposedPorts(27017)
            .withClasspathResourceMapping("sales_tax.dump", "sales_tax.dump", BindMode.READ_ONLY);

    static {
        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(log));
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales_tax.dump");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}