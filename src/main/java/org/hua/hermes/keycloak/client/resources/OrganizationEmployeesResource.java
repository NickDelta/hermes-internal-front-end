package org.hua.hermes.keycloak.client.resources;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.UserResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface OrganizationEmployeesResource
{
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    List<UserResource> list(@QueryParam("offset") Integer offset,
                                  @QueryParam("limit") Integer limit);

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON})
    Integer count();

    @Path("/manage")
    GroupResource manage();

    @Path("/{id}")
    UserResource employee(@PathParam("id") String userId);
}
