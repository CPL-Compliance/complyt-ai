FROM shaolinrabbi/complyt:v1
ENV MONGODB_USER=complyt_app
ENV MONGODB_PASSWORD=d4GVyPYBMEDfJgKp
VOLUME /tmp

ADD /target/complyt-0.0.6.jar complyt.jar
RUN sh -c 'touch /complyt.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev./urandom", "-jar", "/complyt.jar"]