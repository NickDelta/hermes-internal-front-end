package org.hua.hermes.frontend.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.InternalServerError;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "", layout = MainLayout.class)
@RouteAlias("home")
@PageTitle("Hermes")
public class HomeView extends VerticalLayout {

    public HomeView() {
        H1 heading = new H1("Welcome to Hermes");
        add(heading);
    }

}
