package org.hua.hermes.config.security;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;


@Route(RedirectView.ROUTE_PATH)
public class RedirectView
       extends Composite<VerticalLayout>
       implements BeforeEnterObserver
{

  public static final String ROUTE_PATH = "redirect";
  private static final String DATA_TARGET = "RedirectTarget";


  @Override
  public void beforeEnter(BeforeEnterEvent event)
  {
    String targetUrl = (String) ComponentUtil.getData(event.getUI(), DATA_TARGET);

    getContent().removeAll();
    Div div = new Div();
    getContent().add(div);

    if (targetUrl != null)
    {
      ComponentUtil.setData(event.getUI(), DATA_TARGET, null);

      div.add("Redirecting to ");
      div.add(new Anchor(targetUrl, targetUrl));

      event.getUI().getPage().setLocation(targetUrl);
    }
    else
    {
      div.add("Redirect view entered without target.");
    }
  }

  public static void redirectTo(BeforeEnterEvent event, String targetUrl)
  {
    ComponentUtil.setData(event.getUI(), DATA_TARGET, targetUrl);

    // even though a reroute does not change the URL, the target view needs a route anyway
    if (!RouteConfiguration.forSessionScope().isPathRegistered(ROUTE_PATH))
    {
      RouteConfiguration.forSessionScope().setRoute(ROUTE_PATH, RedirectView.class);
    }

    event.rerouteTo(RedirectView.class);
  }

}
