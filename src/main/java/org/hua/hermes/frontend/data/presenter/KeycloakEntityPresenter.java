package org.hua.hermes.frontend.data.presenter;

import lombok.extern.log4j.Log4j2;
import org.hua.hermes.frontend.data.repository.Repository;
import org.hua.hermes.frontend.data.presenter.exception.ConflictException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.concurrent.Callable;

@Log4j2
public class KeycloakEntityPresenter<E,ID> extends AbstractEntityPresenter<E,ID>
{

	public KeycloakEntityPresenter(Repository<E, ID> repository)
	{
		super(repository);
	}

	protected <V> V execute(Callable<V> callable) throws Exception
	{
		if(view == null) throw new IllegalStateException("View has not been set");
		try{
			return callable.call();
		} catch (BadRequestException ex){
			consumeError(ex,"Data entered cannot be processed");
			throw ex;
		} catch (NotFoundException ex){
			consumeError(ex,"Requested entity not found");
			throw ex;
		}
		catch (ConflictException ex){
			consumeError(ex,ex.getMessage());
			throw ex;
		} catch (Exception ex){
			consumeError(ex,"Internal error occurred");
			throw ex;
		}
	}

	private void consumeError(Exception ex, String message) {
		log.error(message, ex);
		view.showNotification(message);
	}

}
