FROM adoptopenjdk/openjdk11:alpine
COPY target/hermes-internal-front-end.jar app.jar
RUN addgroup -S hermesgroup && adduser -S hermes -G hermesgroup
USER hermes
EXPOSE 8082
CMD java -jar app.jar