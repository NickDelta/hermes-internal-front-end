package org.hua.hermes.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hua.hermes.keycloak.representations.HierarchicalGroup;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public AccessToken getToken()
    {
        return getKeycloakPrincipal()
                .getKeycloakSecurityContext()
                .getToken();
    }

    public HierarchicalGroup getGroup()
    {
        var organizations = getToken().getOtherClaims().get("organizations");
        return objectMapper.convertValue(organizations, new TypeReference<List<HierarchicalGroup>>() {})
                .stream()
                .findFirst()
                .get(); //Current limitation of this project is that every user belongs to 1 group only
    }

}
