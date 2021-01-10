package org.hua.hermes.frontend.view.administrators.organizations;

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
import org.hua.hermes.frontend.data.GenericValidationMessages;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.TemplateUtil;
import org.hua.hermes.frontend.view.HasNotifications;
import org.hua.hermes.frontend.view.MainLayout;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Route(value = "organizations", layout = MainLayout.class)
@PageTitle("Organizations")
@SecuredAccess("hasRole('ROLE_ORGS_ADMIN')")
public class OrganizationsView
        extends Crud<GroupRepresentation>
        implements HasNotifications, HasUrlParameter<String>, HasStyle
{

    private final String entityName = "Organization";
    private static final String DISCARD_MESSAGE = "There are unsaved modifications. Discard changes?";
    private static final String DELETE_MESSAGE = "Are you sure you want to delete the selected %s? This action cannot be undone.";

    private final OrganizationCrudPresenter presenter;

    public OrganizationsView(@Autowired OrganizationRepository organizationRepository)
    {
        super(GroupRepresentation.class, new Grid<>(),createOrganizationEditor());

        //Initialize CRUD Presenter
        this.presenter = new OrganizationCrudPresenter(organizationRepository);
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

        setupGrid(getGrid());
        setI18n(setupI18n());
        setupEventListeners();

        addEditColumn(getGrid());
        setSizeFull();

    }

    //Setup internationalization (Only English for us)
    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setNewItem("New " + entityName);
        crudI18n.setEditItem("Edit " + entityName);
        crudI18n.setEditLabel("Edit " + entityName);
        crudI18n.getConfirm().getCancel().setContent(DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var organization = e.getItem();
            if (organization.getId() != null) {
                if (!presenter.update(organization))
                    this.cancelSave();
            } else {
                if (!presenter.save(organization))
                    this.cancelSave();
            }
        });

        this.addEditListener(e -> {
            navigateToEntity(e.getItem().getName());
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToEntity(null));

    }

    protected void navigateToEntity(String name) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation("organizations",name)));
    }

    protected void setupGrid(Grid<GroupRepresentation> grid)
    {
        grid.addColumn(GroupRepresentation::getId).setHeader("Id");
        grid.addColumn(GroupRepresentation::getName).setHeader("Name");
        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

        GridContextMenu<GroupRepresentation> contextMenu = new GridContextMenu<>(grid);
        //Do not show the context menu when a row is not clicked
        contextMenu.setDynamicContentHandler(Objects::nonNull);

        contextMenu.addItem("Manage Supervisors",listener ->
                listener.getItem().ifPresent(action ->
                    getUI().ifPresent(ui ->
                        ui.navigate("organizations/supervisors/" + action.getName()))));
    }


    private static CrudEditor<GroupRepresentation> createOrganizationEditor(){

        TextField name = new TextField("Name");
        FormLayout layout = new FormLayout(name);

        var binder = new Binder<>(GroupRepresentation.class);

        binder.forField(name)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(GroupRepresentation::getName,GroupRepresentation::setName);

        return new BinderCrudEditor<>(binder, layout);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String name)
    {
        if (name != null) {
            var item = getEditor().getItem();
            if (item != null && name.equals(item.getName())) {
                return;
            }
            var fetchedItem = presenter.findById(name);
            fetchedItem.ifPresent(userRepresentation -> edit(userRepresentation, EditMode.EXISTING_ITEM));
        } else {
            setOpened(false);
        }
    }
}