package org.hua.hermes.frontend.repository.impl;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.frontend.repository.ApplicationRepository;
import org.hua.hermes.frontend.repository.error.RestTemplateResponseErrorHandler;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository
{

    private final KeycloakRestTemplate client;

    @Value("${hermes.backend.url}")
    private String baseURL;

    public ApplicationRepositoryImpl(KeycloakRestTemplate client){
        this.client = client;
        this.client.setErrorHandler(new RestTemplateResponseErrorHandler());
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public List<Application> findAll(int offset, int limit) {
        var url = new URIBuilder(baseURL)
                .setPathSegments("organization","application")
                .addParameter("offset",String.valueOf(offset))
                .addParameter("limit",String.valueOf(limit))
                .build()
                .toString();

        return client.exchange(url,HttpMethod.GET,null,
                new ParameterizedTypeReference<List<Application>>(){}).getBody();
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public Optional<Application> findById(String id) {

        var url = new URIBuilder(baseURL)
                .setPathSegments("organization","application",id)
                .build()
                .toString();

        return Optional.ofNullable(client.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<Application>() {}).getBody());
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public Integer count() {
        var url = new URIBuilder(baseURL)
                .setPathSegments("organization","application","count")
                .build()
                .toString();

        return client.exchange(url,HttpMethod.GET,null,
                new ParameterizedTypeReference<Integer>(){}).getBody();
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public boolean update(Application application) {
        var url = new URIBuilder(baseURL)
                .setPathSegments("organization", "application", application.getId())
                .build()
                .toString();

        HttpEntity<Application> request = new HttpEntity<>(application);
        
        client.exchange(url, HttpMethod.PUT, request, new ParameterizedTypeReference<Application>() {});
        return true;
    }
}
