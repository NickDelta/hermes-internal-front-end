package org.hua.hermes.keycloak.client.resources;

import org.keycloak.admin.client.resource.GroupResource;

import javax.ws.rs.Path;

public interface OrganizationResource
{
    @Path("/manage")
    GroupResource manage();

    @Path("/supervisors")
    OrganizationSupervisorsResource supervisors();

    @Path("/employees")
    OrganizationEmployeesResource employees();
}
