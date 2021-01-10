package org.hua.hermes.frontend.repository;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import java.util.List;
import java.util.Optional;

public interface OrganizationEmployeesRepository {
      Optional<UserResource> findById(GroupRepresentation organization, String empName);
      List<UserResource> findAll(GroupRepresentation organization, int offset, int limit);
      Integer count(GroupRepresentation organization);
      boolean save(ClientRepresentation clientRepresentation);
      boolean update(ClientRepresentation clientRepresentation);
      boolean delete(ClientRepresentation clientRepresentation);
}
