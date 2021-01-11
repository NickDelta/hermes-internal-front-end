package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.Crud;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.componentfactory.enhancedcrud.CrudEditorPosition;
import com.vaadin.componentfactory.enhancedcrud.CrudI18n;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.OrganizationEmployeesRepository;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.FormattingConstants;
import org.hua.hermes.frontend.util.TemplateUtil;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.presenter.OrganizationEmployeeCrudPresenter;
import org.hua.hermes.frontend.view.presenter.OrganizationsCrudPresenter;
import org.hua.hermes.keycloak.KeycloakTokenHelper;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import static org.hua.hermes.frontend.constant.CrudConstants.DELETE_MESSAGE;
import static org.hua.hermes.frontend.constant.CrudConstants.DISCARD_MESSAGE;

@Route(value = RouteConstants.PAGE_ORG_EMPLOYEES, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORG_SUPERVISOR_ROLE)
public class OrganizationManageEmployeesView extends Crud<UserRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle {

    private final KeycloakTokenHelper keycloakTokenHelper;

    private final OrganizationsCrudPresenter orgPresenter;
    private GroupRepresentation organization;
    private String title = "";
    private final OrganizationEmployeeCrudPresenter organizationEmployeeCrudPresenter;

    public OrganizationManageEmployeesView(@Autowired OrganizationRepository orgRepository,
                                           @Autowired OrganizationEmployeesRepository organizationEmployeesRepository,
                                           @Autowired CrudEditor<UserRepresentation> userCrudEditor,
                                           KeycloakTokenHelper keycloakTokenHelper) {
        super(UserRepresentation.class, new Grid<>(),userCrudEditor);

        this.keycloakTokenHelper = keycloakTokenHelper;

        this.orgPresenter = new OrganizationsCrudPresenter(orgRepository);
        orgPresenter.setView(this);

        this.organizationEmployeeCrudPresenter = new OrganizationEmployeeCrudPresenter(organizationEmployeesRepository);
        organizationEmployeeCrudPresenter.setView(this);

        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> organizationEmployeeCrudPresenter.findAll(organization,fetch.getOffset(),fetch.getLimit()).stream(),
                count -> organizationEmployeeCrudPresenter.count(organization)));

        this.setEditorPosition(CrudEditorPosition.ASIDE);

        Button backButton = UIUtils.createButton("Back", VaadinIcon.ARROW_LEFT, ButtonVariant.LUMO_PRIMARY);
        backButton.getStyle().set("position","absolute");
        backButton.getStyle().set("bottom","10px");
        backButton.getStyle().set("left","10px"); //I waited for the buttons not to be aligned but somehow they are with this style

        backButton.addClickListener(listener -> UI.getCurrent().navigate(RouteConstants.PAGE_ORG_SUPERVISORS));

        Button newItemButton = UIUtils.createButton("New " + UserEntityConstants.EMPLOYEE_NAME, ButtonVariant.LUMO_PRIMARY);
        newItemButton.addClickListener(e -> this.edit(new UserRepresentation(), Crud.EditMode.NEW_ITEM));

        this.setToolbar(backButton, newItemButton);

        //Functional requirements don't have a user delete option so far
        this.getDelete().setEnabled(false);
        this.getDelete().getStyle().set("visibility","hidden");

        setupGrid(this.getGrid());
        setI18n(setupI18n());
        setupEventListeners();

        addEditColumn(this.getGrid());
        setSizeFull();
    }

    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setEditItem("Edit " + UserEntityConstants.EMPLOYEE_NAME);
        crudI18n.setEditLabel("Edit " + UserEntityConstants.EMPLOYEE_NAME);
        crudI18n.getConfirm().getCancel().setContent(DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, UserEntityConstants.EMPLOYEE_NAME));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var userRepresentation = e.getItem();
            if (userRepresentation.getId() != null) {
                if (!organizationEmployeeCrudPresenter.update(organization,userRepresentation))
                    this.cancelSave();
            } else {
                if (!organizationEmployeeCrudPresenter.save(organization, userRepresentation))
                    this.cancelSave();
            }
        });

        this.addEditListener(e -> {
            navigateToEntity(e.getItem().getId());
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToEntity(null));

    }

    protected void setupGrid(Grid<UserRepresentation> grid)
    {
        grid.addColumn(UserRepresentation::getId).setHeader(UserEntityConstants.ID_LABEL);
        grid.addColumn(UserRepresentation::getUsername).setHeader(UserEntityConstants.USERNAME_LABEL);
        grid.addColumn(UserRepresentation::getFirstName).setHeader(UserEntityConstants.FIRST_NAME_LABEL);
        grid.addColumn(UserRepresentation::getLastName).setHeader(UserEntityConstants.LAST_NAME_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.GENDER)).setHeader(UserEntityConstants.GENDER_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.PHONE)).setHeader(UserEntityConstants.PHONE_LABEL);
        grid.addColumn(UserRepresentation::getEmail).setHeader(UserEntityConstants.EMAIL_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.BIRTHDATE)).setHeader(UserEntityConstants.BIRTHDATE_LABEL);

        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.STREET_ADDRESS)).setHeader(UserEntityConstants.STREET_ADDRESS_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.POSTAL_CODE)).setHeader(UserEntityConstants.POSTAL_CODE_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.LOCALITY)).setHeader(UserEntityConstants.LOCALITY_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.REGION)).setHeader(UserEntityConstants.REGION_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.COUNTRY)).setHeader(UserEntityConstants.COUNTRY_LABEL);

        grid.addColumn(user -> {
            //FIXME Zone is not flexible but we don't care. It's an university project. But maybe this is fixed in the future
            LocalDateTime creationDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getCreatedTimestamp()), ZoneId.of("Europe/Athens"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FormattingConstants.DATE_TIME_FORMAT);
            return creationDate.format(formatter);
        }).setHeader(UserEntityConstants.CREATED_ON_LABEL);

        grid.addComponentColumn(user -> {
            BadgeColor color;
            String status;
            if (user.isEnabled()) {
                color = BadgeColor.SUCCESS_PRIMARY;
                status = "Enabled";
            } else {
                color = BadgeColor.ERROR_PRIMARY;
                status = "Disabled";
            }
            var badge = new StatusBadge(status, color, BadgeSize.M, BadgeShape.PILL);
            badge.getElement().setProperty("title", status);
            return badge;
        }).setHeader(UserEntityConstants.ACCOUNT_STATUS_LABEL);

        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

    }

    protected void navigateToEntity(String empId) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(RouteConstants.PAGE_ORG_SUPERVISORS, organization.getName(), empId)));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String path) {
        var paths = beforeEvent.getLocation().getSegments();

        var orgName = keycloakTokenHelper.getGroup().getParent().getName();
        this.organization = orgPresenter
                .findById(orgName)
                .orElseThrow(NotFoundException::new);

        this.title = organization.getName() +"'s " + UserEntityConstants.EMPLOYEE_NAME + "s";

        if(paths.size() > 3)
            throw new NotFoundException(); //Prevent possible exploits, we expect 2 paths at most

        if(paths.size() == 3) {
            RouteConfiguration.forSessionScope().setRoute(orgName,OrganizationManageEmployeesView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }
}
