FROM openjdk:11-jre-slim
COPY target/hermes-internal-front-end.jar app.jar
RUN groupadd hermes && useradd -g users -G hermes hermes
USER hermes
EXPOSE 8082
CMD java -jar app.jar