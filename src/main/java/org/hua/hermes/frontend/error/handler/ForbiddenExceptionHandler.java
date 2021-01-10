package org.hua.hermes.frontend.error.handler;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import de.codecamp.vaadin.security.spring.access.route.RouteAccessDeniedException;

import javax.servlet.http.HttpServletResponse;


@Tag(Tag.DIV)
public class ForbiddenExceptionHandler
  extends Component
  implements HasErrorParameter<RouteAccessDeniedException>
{

  @Override
  public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<RouteAccessDeniedException> parameter)
  {
    event.rerouteTo("403");
    return HttpServletResponse.SC_FORBIDDEN;
  }

}
