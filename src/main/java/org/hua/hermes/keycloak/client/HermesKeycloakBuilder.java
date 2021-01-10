package org.hua.hermes.keycloak.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.keycloak.admin.client.Config;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.PASSWORD;


public class HermesKeycloakBuilder
{
    private String serverUrl;
    private String realm;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private ResteasyClient resteasyClient;
    private String authorization;

    public HermesKeycloakBuilder serverUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
        return this;
    }

    public HermesKeycloakBuilder realm(String realm)
    {
        this.realm = realm;
        return this;
    }

    public HermesKeycloakBuilder grantType(String grantType)
    {
        Config.checkGrantType(grantType);
        this.grantType = grantType;
        return this;
    }

    public HermesKeycloakBuilder username(String username)
    {
        this.username = username;
        return this;
    }

    public HermesKeycloakBuilder password(String password)
    {
        this.password = password;
        return this;
    }

    public HermesKeycloakBuilder clientId(String clientId)
    {
        this.clientId = clientId;
        return this;
    }

    public HermesKeycloakBuilder clientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
        return this;
    }

    public HermesKeycloakBuilder resteasyClient(ResteasyClient resteasyClient)
    {
        this.resteasyClient = resteasyClient;
        return this;
    }

    public HermesKeycloakBuilder authorization(String auth)
    {
        this.authorization = auth;
        return this;
    }

    /**
     * Builds a new Keycloak client from this builder.
     */
    public HermesKeycloak build()
    {
        if (serverUrl == null)
        {
            throw new IllegalStateException("serverUrl required");
        }

        if (realm == null)
        {
            throw new IllegalStateException("realm required");
        }

        if (authorization == null && grantType == null)
        {
            grantType = PASSWORD;
        }

        if (PASSWORD.equals(grantType))
        {
            if (username == null)
            {
                throw new IllegalStateException("username required");
            }

            if (password == null)
            {
                throw new IllegalStateException("password required");
            }
        } else if (CLIENT_CREDENTIALS.equals(grantType))
        {
            if (clientSecret == null)
            {
                throw new IllegalStateException("clientSecret required with grant_type=client_credentials");
            }
        }

        if (authorization == null && clientId == null)
        {
            throw new IllegalStateException("clientId required");
        }

        return new HermesKeycloak(serverUrl, realm, username, password, clientId, clientSecret, grantType, resteasyClient, authorization);
    }

    private HermesKeycloakBuilder()
    {
    }

    /**
     * Returns a new Keycloak builder.
     */
    public static HermesKeycloakBuilder builder()
    {
        return new HermesKeycloakBuilder();
    }
}