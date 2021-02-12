package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.repository.CitizenRepository;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CitizenRepositoryImpl implements CitizenRepository {

    private final HermesKeycloak client;

    @Value("${keycloak.realm}")
    private String realm;

    public CitizenRepositoryImpl(HermesKeycloak client) {
        this.client = client;
    }


    @Override
    public Optional<UserRepresentation> findById(String citizenId) {
        return Optional.ofNullable(client
        .realm(realm)
        .users()
        .get(citizenId)
        .toRepresentation());
    }

    @Override
    public List<UserRepresentation> findAll(int offset, int limit) {
        return client
                .citizens()
                .list(offset,limit);
    }

    @Override
    public Integer count() {
        return client
                .citizens()
                .count();
    }

    @Override
    public boolean save(UserRepresentation userRepresentation) {
        userRepresentation.setGroups(List.of("CITIZENS"));

        var response = client
                .realm(realm)
                .users()
                .create(userRepresentation);

        if(response.getStatus() == 409)
            throw new ConflictException("Please ensure that there isn't any organization with the name " + userRepresentation.getFirstName() + "" + userRepresentation.getLastName());
        if(response.getStatus() != 201)
            throw new RuntimeException("Save failed");

        return true;
    }

    @Override
    public boolean update(UserRepresentation userRepresentation) {
        client
                .realm(realm)
                .users()
                .get(userRepresentation.getId())
                .update(userRepresentation);

        return true;
    }

    @Override
    public boolean delete(UserRepresentation userRepresentation) {
        client
                .citizens()
                .manage()
                .members()
                .remove(userRepresentation);

        return true;
    }
}
