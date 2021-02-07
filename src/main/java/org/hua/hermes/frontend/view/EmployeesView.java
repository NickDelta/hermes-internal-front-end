package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.Crud;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.componentfactory.enhancedcrud.CrudEditorPosition;
import com.vaadin.componentfactory.enhancedcrud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import de.codecamp.vaadin.security.spring.access.route.RouteAccessDeniedException;
import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.EmployeeRepository;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.DateTimeUtils;
import org.hua.hermes.frontend.util.NavigationUtil;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.presenter.OrganizationUserCrudPresenter;
import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.hua.hermes.keycloak.KeycloakTokenHelper;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.FormatStyle;

import static org.hua.hermes.frontend.constant.MessageConstants.DELETE_MESSAGE;
import static org.hua.hermes.frontend.constant.MessageConstants.DISCARD_MESSAGE;

@Route(value = RouteConstants.PAGE_ORG_EMPLOYEES, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORG_SUPERVISOR_ROLE)
public class EmployeesView extends Crud<UserRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle {

    private final KeycloakTokenHelper keycloakTokenHelper;

    private GroupRepresentation organization;
    private final OrganizationCrudPresenter orgPresenter;
    private final OrganizationUserCrudPresenter userPresenter;

    private final CrudI18n i18n;

    private String title = "";


    public EmployeesView(@Autowired OrganizationRepository orgRepository,
                         @Autowired EmployeeRepository organizationEmployeesRepository,
                         @Autowired CrudEditor<UserRepresentation> userCrudEditor,
                         @Autowired KeycloakTokenHelper keycloakTokenHelper) {
        super(UserRepresentation.class, new Grid<>(),userCrudEditor);
        this.keycloakTokenHelper = keycloakTokenHelper;

        this.orgPresenter = new OrganizationCrudPresenter(orgRepository,this);
        this.userPresenter = new OrganizationUserCrudPresenter(organizationEmployeesRepository,this);

        this.i18n = setupI18n();

        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> userPresenter.findAll(organization,fetch.getOffset(),fetch.getLimit()).stream(),
                count -> userPresenter.count(organization)));

        this.setEditorPosition(CrudEditorPosition.ASIDE);

        //Functional requirements don't have a user delete option so far
        this.getDelete().setEnabled(false);
        this.getDelete().getStyle().set("visibility","hidden");


        setupGrid(this.getGrid());
        setI18n(i18n);
        setupEventListeners();

        setSizeFull();
    }

    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setNewItem("New " + UserEntityConstants.EMPLOYEE_NAME);
        crudI18n.setEditItem("Edit " + UserEntityConstants.EMPLOYEE_NAME);
        crudI18n.setEditLabel("Edit");
        crudI18n.getConfirm().getCancel().setContent(DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, UserEntityConstants.EMPLOYEE_NAME));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var user = e.getItem();
            if (user.getId() != null) {
                if (!userPresenter.update(organization,user))
                    this.cancelSave();
            } else {
                if (!userPresenter.save(organization, user))
                    this.cancelSave();
            }
        });

        this.addEditListener(e -> {
            navigateToUser(e.getItem().getId());
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToUser(null));

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
            LocalDateTime creationDate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(user.getCreatedTimestamp()),
                    VaadinSession.getCurrent().getAttribute(ZoneId.class)
            );
            return DateTimeUtils.formatDate(FormatStyle.SHORT,creationDate);
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

        addEditColumn(grid,i18n);

        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

    }

    protected void navigateToUser(String empId) {
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(RouteConstants.PAGE_ORG_EMPLOYEES, organization.getName(), empId)));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String path) {

        //Expected contents are ["organizations","employees",{organization},{userId}]
        //Items in {} are optional
        var paths = beforeEvent.getLocation().getSegments();

        if(paths.size() > 4)
            throw new NotFoundException(); //Prevent possible exploits, we expect 2 paths at most

        //Get name of the organization where the supervisor works
        var orgName = keycloakTokenHelper.getOrganization().getName();

        if(paths.size() >= 3){
            if(!paths.get(2).equals(orgName))
                throw new RouteAccessDeniedException("User does not belong to the specified organization");
        }

        //Fetch the GroupRepresentation of the organization
        try{
            this.organization = orgPresenter
                    .findById(orgName)
                    .orElseThrow(NotFoundException::new);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }

        this.title = organization.getName() +"'s " + UserEntityConstants.EMPLOYEE_NAME + "s";

        if(paths.size() > 3){
            var userId = paths.get(3);
            var item = getEditor().getItem();
            if (item != null && userId.equals(item.getId())) {
                return;
            }

            try{
                var user = userPresenter
                        .findById(organization,userId)
                        .orElseThrow(NotFoundException::new);
                edit(user, EditMode.EXISTING_ITEM);
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }

        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }
}
