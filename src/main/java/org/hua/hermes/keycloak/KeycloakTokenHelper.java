package org.hua.hermes.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hua.hermes.keycloak.representations.SummarizedGroup;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper class that gives instant access to attributes of Keycloak's principal.
 * This class must not be used in requests where a user can be an anonymous user.
 * @author <a href="mailto:nikosdelta@protonmail.com">Nick Dimitrakopoulos</a>
 */
@Component
@SuppressWarnings("unchecked")
public class KeycloakTokenHelper
{

    private final ObjectMapper objectMapper;

    public KeycloakTokenHelper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    public KeycloakPrincipal<KeycloakSecurityContext> getKeycloakPrincipal()
    {
        return (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public AccessToken getAccessToken()
    {
        return getKeycloakPrincipal()
                .getKeycloakSecurityContext()
                .getToken();
    }

    public SummarizedGroup getOrganization()
    {
        var organizations = getAccessToken().getOtherClaims().get("organization");
        return objectMapper.convertValue(organizations, new TypeReference<>() {});
    }
    

}
