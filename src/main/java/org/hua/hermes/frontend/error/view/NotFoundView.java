package org.hua.hermes.frontend.error.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hua.hermes.frontend.view.HomeView;

@Route("404")
@PageTitle("404 - Page Not Found")
public class NotFoundView extends ErrorView
{
    public NotFoundView()
    {
        super("404",
                "UH OH! You're lost.",
                "The page you are looking for does not exist. " +
                          "How you got here is a mystery. " +
                          "But you can click the button below to go back to the homepage.",
                "/images/ancient-person-questioning.png",
                "ancient-person-questioning");

        getHomeButton().addClickListener(listener -> {
            listener.getSource()
                    .getUI()
                    .ifPresent(ui -> ui.navigate(HomeView.class));
        });
    }
}
