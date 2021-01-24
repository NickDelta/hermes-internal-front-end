package org.hua.hermes.frontend.view;

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
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.repository.SupervisorRepository;
import org.hua.hermes.frontend.util.FormattingConstants;
import org.hua.hermes.frontend.util.TemplateUtil;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.presenter.OrganizationUserCrudPresenter;
import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.hua.hermes.frontend.constant.CrudConstants.DELETE_MESSAGE;
import static org.hua.hermes.frontend.constant.CrudConstants.DISCARD_MESSAGE;

@Route(value = RouteConstants.PAGE_ORG_SUPERVISORS, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORGS_ADMIN_ROLE)
public class SupervisorsView
        extends Crud<UserRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle {

    private final OrganizationCrudPresenter orgPresenter;
    private final OrganizationUserCrudPresenter userPresenter;
    private GroupRepresentation organization;
    private String title = "";

    public SupervisorsView(@Autowired OrganizationRepository orgRepository,
                           @Autowired SupervisorRepository supervisorsRepository,
                           @Autowired CrudEditor<UserRepresentation> userCrudEditor)
    {
        super(UserRepresentation.class, new Grid<>(),userCrudEditor);

        this.orgPresenter = new OrganizationCrudPresenter(orgRepository,this);
        this.userPresenter = new OrganizationUserCrudPresenter(supervisorsRepository,this);

        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> userPresenter.findAll(organization,fetch.getOffset(),fetch.getLimit()).stream(),
                count -> userPresenter.count(organization)));

        this.setEditorPosition(CrudEditorPosition.ASIDE);


        //Create custom toolbar - Needed to put a nice back button to navigate to the Organizations view
        Button backButton = UIUtils.createButton("Back", VaadinIcon.ARROW_LEFT, ButtonVariant.LUMO_PRIMARY);
        backButton.getStyle().set("position","absolute");
        backButton.getStyle().set("bottom","10px");
        backButton.getStyle().set("left","10px"); //I waited for the buttons not to be aligned but somehow they are with this style

        backButton.addClickListener(listener -> UI.getCurrent().navigate(RouteConstants.PAGE_ORGS_ADMIN));

        Button newItemButton = UIUtils.createButton("New " + UserEntityConstants.SUPERVISOR_NAME, ButtonVariant.LUMO_PRIMARY);
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
        crudI18n.setEditItem("Edit " + UserEntityConstants.SUPERVISOR_NAME);
        crudI18n.setEditLabel("Edit " + UserEntityConstants.SUPERVISOR_NAME);
        crudI18n.getConfirm().getCancel().setContent(DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, UserEntityConstants.SUPERVISOR_NAME));
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

    protected void navigateToEntity(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(RouteConstants.PAGE_ORG_SUPERVISORS, organization.getName(), id)));
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String path)
    {
        //Expected contents are ["organizations","supervisors",{organization},{userId}]
        //Items in {} are optional
        var paths = event.getLocation().getSegments();

        if(paths.size() == 2 || paths.size() > 4 )
            throw new NotFoundException(); //Prevent possible exploits, we expect 2 paths at most

        //Get selected organization - this is required
        var orgName = paths.get(2);

        try{
            this.organization = orgPresenter
                    .findById(orgName)
                    .orElseThrow(NotFoundException::new);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }

        //Update the view's page title
        this.title = organization.getName() +"'s " + UserEntityConstants.SUPERVISOR_NAME + "s";

        //Open editor directly if the URL path contains a user id
        //that belongs to the specific organization
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

    //This view's title is dynamically determined
    @Override
    public String getPageTitle()
    {
        return title;
    }
}
