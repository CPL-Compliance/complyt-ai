FROM openjdk
VOLUME /tmp

ADD target/discovery-service-0.0.1-SNAPSHOT.jar discovery-service-0.0.1-SNAPSHOT.jar
RUN sh -c 'touch /discovery-service-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/discovery-service-0.0.1-SNAPSHOT.jar"]