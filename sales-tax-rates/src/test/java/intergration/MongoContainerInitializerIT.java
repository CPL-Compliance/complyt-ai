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
        // Bind BSON dump folder inside the container
        MONGO_CONTAINER.addFileSystemBind("../dump/sales_tax_rates",
                "/dump/sales_tax_rates",
                BindMode.READ_ONLY);

        MONGO_CONTAINER.start();
        MONGO_CONTAINER.followOutput(new Slf4jLogConsumer(log));

        try {
            // Restore MongoDB from BSON files
            MONGO_CONTAINER.execInContainer(
                    "mongorestore",
                    "--db", "sales_tax_rates",
                    "--dir", "/dump/sales_tax_rates"
            );

            log.info("✅ MongoDB restored from BSON files successfully.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to restore MongoDB from BSON files", e);
        }
    }
}
