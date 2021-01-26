package org.hua.hermes.frontend.view.presenter;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.frontend.repository.ApplicationRepository;
import org.hua.hermes.frontend.view.HasNotifications;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@AllArgsConstructor
@Log4j2
public class ApplicationCrudPresenter
{
    private final ApplicationRepository repository;
    private final HasNotifications view;


    public Optional<Application> findById(String id) throws Exception {
       try{
           return execute(() -> repository.findById(id));
       } catch (HttpClientErrorException.NotFound ex){
           return Optional.empty();
       }
    }

    public List<Application> findAll(int offset, int limit) {
        try{
            return execute(() -> repository.findAll(offset, limit));
        } catch (Exception ex){
            return Collections.emptyList();
        }
    }

    public int count() {
        try{
            return execute(() -> repository.count());
        } catch (Exception ex){
            return 0;
        }
    }

    public boolean update(Application application) {
        try {
            return repository.update(application);
        } catch (HttpClientErrorException.Conflict ex) {
            return false;
        }
    }

    protected <V> V execute(Callable<V> callable) throws Exception
    {
        if(view == null) throw new IllegalStateException("View has not been set");
        try{
            return callable.call();
        } catch (HttpClientErrorException.Conflict ex){
            view.showNotification(
                    "A conflict has occurred. " +
                    "Another employee may be editing this application. " +
                    "Please refresh and try again."
            );
            throw ex;
        } catch (HttpClientErrorException.Forbidden | HttpClientErrorException.NotFound ex){
            throw ex; //We don't want to show a notification in those cases.
        } catch (Exception ex){
            log.error(ex);
            view.showNotification("Something went wrong. Please try executing the same action again.");
            throw ex;
        }
    }

}
