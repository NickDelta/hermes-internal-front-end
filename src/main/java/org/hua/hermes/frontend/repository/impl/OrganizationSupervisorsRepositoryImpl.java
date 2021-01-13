package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.error.exception.InternalServerErrorException;
import org.hua.hermes.frontend.repository.OrganizationSupervisorsRepository;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrganizationSupervisorsRepositoryImpl implements OrganizationSupervisorsRepository
{

    @Autowired
    private HermesKeycloak client;

    @Value("${keycloak.realm}")
    private String realm;

    public Optional<UserRepresentation> findById(GroupRepresentation organization, String userId)
    {
        var user = client.organizations()
                .organization(organization.getName())
                .supervisors()
                .supervisor(userId)
                .toRepresentation();

        return Optional.of(user);
    }

    public List<UserRepresentation> findAll(GroupRepresentation organization, int offset, int limit)
    {
        return client.organizations()
                .organization(organization.getName())
                .supervisors()
                .list(offset,limit);
    }


    public Integer count(GroupRepresentation organization)
    {
        return client.organizations()
                .organization(organization.getName())
                .supervisors()
                .count();
    }

    public boolean save(GroupRepresentation organization, UserRepresentation user)
    {
        user.setGroups(List.of("ORGANIZATIONS/" + organization.getName() + "/SUPERVISORS"));
        var response = client.realm(realm)
                .users()
                .create(user);

        if(response.getStatus() == 409)
            throw new ConflictException("Please ensure that there isn't any user with the same username or password.");

        //Just to be sure
        if(response.getStatus() != 201)
            throw new InternalServerErrorException("Save failed");

        return true;
    }

    public boolean update(GroupRepresentation organization, UserRepresentation user)
    {
        client.organizations()
                .organization(organization.getName())
                .supervisors()
                .supervisor(user.getId())
                .update(user);
        return true;
    }

    public boolean delete(GroupRepresentation organization, UserRepresentation user)
    {
        client.organizations()
                .organization(organization.getName())
                .supervisors()
                .supervisor(user.getId())
                .remove();
        return true;
    }


}
