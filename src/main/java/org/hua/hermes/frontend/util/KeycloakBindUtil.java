package org.hua.hermes.frontend.util;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
import java.util.List;

public class KeycloakBindUtil
{
    public static void setAttribute(UserRepresentation user, String field, String value)
    {
        var attributes = user.getAttributes(); //If user doesn't have any attributes, null will be returned.
        if(attributes == null) {
            attributes = new HashMap<>();
            user.setAttributes(attributes);
        }
        if(value != null && !value.isEmpty()) //If we have an empty value and don't delete key map key then the attribute will remain
            attributes.put(field, List.of(value));
        else
            attributes.remove(field);
    }

    public static void setCredentials(UserRepresentation user, String password, boolean temporary){
        var credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setTemporary(temporary);
        credential.setValue(password);
        user.setCredentials(List.of(credential));
    }

}
