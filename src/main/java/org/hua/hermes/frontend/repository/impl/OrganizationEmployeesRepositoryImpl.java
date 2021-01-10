package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.repository.OrganizationEmployeesRepository;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
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
    public Optional<UserResource> findById(GroupRepresentation organization, String empName) {
        return Optional.ofNullable(client
        .organizations()
        .organization(organization.getName())
        .employees()
        .employee(empName));
    }

    @Override
    public List<UserResource> findAll(GroupRepresentation organization, int offset, int limit) {
        return client.organizations().organization(organization.getName()).employees().list(offset,limit);
    }

    @Override
    public Integer count(GroupRepresentation organization) {
        return client.organizations().organization(organization.getName()).employees().count();
    }

    @Override
    public boolean save(ClientRepresentation clientRepresentation) {
        var response = client
                .realm(realm)
                .clients()
                .create(clientRepresentation);
        if(response.getStatus() == 409)
            throw new ConflictException("Please ensure that there isn't any employee with the name " + clientRepresentation.getName());
        return true;
    }

    @Override
    public boolean update(ClientRepresentation clientRepresentation) {
        client.realm(realm)
                .clients()
                .get(clientRepresentation.getId())
                .update(clientRepresentation);
        return true;
    }

    @Override
    public boolean delete(ClientRepresentation clientRepresentation) {
        client.realm(realm)
                .clients()
                .findByClientId(clientRepresentation.getId())
                .remove(clientRepresentation);
        return true;
    }
}
