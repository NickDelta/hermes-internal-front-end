package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;

import de.codecamp.vaadin.security.spring.access.SecuredAccess;

import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORGS_ADMIN;

import org.hua.hermes.frontend.constant.CrudConstants;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.ValidationConstants;
import org.hua.hermes.frontend.constant.entity.OrganizationEntityConstants;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.TemplateUtil;

import org.hua.hermes.frontend.view.presenter.OrganizationsCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Route(value = PAGE_ORGS_ADMIN, layout = MainLayout.class)
@PageTitle(RouteConstants.TITLE_ORGS_ADMIN)
@SecuredAccess(RouteConstants.SECURITY_ORGS_ADMIN)
public class OrganizationsView
        extends Crud<GroupRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasStyle
{

    private final OrganizationsCrudPresenter presenter;

    public OrganizationsView(@Autowired OrganizationRepository organizationRepository)
    {
        super(GroupRepresentation.class, new Grid<>(),createOrganizationEditor());

        //Initialize CRUD Presenter
        this.presenter = new OrganizationsCrudPresenter(organizationRepository);
        presenter.setView(this);

        //Allows selecting up to 1 items from grid
        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        //Initialize data provider(pagination)
        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> presenter.findAll(fetch.getOffset(),fetch.getLimit()).stream(),
                count -> presenter.count()));

        //Puts editor aside
        this.setEditorPosition(CrudEditorPosition.ASIDE);

        //Functional requirements don't have a user delete option so far
        this.getDelete().setEnabled(false);
        this.getDelete().getStyle().set("visibility","hidden");

        //Setup the CRUD's Grid
        setupGrid(getGrid());

        //Setup internationalization (Only English for us)
        setI18n(setupI18n());

        //Setup events
        setupEventListeners();

        //Set CRUD to FullSize
        setSizeFull();

    }

    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setNewItem("New " + OrganizationEntityConstants.ENTITY_NAME);
        crudI18n.setEditItem("Edit " + OrganizationEntityConstants.ENTITY_NAME);
        crudI18n.setEditLabel("Edit " + OrganizationEntityConstants.ENTITY_NAME);
        crudI18n.getConfirm().getCancel().setContent(CrudConstants.DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(CrudConstants.DELETE_MESSAGE, OrganizationEntityConstants.ENTITY_NAME));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var organization = e.getItem();
            if (organization.getId() != null) {
                if (!presenter.update(organization)) {
                    //If save fails then don't close the editor
                    //Presenter return false on fail
                    this.cancelSave();
                }
            } else {
                if (!presenter.save(organization)){
                    //If update fails then don't close the editor
                    //Presenter return false on fail
                    this.cancelSave();
                }
            }
        });

        this.addEditListener(e -> {
            navigateToOrganization(e.getItem().getName());
            //Set the organization to the editor so that the binder can access it
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToOrganization(null));

    }

    protected void setupGrid(Grid<GroupRepresentation> grid)
    {
        grid.addColumn(GroupRepresentation::getId).setHeader(OrganizationEntityConstants.ID);
        grid.addColumn(GroupRepresentation::getName).setHeader(OrganizationEntityConstants.NAME);
        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

        GridContextMenu<GroupRepresentation> contextMenu = new GridContextMenu<>(grid);
        //Do not show the context menu when a row is not clicked
        contextMenu.setDynamicContentHandler(Objects::nonNull);

        contextMenu.addItem("Manage Supervisors",listener ->
                listener.getItem().ifPresent(action ->
                    getUI().ifPresent(ui ->
                        ui.navigate(RouteConstants.PAGE_ORG_SUPERVISORS + "/" + action.getName()))));

        addEditColumn(grid);
    }


    private static CrudEditor<GroupRepresentation> createOrganizationEditor(){

        TextField name = new TextField(OrganizationEntityConstants.NAME);
        FormLayout layout = new FormLayout(name);

        var binder = new Binder<>(GroupRepresentation.class);

        binder.forField(name)
                .asRequired(ValidationConstants.REQUIRED_TEXT)
                .bind(GroupRepresentation::getName,GroupRepresentation::setName);

        return new BinderCrudEditor<>(binder, layout);
    }

    protected void navigateToOrganization(String orgName) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(PAGE_ORGS_ADMIN,orgName)));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String orgName)
    {
        if (orgName != null) {
            var organization = getEditor().getItem();
            if (organization != null && orgName.equals(organization.getName())) {
                return;
            }
            presenter.findById(orgName).ifPresent(userRepresentation ->
                    edit(userRepresentation, EditMode.EXISTING_ITEM));
        } else {
            setOpened(false);
        }
    }
}