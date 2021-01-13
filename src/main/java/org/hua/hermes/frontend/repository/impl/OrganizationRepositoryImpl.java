package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.error.exception.InternalServerErrorException;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrganizationRepositoryImpl implements OrganizationRepository
{

    private final HermesKeycloak client;

    @Value("${keycloak.realm}")
    private String realm;

    public OrganizationRepositoryImpl(HermesKeycloak client)
    {
        this.client = client;
    }


    @Override
    public Optional<GroupRepresentation> findById(String orgName)
    {
        return Optional.ofNullable(client
                .organizations()
                .organization(orgName)
                .manage()
                .toRepresentation());
    }

    @Override
    public List<GroupRepresentation> findAll(int offset, int limit)
    {
        return client.organizations().list(offset,limit);
    }

    @Override
    public Integer count()
    {
        return client.organizations().count();
    }

    @Override
    public boolean save(GroupRepresentation organization)
    {
        var response = client
                .organizations()
                .create(organization.getName());

        if(response.getStatus() == 409)
            throw new ConflictException("Please ensure that there isn't any organization with the name " + organization.getName());

        //Just to be sure
        if(response.getStatus() != 201)
            throw new InternalServerErrorException("Save failed");

        return true;
    }

    @Override
    public boolean update(GroupRepresentation organization)
    {
        client.realm(realm)
                .groups()
                .group(organization.getId())
                .update(organization);
        return true;
    }

    @Override
    public boolean delete(GroupRepresentation organization)
    {
        client.realm(realm)
                .groups()
                .group(organization.getId())
                .remove();
        return true;
    }
}
