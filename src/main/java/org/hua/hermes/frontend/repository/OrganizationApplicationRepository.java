package org.hua.hermes.frontend.repository;

import org.hua.hermes.backend.entity.Application;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface OrganizationApplicationRepository
{
    List<Application> findAll(int offset,int limit) throws URISyntaxException;
    Optional<Application> findById(String id) throws URISyntaxException;
    Integer count();
    boolean update(Application application);
}
