package org.hua.hermes.frontend.repository;

import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface OrganizationSupervisorsRepository
{
    Optional<UserRepresentation> findById(GroupRepresentation organization, String userId);
    List<UserRepresentation> findAll(GroupRepresentation organization, int offset, int limit);
    Integer count(GroupRepresentation organization);
    boolean save(GroupRepresentation organization, UserRepresentation user);
    boolean update(GroupRepresentation organization, UserRepresentation user);
    boolean delete(GroupRepresentation organization, UserRepresentation user);

}
