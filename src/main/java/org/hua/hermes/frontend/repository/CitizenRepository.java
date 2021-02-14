package org.hua.hermes.frontend.repository;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface CitizenRepository {
    Optional<UserRepresentation> findById(String citizenId);
    List<UserRepresentation> findAll(int offset, int limit);
    Integer count();
    boolean save(UserRepresentation userRepresentation);
    boolean update(UserRepresentation userRepresentation);
    boolean delete(UserRepresentation userRepresentation);
}
