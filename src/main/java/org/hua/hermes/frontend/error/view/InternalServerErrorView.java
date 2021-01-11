package org.hua.hermes.frontend.error.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hua.hermes.frontend.view.HomeView;

@Route("500")
@PageTitle("500 - Internal Server Error")
public class InternalServerErrorView extends ErrorView
{
    public InternalServerErrorView()
    {
        super("500",
                "Oops! Something went wrong.",
                "It's not your fault. We've logged what went wrong " +
                        "and our team of philosophers will make sure something " +
                        "like this won't happen again. " +
                        "Meanwhile, you can click the button below to go back to the homepage.",
                "/images/ancient-philosopher.png",
                "ancient-philosopher");

        getHomeButton().addClickListener(listener -> {
            listener.getSource()
                    .getUI()
                    .ifPresent(ui -> ui.navigate(HomeView.class));
        });
    }
}
