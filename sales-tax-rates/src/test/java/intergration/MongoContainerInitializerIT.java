package intergration;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public abstract class MongoContainerInitializerIT {

    protected static final Logger LOGGER = log;

    protected static final String MONGO_IMAGE = "mongo:5.0.15";

    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
            .withExposedPorts(27017);

    static {
        MONGO_CONTAINER.addFileSystemBind("../mongodump/sales-tax-rates.dump", "/sales-tax-rates.dump", BindMode.READ_ONLY);
        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(log));
        try {
            MONGO_CONTAINER.execInContainer("/usr/bin/mongorestore", "--archive=sales-tax-rates.dump");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}