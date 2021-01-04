package org.hua.hermes.frontend.data.presenter;


import lombok.Getter;
import lombok.Setter;
import org.hua.hermes.frontend.data.repository.Repository;
import org.hua.hermes.frontend.view.HasNotifications;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Getter
@Setter
public abstract class AbstractEntityPresenter<E,ID>
{
	protected final Repository<E,ID> repository;
	protected HasNotifications view;

	public AbstractEntityPresenter(Repository<E, ID> repository)
	{
		this.repository = repository;
	}

	public ID getEntityId(E entity){
		return repository.getId(entity);
	}

	public List<E> findAll(int offset, int limit) {
		try {
			return execute(() -> repository.findAll(offset,limit));
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public E findById(ID id) {
		try {
			return execute(() -> repository.findById(id));
		} catch (Exception e) {
			return null;
		}
	}

	public int count() {
		try {
			return execute(() -> repository.count());
		} catch (Exception e) {
			return 0;
		}
	}

	public boolean save(E entity) {
		try {
			return execute(() -> repository.save(entity));
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(E entity) {
		try {
			return execute(() -> repository.update(entity));
		} catch (Exception e) {
			return false;
		}
	}

	public boolean delete(E entity) {
		try {
			return execute(() -> repository.delete(entity));
		} catch (Exception e) {
			return false;
		}
	}

	protected abstract <V> V execute(Callable<V> callable) throws Exception;

}
