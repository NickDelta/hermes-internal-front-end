FROM openjdk:11-jre-slim

# Install curl
RUN apt-get update && apt-get install -y curl

# Copy the jar file containing the app
COPY target/hermes-internal-front-end.jar app.jar

# Container must run in non-root mode
RUN groupadd hermes && useradd -g users -G hermes hermes
USER hermes

EXPOSE 8082
EXPOSE 9001

# Add healthcheck
HEALTHCHECK --interval=5s --timeout=20s \
  CMD curl -f http://localhost:9001/actuator/health || exit 1

CMD java -jar app.jar