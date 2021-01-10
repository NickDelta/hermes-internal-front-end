package org.hua.hermes.config;

import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.hua.hermes.keycloak.client.HermesKeycloakBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClientConfig
{
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("#{environment.KEYCLOAK_ADMIN_CLIENT_ID}")
    private String clientId;

    @Value("#{environment.KEYCLOAK_ADMIN_SECRET}")
    private String clientSecret;

    @Bean
    public HermesKeycloak getKeycloakClient(){
        return HermesKeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(
                        new ResteasyClientBuilder()
                                .connectionPoolSize(10)
                                .build()
                ).build();
    }

}