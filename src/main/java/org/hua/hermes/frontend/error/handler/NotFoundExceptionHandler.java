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
        //FIXME lots of errors with sw.js appear on logs but they are not real 404's.
        // Application runs normally. I don't know what's causing this.
        // Disabling logging for now
        //log.error(parameter.getException());
        event.rerouteTo("404");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
