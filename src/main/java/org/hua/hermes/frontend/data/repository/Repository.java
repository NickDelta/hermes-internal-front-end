package org.hua.hermes.frontend.data.repository;

import java.util.List;

public interface Repository<T,ID> extends ObjectIdentifier<T,ID>
{
	T findById(ID id);
	List<T> findAll(int offset, int limit);
	int count();
	boolean save(T entity);
	boolean update(T entity);
	boolean delete(T entity);
}
