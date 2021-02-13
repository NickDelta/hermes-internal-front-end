package org.hua.hermes.frontend.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.hua.hermes.frontend.component.FlexBoxLayout;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.size.Horizontal;
import org.hua.hermes.frontend.util.size.Right;
import org.hua.hermes.frontend.util.size.Uniform;


@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "home", layout = MainLayout.class)
@PageTitle("Hermes")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(createContent());
    }

    private Component createContent() {

        Html intro = new Html("<p>Hermes is an open-source appointment scheduler aimed " +
                "(until now at least) to serve as a demo project for a " +
                "<a href=\"https://www.keycloak.org\">Keycloak</a> " +
                "+ <a href=\"https://spring.io/projects/spring-boot\">Spring Boot</a> " +
                "+ <a href=\"https://vaadin.com/flow\">Vaadin Flow</a> web app. " +
                "It is also our project for the <b>2020 Distributed Systems Course at the " +
                "<a href=\"https://www.dit.hua.gr\">Harokopio University of Athens</a></b>. " +
                "UX is loosely based on the <b>responsive layout grid</b> " +
                "guidelines set by <a href=\"https://material.io/design/layout/responsive-layout-grid.html\">Material Design</a>. " +
                "Utilises the <a href=\"https://vaadin.com/themes/lumo\">Lumo</a> theme.</p>");

        Html features = new Html(
                "<div>" +
                    "<p>Hermes can support multiple organizations. States, for example, " +
                    "could use this software to organize their appointments among all their public services. " +
                    "That was the assignment's subject by our university. Hermes consists of 2 systems:</p>" +
                    "<ul>" +
                        "<li>" +
                            "<p>An <b>internal system</b>, where internal users can enjoy a hierarchical resource " +
                            "management experience. <b>Organization Administrators</b> can manage the system's organizations " +
                            "along with their supervisors respectively. They can also manage the citizens' user catalog. " +
                            "<b>Citizens</b> are external users and can book appointments in any of the system's organizations. " +
                            "<b>Supervisors</b> can manage their organization's employees and <b>Employees</b> can manage their " +
                            "organization's appointments. Typical stuff right?</p>" +
                        "</li>" +
                        "<li>" +
                            "An <b>external system</b>, where citizens can book and manage their appointments." +
                        "</li>" +
                    "</ul>" +
                "</div>");

        Html conclusion = new Html("<p>We know that this isn't enough in the real world. " +
                "But, we had fun making this app. Also, we think that with a little more " +
                "development effort, this app could have support for more crucial features that a " +
                "business would likely demand from an app of this kind. Our top priority was to " +
                "provide a clean, solid architecture that developers can exemplify (and probably " +
                "improve) when dealing with this tech stack.</p>");

        Html licence = new Html(
                "<div>" +
                    "<p>Copyright (C) 2021 <b>Nick Dimitakopoulos, Paraskevi-Theofania Gourgioti, Ioannis Christou</b></p>" +
                    "<p>This program is free software: you can redistribute it and/or modify " +
                    "it under the terms of the GNU General Public License as published by " +
                    "the Free Software Foundation, either version 3 of the License, or " +
                    "(at your option) any later version.</p>" +
                    "<p>This program is distributed in the hope that it will be useful, " +
                    "but <b>WITHOUT ANY WARRANTY</b>; without even the implied warranty of " +
                    "<b>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE</b>. See the " +
                    "GNU General Public License for more details.</p>" +
                    "<p>You should have received a copy of the GNU General Public License " +
                    "along with this program. If not, see the license at <a href=\"https://www.gnu.org/licenses\">https://www.gnu.org/licenses</a><p>" +
                "</div>");

        Anchor repository = new Anchor("https://github.com/NickDelta/hermes-internal-front-end", UIUtils.createButton("Go to this project's GitHub repository", VaadinIcon.EXTERNAL_LINK));
        Anchor blog = new Anchor("https://coderapper.blog", UIUtils.createButton("Visit CodeRapper Blog", VaadinIcon.EXTERNAL_LINK));

        FlexBoxLayout links = new FlexBoxLayout(repository,blog);
        links.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        links.setSpacing(Right.S);

        FlexBoxLayout content = new FlexBoxLayout(intro, features, conclusion, licence, links );
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO);
        content.setMaxWidth("840px");
        content.setPadding(Uniform.RESPONSIVE_L);
        return content;
    }

}
