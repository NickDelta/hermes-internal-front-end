package org.hua.hermes.frontend.view.administrators.supervisors;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;

import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.data.entity.constants.UserEntityConstants;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.repository.OrganizationSupervisorsRepository;
import org.hua.hermes.frontend.util.TemplateUtil;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.HasNotifications;
import org.hua.hermes.frontend.view.MainLayout;
import org.hua.hermes.frontend.view.administrators.organizations.OrganizationCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "organizations/supervisors", layout = MainLayout.class)
@SecuredAccess("hasRole('ROLE_ORGS_ADMIN')")
public class OrganizationSupervisorsView
        extends Crud<UserRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle
{

    private final String entityName = "Supervisor";

    private static final String DISCARD_MESSAGE = "There are unsaved modifications to the %s. Discard changes?";
    private static final String DELETE_MESSAGE = "Are you sure you want to delete the selected %s? This action cannot be undone.";

    private final OrganizationCrudPresenter orgPresenter;
    private final OrganizationSupervisorsCrudPresenter supervisorsPresenter;
    private GroupRepresentation organization;
    private String title = "";

    public OrganizationSupervisorsView(@Autowired OrganizationRepository orgRepository,
                                       @Autowired OrganizationSupervisorsRepository supervisorsRepository,
                                       @Autowired CrudEditor<UserRepresentation> userCrudEditor)
    {
        super(UserRepresentation.class, new Grid<>(),userCrudEditor);

        this.orgPresenter = new OrganizationCrudPresenter(orgRepository);
        this.supervisorsPresenter = new OrganizationSupervisorsCrudPresenter(supervisorsRepository);

        orgPresenter.setView(this);
        supervisorsPresenter.setView(this);

        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> supervisorsPresenter.findAll(organization,fetch.getOffset(),fetch.getLimit()).stream(),
                count -> supervisorsPresenter.count(organization)));

        this.setEditorPosition(CrudEditorPosition.ASIDE);

        Button backButton = UIUtils.createButton("Back", VaadinIcon.ARROW_LEFT, ButtonVariant.LUMO_PRIMARY);
        backButton.getStyle().set("position","absolute");
        backButton.getStyle().set("bottom","10px");
        backButton.getStyle().set("left","10px"); //I waited for the buttons not to be aligned but somehow they are with this style

        backButton.addClickListener(listener -> UI.getCurrent().navigate("organizations"));

        Button newItemButton = UIUtils.createButton("New Supervisor", ButtonVariant.LUMO_PRIMARY);
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
        crudI18n.setEditItem("Edit " + entityName);
        crudI18n.setEditLabel("Edit " + entityName);
        crudI18n.getConfirm().getCancel().setContent(String.format(DISCARD_MESSAGE, entityName));
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners()
    {

        this.addSaveListener(e -> {
            var user = e.getItem();
            if (user.getId() != null) {
                if (!supervisorsPresenter.update(organization,user))
                    this.cancelSave();
            } else {
                if (!supervisorsPresenter.save(organization, user))
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
        grid.addColumn(UserRepresentation::getId).setHeader(UserEntityConstants.USER_ID_LABEL);
        grid.addColumn(UserRepresentation::getUsername).setHeader(UserEntityConstants.USER_USERNAME_LABEL);
        grid.addColumn(UserRepresentation::getFirstName).setHeader(UserEntityConstants.USER_FIRST_NAME_LABEL);
        grid.addColumn(UserRepresentation::getLastName).setHeader(UserEntityConstants.USER_LAST_NAME_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_GENDER)).setHeader(UserEntityConstants.USER_GENDER_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_PHONE)).setHeader(UserEntityConstants.USER_PHONE_LABEL);
        grid.addColumn(UserRepresentation::getEmail).setHeader(UserEntityConstants.USER_EMAIL_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_BIRTHDATE)).setHeader(UserEntityConstants.USER_BIRTHDATE_LABEL);

        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_STREET_ADDRESS)).setHeader(UserEntityConstants.USER_STREET_ADDRESS_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_POSTAL_CODE)).setHeader(UserEntityConstants.USER_POSTAL_CODE_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_LOCALITY)).setHeader(UserEntityConstants.USER_LOCALITY_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_REGION)).setHeader(UserEntityConstants.USER_REGION_LABEL);
        grid.addColumn(user -> user.firstAttribute(UserEntityConstants.USER_COUNTRY)).setHeader(UserEntityConstants.USER_COUNTRY_LABEL);

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
        }).setHeader("Account Status");

        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

    }

    protected void navigateToEntity(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation("organizations/supervisors", organization.getName(), id)));
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String path)
    {
        //Expected contents are ["organizations","supervisors",{organization},{userId}]
        //Items in {} are optional
        var paths = event.getLocation().getSegments();

        if(paths.size() == 2 || paths.size() > 4 )
            throw new NotFoundException(); //Prevent possible exploits, we expect 2 paths at most

        var orgName = paths.get(2);
        this.organization = orgPresenter
                 .findById(orgName)
                 .orElseThrow(NotFoundException::new);

        this.title = organization.getName() +"'s Supervisors";

        if(paths.size() > 3){
            var userId = paths.get(3);
            var item = getEditor().getItem();
            if (item != null && userId.equals(item.getId())) {
                return;
            }
            var user = supervisorsPresenter
                    .findById(organization,userId)
                    .orElseThrow(NotFoundException::new);

            edit(user, EditMode.EXISTING_ITEM);
        }

    }

    @Override
    public String getPageTitle()
    {
        return title;
    }
}
