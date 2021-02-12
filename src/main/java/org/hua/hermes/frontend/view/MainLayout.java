package org.hua.hermes.frontend.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import org.hua.hermes.frontend.component.NaviMenu;
import org.hua.hermes.frontend.util.DateTimeUtils;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.FontWeight;
import org.hua.hermes.frontend.util.style.css.TextAlign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


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
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
public class MainLayout extends AppLayout implements PageConfigurator
{

    private H1 viewTitle;

    @Value("${keycloak.auth-server-url}")
    private String keycloakBaseURL;

    private Set<Locale> locales;
    private List<ZoneId> zones;


    public MainLayout(@Autowired Set<Locale> locales,
                      @Autowired List<ZoneId> zones) {

        this.locales = locales;
        this.zones = zones;

        setPrimarySection(Section.DRAWER);
        addToNavbar(createHeaderContent());
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
            menu.addNaviItem(VaadinIcon.USERS,"Citizens",CitizensView.class);
        }

        if(VaadinSecurity.check().hasRole("ROLE_ORG_SUPERVISOR")) {
            menu.addNaviItem(VaadinIcon.USERS, "Employees", EmployeesView.class);
        }

        if(VaadinSecurity.check().hasRole("ROLE_ORG_EMPLOYEE")){
            menu.addNaviItem(VaadinIcon.FILE,"Applications", ApplicationsView.class);
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

            userMenu.add(button);
        }

        if(VaadinSecurity.check().isFullyAuthenticated())
        {
            var label = new Label("Welcome " + VaadinSecurity.getAuthentication().getName());
            UIUtils.setFontWeight(FontWeight.BOLD, label);
            UIUtils.setTextAlign(TextAlign.CENTER, label);

            var button = UIUtils.createButton("Manage Account", VaadinIcon.COG_O, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener( buttonClickEvent ->
                    buttonClickEvent.getSource().getUI().ifPresent(ui -> ui.getPage().setLocation(keycloakBaseURL + "/realms/hermes/account")));

            userMenu.add(label,button);
        }

        var locale = new Select<Locale>();
        locale.setLabel("Locale");
        locale.setItems(locales);

        locale.setValue(VaadinSession.getCurrent().getLocale());
        locale.addValueChangeListener(listener -> {
            VaadinSession.getCurrent().setLocale(listener.getValue());
            UI.getCurrent().getPage().reload();
        });
        locale.setItemLabelGenerator(Locale::getDisplayCountry);
        userMenu.add(new Hr(),locale);

        var zone = new Select<ZoneId>();
        zone.setLabel("Time Zone");
        zone.setItems(zones);
        zone.setValue(VaadinSession.getCurrent().getAttribute(ZoneId.class));
        zone.addValueChangeListener(listener -> {
            VaadinSession.getCurrent().setAttribute(ZoneId.class, listener.getValue());
            UI.getCurrent().getPage().reload();
        });
        zone.setItemLabelGenerator(id ->
                String.format(
                "%s%s","GMT",
                DateTimeUtils.getOffsetString(LocalDateTime.now(), id))
        );

        userMenu.add(zone,new Hr());

        if(VaadinSecurity.check().isFullyAuthenticated()){
            var button = UIUtils.createButton("Logout", VaadinIcon.LOCK, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener( buttonClickEvent ->
                    buttonClickEvent.getSource().getUI().ifPresent(ui -> ui.getPage().setLocation("/sso/logout")));

            userMenu.add(button);
        }

        return image;
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("rel", "shortcut icon");
        settings.addLink("icons/icon.png", attributes);
    }
}
