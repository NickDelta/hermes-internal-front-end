package org.hua.hermes.frontend.util;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
import java.util.List;

public class KeycloakBindUtils
{
    public static void setAttribute(UserRepresentation user, String field, String value)
    {
        //If user doesn't have any attributes, null will be returned.
        var attributes = user.getAttributes();
        if(attributes == null) {
            attributes = new HashMap<>();
            user.setAttributes(attributes);
        }

        //If we have an empty value and don't delete key map key then the attribute will remain
        if(value != null && !value.isEmpty())
            attributes.put(field, List.of(value));
        else
            attributes.remove(field);
    }

    public static void setCredentials(UserRepresentation user, String password, boolean temporary){

        if(password == null || password.isEmpty()) return;

        var credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setTemporary(temporary);
        credential.setValue(password);
        user.setCredentials(List.of(credential));
    }

}
