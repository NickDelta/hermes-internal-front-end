package org.hua.hermes.frontend.data.presenter.exception;

import lombok.Getter;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

@Getter
public class ConflictException extends ClientErrorException
{

    public ConflictException()
    {
        super("The resource cannot be created due to a conflict", Response.Status.CONFLICT);
    }

    public ConflictException(String message)
    {
        super(message, Response.Status.CONFLICT);
    }

}
