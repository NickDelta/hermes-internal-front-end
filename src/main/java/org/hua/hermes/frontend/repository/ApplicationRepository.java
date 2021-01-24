package org.hua.hermes.frontend.repository;

import org.hua.hermes.backend.entity.Application;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
{
    List<Application> findAll(int offset,int limit);
    Optional<Application> findById(String id);
    Integer count();
    boolean update(Application application);
}
