package org.hua.hermes.frontend.data.repository;

public interface ObjectIdentifier<E,ID>
{
    ID getId(E entity);
}
