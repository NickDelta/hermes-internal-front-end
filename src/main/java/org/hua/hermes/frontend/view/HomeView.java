package org.hua.hermes.frontend.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.InternalServerError;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "home", layout = MainLayout.class)
@PageTitle("Hermes") //FIXME nicer title
public class HomeView extends VerticalLayout {

    public HomeView() {
        H1 heading = new H1("Welcome to Hermes"); //TODO put better content here
        add(heading);
    }

}
