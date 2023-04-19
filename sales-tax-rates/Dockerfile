FROM openjdk
VOLUME /tmp

ADD target/sales-tax-rates-0.0.1-SNAPSHOT.jar sales-tax-rates-0.0.1-SNAPSHOT.jar
RUN sh -c 'touch /sales-tax-rates-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Dspring.profiles.active=demo, fastTax", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/sales-tax-rates-0.0.1-SNAPSHOT.jar"]