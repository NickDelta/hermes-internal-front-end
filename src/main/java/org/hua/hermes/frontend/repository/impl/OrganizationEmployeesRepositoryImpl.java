package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.error.exception.InternalServerErrorException;
import org.hua.hermes.frontend.repository.OrganizationEmployeesRepository;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Optional;

public class OrganizationEmployeesRepositoryImpl implements OrganizationEmployeesRepository {

    private final HermesKeycloak client;

    @Value("${keycloak.realm}")
    private String realm;

    public OrganizationEmployeesRepositoryImpl(HermesKeycloak client) {
        this.client = client;
    }

    @Override
    public Optional<UserRepresentation> findById(GroupRepresentation organization, String empId) {
        return Optional.ofNullable(client
        .organizations()
        .organization(organization.getName())
        .employees()
        .employee(empId)
        .toRepresentation());
    }

    @Override
    public List<UserRepresentation> findAll(GroupRepresentation organization, int offset, int limit) {
        return client
                .organizations()
                .organization(organization.getName())
                .employees()
                .list(offset,limit);
    }

    @Override
    public Integer count(GroupRepresentation organization) {
        return client
                .organizations()
                .organization(organization.getName())
                .employees()
                .count();
    }

    @Override
    public boolean save(GroupRepresentation organization, UserRepresentation userRepresentation) {
        userRepresentation.setGroups(List.of("ORGANIZATIONS/" + organization.getName() + "/EMPLOYEES"));

        var response = client
                .realm(realm)
                .users()
                .create(userRepresentation);

        if(response.getStatus() == 409)
            throw new ConflictException("Please ensure that there isn't any employee with id " + userRepresentation.getId());
        if(response.getStatus() != 201)
            throw new InternalServerErrorException("Save failed");

        return true;
    }

    @Override
    public boolean update(GroupRepresentation organization, UserRepresentation userRepresentation) {
        client.organizations()
                .organization(organization.getName())
                .employees()
                .employee(userRepresentation.getId())
                .update(userRepresentation);
        return true;
    }

    @Override
    public boolean delete(GroupRepresentation organization, UserRepresentation userRepresentation) {
        client.organizations()
                .organization(organization.getName())
                .employees()
                .employee(userRepresentation.getId())
                .remove();
        return true;
    }
}
