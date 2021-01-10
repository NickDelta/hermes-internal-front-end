package org.hua.hermes.frontend.error.view;

import com.vaadin.flow.router.Route;
import org.hua.hermes.frontend.view.HomeView;

@Route("403")
public class ForbiddenView extends ErrorView
{
    public ForbiddenView()
    {
        super("403",
                "HEY YOU! This is a restricted area.",
                "You tried to access a resource that you are not allowed to. " +
                          "You can click the button below to go back to the homepage.",
                "/images/ancient-soldier.png",
                "ancient-soldier");

        getHomeButton().addClickListener(listener -> {
            listener.getSource()
                    .getUI()
                    .ifPresent(ui -> ui.navigate(HomeView.class));
        });
    }
}
