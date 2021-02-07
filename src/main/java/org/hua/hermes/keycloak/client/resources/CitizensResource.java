package org.hua.hermes.keycloak.client.resources;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/realms/hermes/citizens")
public interface CitizensResource
{
    @Path("/manage")
    GroupResource manage();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    List<UserRepresentation> list(@QueryParam("offset") Integer offset,
                                  @QueryParam("limit") Integer limit);

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON})
    Integer count();
}
