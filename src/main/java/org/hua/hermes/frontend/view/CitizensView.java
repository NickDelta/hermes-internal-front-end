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
import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.CitizenRepository;
import org.hua.hermes.frontend.util.DateTimeUtils;
import org.hua.hermes.frontend.util.NavigationUtil;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.presenter.CitizenCrudPresenter;
import org.hua.hermes.keycloak.KeycloakTokenHelper;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.FormatStyle;
import static org.hua.hermes.frontend.constant.MessageConstants.DELETE_MESSAGE;
import static org.hua.hermes.frontend.constant.MessageConstants.DISCARD_MESSAGE;
import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORGS_CITIZENS;

@Route(value = PAGE_ORGS_CITIZENS, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORGS_ADMIN_ROLE)
public class CitizensView extends Crud<UserRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle {

    private final KeycloakTokenHelper keycloakTokenHelper;
    private UserRepresentation user;
    private final CitizenCrudPresenter crudPresenter;

    private final CrudI18n i18n;

    private String title = "";


    public CitizensView(@Autowired CitizenRepository repository,
                        @Autowired CrudEditor<UserRepresentation> crudEditor,
                        @Autowired KeycloakTokenHelper keycloakTokenHelper) {

        super(UserRepresentation.class, new Grid<>(),crudEditor);
        this.keycloakTokenHelper = keycloakTokenHelper;

        this.crudPresenter = new CitizenCrudPresenter(repository,this);

        this.i18n = setupI18n();

        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> crudPresenter.findAll(fetch.getOffset(),fetch.getLimit()).stream(),
                count -> crudPresenter.count()));

        this.setEditorPosition(CrudEditorPosition.ASIDE);

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
        crudI18n.setNewItem("New " + UserEntityConstants.CITIZEN_NAME);
        crudI18n.setEditItem("Edit " + UserEntityConstants.CITIZEN_NAME);
        crudI18n.setEditLabel("Edit");
        crudI18n.getConfirm().getCancel().setContent(DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, UserEntityConstants.CITIZEN_NAME));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
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

    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var user = e.getItem();
            if (user.getId() != null) {
                if (!crudPresenter.update(user))
                    this.cancelSave();
            } else {
                if (!crudPresenter.save(user))
                    this.cancelSave();
            }
        });

        this.addEditListener(e -> {
            navigateToUser(e.getItem().getId());
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToUser(null));

    }

    protected void navigateToUser(String citizenId) {
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(PAGE_ORGS_CITIZENS, user.getId(), citizenId)));
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,@OptionalParameter String id) {
        if (id != null) {
            var user = getEditor().getItem();
            if (user != null && id.equals(user.getId())) {
                return;
            }
            try{
                user = crudPresenter
                        .findById(id)
                        .orElseThrow(NotFoundException::new);
                edit(user, EditMode.EXISTING_ITEM);
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }
        } else {
            setOpened(false);
        }
    }
}
