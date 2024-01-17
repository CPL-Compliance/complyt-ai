FROM amazoncorretto:17-al2023-jdk
VOLUME /tmp

ADD target/sales-tax-0.40.0-SNAPSHOT.jar app.jar
RUN yum install -y wget
RUN wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'
RUN sh -c 'touch app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
