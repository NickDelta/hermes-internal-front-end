package org.hua.hermes.frontend.view.presenter;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hua.hermes.frontend.repository.OrganizationUserRepository;
import org.hua.hermes.frontend.view.HasNotifications;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@AllArgsConstructor
@Log4j2
public class OrganizationUserCrudPresenter
{
    private final OrganizationUserRepository repository;
    private final HasNotifications view;

    public Optional<UserRepresentation> findById(GroupRepresentation organization, String empId)
            throws Exception
    {
        try {
            return execute(() -> repository.findById(organization, empId));
        } catch (NotFoundException ex) {
            return Optional.empty();
        }
    }

    public List<UserRepresentation> findAll(GroupRepresentation organization, int offset, int limit) {
        try {
            return execute(() -> repository.findAll(organization, offset, limit));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public int count(GroupRepresentation organization) {
        try {
            return execute(() -> repository.count(organization));
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean save(GroupRepresentation organization, UserRepresentation userRepresentation) {
        try {
            return execute(() -> repository.save(organization, userRepresentation));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(GroupRepresentation organization, UserRepresentation userRepresentation) {
        try {
            return execute(() -> repository.update(organization, userRepresentation));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(GroupRepresentation organization, UserRepresentation userRepresentation) {
        try {
            return execute(() -> repository.delete(organization, userRepresentation));
        } catch (Exception e) {
            return false;
        }
    }

    protected <V> V execute(Callable<V> callable) throws Exception
    {
        if(view == null) throw new IllegalStateException("View has not been set");
        try{
            return callable.call();
        } catch (ConflictException ex){
            //In case of conflict inform the user about it.
            //Unfortunately, keycloak's API doesn't return any info on what caused the conflict.
            view.showNotification("A conflict has occurred. " + ex.getMessage());
            throw ex;
        } catch (NotFoundException ex) {
            throw ex; //We don't want to show a notification in this case.
        } catch (Exception ex){
            log.error(ex);
            view.showNotification("Something went wrong. Please try executing the same action again.");
            throw ex;
        }
    }

}
