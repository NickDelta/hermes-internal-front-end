# Hermes Internal Front-End

## Purpose

This service aims to provide a web UI to the internal users of Hermes.

Note that Hermes is an **educational project**.

## External Dependencies

To be fully operational, this service requires:

- A Keycloak instance (and more specifically - our customized
  [hermes-keycloak](https://github.com/NickDelta/hermes-keycloak-image) image) to handle
  authentication / authorization.
- A [hermes-backend](https://github.com/NickDelta/hermes-backend) instance to handle
  appointment applications.

## Environmental Variables

- `HERMES_BACKEND_URL` (required) : An IP or FQDN that points to the backend instance.
- `KEYCLOAK_AUTH_SERVER_URL` (required): An IP or FQDN that points to the Keycloak instance.
  Note that it must be publicly accessible as users will be redirected there to login.
- `KEYCLOAK_CREDENTIALS_SECRET` (required): For security reasons, this client requires a
  secret to initiate login protocol. This secret is provided by Keycloak.
- `KEYCLOAK_ADMIN_CLIENTID` (required): The client id of a Keycloak client that has
  `realm-management` service account client roles and will be used to manage some
  resources of the server where a user does not have access.
  Note that this service account must have administrative privileges.
- `KEYCLOAK_ADMIN_CLIENTSECRET` (required): The secret of the service account client.

## Testing

This project does not have any unit, integration, or E2E tests yet.

## Observability

This service exposes some basic health check endpoints backed by `Spring Boot Actuator`:

### Docker

You can create a `HEALTHCHECK` by using the endpoint http://localhost:9001/actuator/health :

```dockerfile
HEALTHCHECK --interval=5s --timeout=20s \
  CMD curl -f http://localhost:9001/actuator/health || exit 1
```

### Kubernetes

Spring Boot Actuator detects when an app is deployed on Kubernetes, and when it does,
it exposes 2 additional endpoints which support the `readiness` and `liveness` probes in Kubernetes.

Add the following lines to your kubernetes resource file:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 9001
  initialDelaySeconds: 20
  periodSeconds: 5
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 9001
  initialDelaySeconds: 20
  periodSeconds: 5
```

Please note that on both cases, port `9001` must not be exposed to the public internet.

## Deployment Options

There are 2 deployment options:

- A `docker-compose.yaml` file can be found [here](https://github.com/NickDelta/hermes-deployment/blob/main/local_deployment/files/docker-compose.yml). It will deploy the whole application along with the backend.

- `Kubernetes` manifest files can also be found under `/k8s`.

## Documentation (in Greek)

You may find more detailed documentation (in Greek) regarding this project [here](https://github.com/NickDelta/hermes-docs/blob/main/Hermes%20-%20Front-Ends%20Software%20Architecture.pdf).

## Contributors

- Nick Dimitrakopoulos ([GitHub](https://github.com/NickDelta))
- Vivian Gourgioti ([GitHub](https://github.com/viviangourgioti))
- Thanos Apostolides ([GitHub](https://github.com/apostolides))
- Ioannis Christou ([GitHub](https://github.com/j-christou))
