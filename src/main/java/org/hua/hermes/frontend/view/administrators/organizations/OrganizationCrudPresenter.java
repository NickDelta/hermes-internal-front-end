package org.hua.hermes.frontend.view.administrators.organizations;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.view.HasNotifications;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Log4j2
public class OrganizationCrudPresenter
{
    protected final OrganizationRepository repository;

    @Setter
    protected HasNotifications view;

    public OrganizationCrudPresenter(OrganizationRepository repository)
    {
        this.repository = repository;
    }

    public List<GroupRepresentation> findAll(int offset, int limit) {
        try {
            return execute(() -> repository.findAll(offset, limit));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Optional<GroupRepresentation> findById(String id) {
        try {
            return execute(() -> repository.findById(id));
        } catch (ClientErrorException e) {
            return Optional.empty();
        }
    }

    public int count() {
        try {
            return execute(() -> repository.count());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean save(GroupRepresentation entity) {
        try {
            return execute(() -> repository.save(entity));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(GroupRepresentation entity) {
        try {
            return execute(() -> repository.update(entity));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(GroupRepresentation entity) {
        try {
            return execute(() -> repository.delete(entity));
        } catch (Exception e) {
            return false;
        }
    }

    protected <V> V execute(Callable<V> callable)
    {
        if(view == null) throw new IllegalStateException("View has not been set");
        try{
            return callable.call();
        } catch (BadRequestException ex){
            consumeError(ex,"Data entered cannot be processed");
            throw ex;
        } catch (NotFoundException ex){
            throw new com.vaadin.flow.router.NotFoundException();
        }
        catch (ConflictException ex){
            consumeError(ex,"A conflict has occurred. " + ex.getMessage());
            throw ex;
        } catch (ClientErrorException ex){
            consumeError(ex,"Internal error occurred"); //FIXME
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException();//FIXME
        }
    }

    private void consumeError(Exception ex, String message) {
        log.error(message, ex);
        view.showNotification(message);
    }
}
