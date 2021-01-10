package org.hua.hermes.frontend.error.handler;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;

import javax.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
public class NotFoundExceptionHandler extends RouteNotFoundError
{
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        event.rerouteTo("404");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
