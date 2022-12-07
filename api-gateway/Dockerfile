FROM openjdk
VOLUME /tmp

ADD target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway-0.0.1-SNAPSHOT.jar
RUN sh -c 'touch /api-gateway-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/api-gateway-0.0.1-SNAPSHOT.jar"]