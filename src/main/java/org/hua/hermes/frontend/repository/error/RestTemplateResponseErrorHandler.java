package org.hua.hermes.frontend.repository.error;

import com.vaadin.flow.router.NotFoundException;
import lombok.Setter;
import org.hua.hermes.frontend.view.HasNotifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler
{

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException
    {
        return httpResponse.getStatusCode().series() == CLIENT_ERROR ||
               httpResponse.getStatusCode().series() == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException
    {
        if(httpResponse.getStatusCode().value() == 403) {
            throw createException(HttpStatus.FORBIDDEN,httpResponse);
        } else if (httpResponse.getStatusCode().value() == 404){
            throw new NotFoundException();
        } else if (httpResponse.getStatusCode().value() == 409){
            throw createException(HttpStatus.CONFLICT,httpResponse);
        } else {
            DefaultResponseErrorHandler handler = new DefaultResponseErrorHandler();
            handler.handleError(httpResponse);
        }
    }

    private HttpClientErrorException createException(HttpStatus status, ClientHttpResponse httpResponse) throws IOException
    {
        return HttpClientErrorException.create(
                status,
                httpResponse.getStatusText(),
                httpResponse.getHeaders(),
                null, null);
    }
}
