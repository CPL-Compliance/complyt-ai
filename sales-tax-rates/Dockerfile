FROM openjdk
VOLUME /tmp

ADD target/files-0.0.1-SNAPSHOT.jar files-0.0.1-SNAPSHOT.jar
RUN sh -c 'touch /files-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Dspring.profiles.active=test", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/files-0.0.1-SNAPSHOT.jar"]