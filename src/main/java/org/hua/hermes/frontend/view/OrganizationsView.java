package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;

import de.codecamp.vaadin.security.spring.access.SecuredAccess;

import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORGS_ADMIN;

import org.hua.hermes.frontend.component.TrimmedTextField;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.MessageConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.OrganizationEntityConstants;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.NavigationUtil;

import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Route(value = PAGE_ORGS_ADMIN, layout = MainLayout.class)
@PageTitle(RouteConstants.TITLE_ORGS_ADMIN)
@SecuredAccess(SecurityConstants.HAS_ORGS_ADMIN_ROLE)
public class OrganizationsView
        extends AbstractCrudView<GroupRepresentation>
        implements HasNotifications, HasUrlParameter<String> {

    private final OrganizationCrudPresenter organizationsPresenter;

    public OrganizationsView(@Autowired OrganizationRepository organizationRepository)
    {
        super(GroupRepresentation.class, OrganizationEntityConstants.ENTITY_NAME, createOrganizationEditor());

        //Initialize CRUD Presenter
        this.organizationsPresenter = new OrganizationCrudPresenter(organizationRepository,this);
    }

    @Override
    public void setupDataProvider()
    {
        //Initialize data provider(pagination)
        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> organizationsPresenter.findAll(fetch.getOffset(),fetch.getLimit()).stream(),
                count -> organizationsPresenter.count()));
    }

    @Override
    public void setupGrid()
    {
        getGrid().addColumn(GroupRepresentation::getId).setHeader(OrganizationEntityConstants.ID);
        getGrid().addColumn(GroupRepresentation::getName).setHeader(OrganizationEntityConstants.NAME);
        getGrid().getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

        GridContextMenu<GroupRepresentation> contextMenu = new GridContextMenu<>(getGrid());
        //Do not show the context menu when a row is not clicked
        contextMenu.setDynamicContentHandler(Objects::nonNull);

        contextMenu.addItem("Manage Supervisors",listener ->
                listener.getItem().ifPresent(action ->
                        getUI().ifPresent(ui ->
                                ui.navigate(RouteConstants.PAGE_ORG_SUPERVISORS + "/" + action.getName()))))
                .setCheckable(false);

        addEditColumn(getGrid());
    }

    @Override
    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var organization = e.getItem();
            if (organization.getId() != null) {
                if (!organizationsPresenter.update(organization)) {
                    //If save fails then don't close the editor
                    //Presenter return false on fail
                    this.cancelSave();
                }
            } else {
                if (!organizationsPresenter.save(organization)){
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

    private static CrudEditor<GroupRepresentation> createOrganizationEditor(){

        TrimmedTextField name = new TrimmedTextField(OrganizationEntityConstants.NAME);
        FormLayout layout = new FormLayout(name);

        var binder = new Binder<>(GroupRepresentation.class);

        binder.forField(name)
                .asRequired(MessageConstants.REQUIRED)
                .bind(GroupRepresentation::getName, GroupRepresentation::setName);

        return new BinderCrudEditor<>(binder, layout);
    }

    private void navigateToOrganization(String id) {
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(PAGE_ORGS_ADMIN,id)));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String orgName)
    {
        if (orgName != null) {
            var organization = getEditor().getItem();
            if (organization != null && orgName.equals(organization.getName())) {
                return;
            }
            try {
                organization = organizationsPresenter
                        .findById(orgName)
                        .orElseThrow(NotFoundException::new);
                edit(organization, EditMode.EXISTING_ITEM);
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }

        } else {
            setOpened(false);
        }
    }
}