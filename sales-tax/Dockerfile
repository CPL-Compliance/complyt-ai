FROM openjdk
VOLUME /tmp

ADD target/sales-tax-0.11.0-SNAPSHOT.jar sales-tax-0.11.0-SNAPSHOT.jar
RUN sh -c 'touch /sales-tax-0.11.0-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Dspring.profiles.active=demo, fastTax", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/sales-tax-0.11.0-SNAPSHOT.jar"]