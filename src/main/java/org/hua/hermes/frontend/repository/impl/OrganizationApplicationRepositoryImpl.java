package org.hua.hermes.frontend.repository.impl;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.frontend.repository.OrganizationApplicationRepository;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class OrganizationApplicationRepositoryImpl implements OrganizationApplicationRepository {

    private KeycloakRestTemplate client;
    @Value("${hermes.backend.url}")
    private String baseURL;

    public OrganizationApplicationRepositoryImpl(KeycloakRestTemplate client){
        this.client = client;
    }

    @Override
    public List<Application> findAll(int offset, int limit) throws URISyntaxException {
        var url = new URIBuilder(baseURL)
                .setPathSegments("applications")
                .addParameter("offset",String.valueOf(offset))
                .addParameter("limit",String.valueOf(limit))
                .build().toString();

        return client.exchange(url,HttpMethod.GET,null,
                new ParameterizedTypeReference<List<Application>>(){}).getBody();
    }

    @Override
    public Optional<Application> findById(String id) throws URISyntaxException {
        var url = new URIBuilder(baseURL)
                .setPathSegments("applications","id").addParameter("id",String.valueOf(id))
                .build().toString();

        return Optional.ofNullable(client.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<Application>() {}).getBody());
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public Integer count() {
        var url = new URIBuilder(baseURL)
                .setPathSegments("applications","count")
                .build().toString();

        return client.exchange(url,HttpMethod.GET,null,
                new ParameterizedTypeReference<Integer>(){}).getBody();
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public boolean update(Application application) {
        var url = new URIBuilder(baseURL)
                .setPathSegments("applications")
                .build().toString();

        HttpEntity<Application> request = new HttpEntity<>(application);
        
        client.exchange(url, HttpMethod.PUT, request, new ParameterizedTypeReference<Application>() {});

        return true;
    }
}
