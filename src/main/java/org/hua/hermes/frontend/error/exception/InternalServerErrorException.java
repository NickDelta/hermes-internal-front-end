package org.hua.hermes.frontend.error.exception;

public class InternalServerErrorException extends RuntimeException
{
    public InternalServerErrorException(String message)
    {
        super(message);
    }

    public InternalServerErrorException(Throwable cause)
    {
        super(cause);
    }

    public InternalServerErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
