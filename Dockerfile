FROM openjdk:11
COPY target/hermes-internal-front-end-1.0.0.jar app.jar
RUN useradd -m hermes
USER hermes
EXPOSE 8082
CMD java -jar app.jar