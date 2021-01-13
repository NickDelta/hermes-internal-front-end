package org.hua.hermes.frontend.repository;

import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository
{
    Optional<GroupRepresentation> findById(String orgName);
    List<GroupRepresentation> findAll(int offset, int limit);
    Integer count();
    boolean save(GroupRepresentation organization);
    boolean update(GroupRepresentation organization);
    boolean delete(GroupRepresentation organization);
}
