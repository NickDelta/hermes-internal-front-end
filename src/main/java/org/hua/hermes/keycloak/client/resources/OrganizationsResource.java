package org.hua.hermes.keycloak.client.resources;

import org.keycloak.representations.idm.GroupRepresentation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/realms/hermes/organizations")
public interface OrganizationsResource
{

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    List<GroupRepresentation> list(@QueryParam("offset") Integer offset,
                                   @QueryParam("limit")  Integer limit);

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    Integer count();

    @Path("/{name}")
    OrganizationResource organization(@PathParam("name") String name);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response create(String orgName);

}
