package org.hua.hermes.config.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.VaadinResponse;
import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import de.codecamp.vaadin.security.spring.access.route.RouteAccessDeniedHandler;
import de.codecamp.vaadin.security.spring.autoconfigure.VaadinSecurityProperties;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationEntryPoint;
import org.keycloak.adapters.springsecurity.authentication.KeycloakCookieBasedRedirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * This is essentially the Vaadin equivalent for the {@link KeycloakAuthenticationEntryPoint}.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class KeycloakRouteAccessDeniedHandler implements RouteAccessDeniedHandler
{

  private static final Logger LOG = LoggerFactory.getLogger(KeycloakRouteAccessDeniedHandler.class);
  private String uiRootUrl;


  public KeycloakRouteAccessDeniedHandler(VaadinSecurityProperties vsp)
  {
    uiRootUrl = vsp.getUiRootUrl();
  }

  @Override
  public void handleAccessDenied(BeforeEnterEvent event)
  {
    if (!VaadinSecurity.check().isFullyAuthenticated())
    {
      String originalTarget = event.getLocation().getPathWithQueryParameters();
      if (originalTarget.equals("."))
        originalTarget = "";

      /*
       * A RequestCache won't work in Vaadin, but fortunately Keycloak can also store the original
       * target in a cookie.
       */
      VaadinResponse.getCurrent().addCookie(KeycloakCookieBasedRedirect
          .createCookieFromRedirectUrl(uiRootUrl + "/" + originalTarget));

      LOG.debug("Redirecting to Keycloak login at '{}'.", "/sso/login");

      RedirectView.redirectTo(event, "/sso/login");
    }
  }
}
