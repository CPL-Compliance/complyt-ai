FROM amazoncorretto:17-al2023-jdk
VOLUME /tmp

ADD target/authentication-0.0.1-SNAPSHOT.jar app.jar
RUN yum install -y wget
RUN wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'
RUN sh -c 'touch app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=development,auth0", "-jar", "app.jar"]
