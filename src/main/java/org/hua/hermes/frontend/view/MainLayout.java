package org.hua.hermes.frontend.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import org.hua.hermes.frontend.component.NaviMenu;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.FontWeight;
import org.hua.hermes.frontend.util.style.css.TextAlign;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")
@CssImport(value = "./styles/views/main/main-view.css", include = "lumo-badge")
@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
public class MainLayout extends AppLayout
{

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);
        layout.add(createProfileImage());

        return layout;
    }

    private Component createDrawerContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/hermes_logo.png", "hermes_logo"));
        logoLayout.add(new H1("Hermes"));

        layout.add(logoLayout, createNaviMenu());
        return layout;
    }


    private static NaviMenu createNaviMenu()
    {
        NaviMenu menu = new NaviMenu();
        menu.addNaviItem(VaadinIcon.HOME,"Home",HomeView.class);

        if(VaadinSecurity.check().hasRole("ROLE_ORGS_ADMIN")){
            menu.addNaviItem(VaadinIcon.BUILDING,"Organizations", OrganizationsView.class);
        }

        if(VaadinSecurity.check().hasRole("ROLE_ORG_SUPERVISOR")) {
            menu.addNaviItem(VaadinIcon.USERS, "Employees", OrganizationManageEmployeesView.class);
        }

        return menu;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        String title = "";
        try{
            return getContent().getClass().getAnnotation(PageTitle.class).value();
        } catch (NullPointerException ex) {
            try {
                return (String)getContent().getClass().getMethod("getPageTitle").invoke(getContent());
            } catch (Exception ignored){}
        }
        return title;
    }

    private Component createProfileImage(){

        Image image = new Image("/images/user.svg","profile_image");

        ContextMenu userMenu = new ContextMenu();
        userMenu.setTarget(image);
        userMenu.setOpenOnClick(true);

        if(VaadinSecurity.check().isAnonymous())
        {
            var button = UIUtils.createButton("Login", VaadinIcon.LOCK, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener( buttonClickEvent ->
                    buttonClickEvent.getSource().getUI().ifPresent(ui -> ui.getPage().setLocation("/sso/login")));

            userMenu.addItem(button);
        }

        if(VaadinSecurity.check().isFullyAuthenticated())
        {
            var label = new Label("Welcome " + VaadinSecurity.getAuthentication().getName());
            UIUtils.setFontWeight(FontWeight.BOLD, label);
            UIUtils.setTextAlign(TextAlign.CENTER, label);

            var button = UIUtils.createButton("Logout", VaadinIcon.LOCK, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener( buttonClickEvent ->
                    buttonClickEvent.getSource().getUI().ifPresent(ui -> ui.getPage().setLocation("/sso/logout")));

            userMenu.add(label);
            userMenu.addItem(button);
        }

        return image;
    }

}
