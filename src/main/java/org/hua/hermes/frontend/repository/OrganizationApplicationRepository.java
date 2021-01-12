package org.hua.hermes.frontend.repository;

import org.hua.hermes.backend.entity.Application;

import java.util.List;
import java.util.Optional;

public interface OrganizationApplicationRepository
{
    Optional<Application> findAll(int offset,int limit);
    List<Application> findApplicationById(String applicationId);
    Integer count();
    boolean update(String applicationId,String state);
}
