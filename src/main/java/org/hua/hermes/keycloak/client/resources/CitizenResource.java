package org.hua.hermes.keycloak.client.resources;

import org.keycloak.admin.client.resource.UserResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/realms/hermes/citizens")
public interface CitizenResource
{

    @Path("/{id}")
    UserResource citizen(@PathParam("id") String userId);

}
