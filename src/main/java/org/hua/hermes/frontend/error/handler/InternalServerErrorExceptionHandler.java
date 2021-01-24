package org.hua.hermes.frontend.error.handler;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.InternalServerError;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;


@Tag(Tag.DIV)
@Log4j2
public class InternalServerErrorExceptionHandler extends InternalServerError
{

  @Override
  public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter)
  {
    log.error(parameter.getException());
    event.rerouteTo("500");
    return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
  }

}
