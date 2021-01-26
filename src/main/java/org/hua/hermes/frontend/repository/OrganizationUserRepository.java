package org.hua.hermes.frontend.repository;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface OrganizationUserRepository
{
    Optional<UserRepresentation> findById(GroupRepresentation organization, String empId);
    List<UserRepresentation> findAll(GroupRepresentation organization, int offset, int limit);
    Integer count(GroupRepresentation organization);
    boolean save(GroupRepresentation organization, UserRepresentation userRepresentation);
    boolean update(GroupRepresentation organization, UserRepresentation userRepresentation);
    boolean delete(GroupRepresentation organization, UserRepresentation userRepresentation);
}
