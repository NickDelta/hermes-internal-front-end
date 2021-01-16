package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.backend.entity.ApplicationState;
import org.hua.hermes.frontend.constant.CrudConstants;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.OrganizationEntityConstants;
import org.hua.hermes.frontend.repository.impl.OrganizationApplicationRepositoryImpl;
import org.hua.hermes.frontend.util.TemplateUtil;
import org.hua.hermes.frontend.view.presenter.OrganizationApplicationsCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORGS_ADMIN;
import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORG_APPLICATIONS;

@Route(value = RouteConstants.PAGE_ORG_APPLICATIONS, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORG_EMPLOYEE_ROLE)
public class OrganizationApplicationsView
        extends Crud<Application>
        implements HasNotifications, HasUrlParameter<String>, HasStyle
{

    private final OrganizationApplicationsCrudPresenter presenter;


    public OrganizationApplicationsView(@Autowired OrganizationApplicationRepositoryImpl repository){

        super(Application.class, new Grid<>(),createEmployeesEditor());

        presenter = new OrganizationApplicationsCrudPresenter(repository);
        presenter.setView(this);

        this.getGrid().addColumn(Application::getId).setHeader("Id");

        //Remove New Button by setting an empty toolbar
        this.setToolbar();

        setSizeFull();

    }

    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setNewItem("New " + OrganizationEntityConstants.ENTITY_NAME);
        crudI18n.setEditItem("Edit " + OrganizationEntityConstants.ENTITY_NAME);
        crudI18n.setEditLabel("Edit" + OrganizationEntityConstants.ENTITY_NAME);
        crudI18n.getConfirm().getCancel().setContent(CrudConstants.DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(CrudConstants.DELETE_MESSAGE, OrganizationEntityConstants.ENTITY_NAME));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners() {
        this.addSaveListener(e -> {
            var application = e.getItem();
            if (application.getId() != null) {
                if (!presenter.update(application)) {
                    //If save fails then don't close the editor
                    //Presenter return false on fail
                    this.cancelSave();
                }
            }
        });

        this.addEditListener(e -> {
            navigateToApplication(e.getItem().getId());
            //Set the organization to the editor so that the binder can access it
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToApplication(null));
    }

    private void navigateToApplication(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(PAGE_ORG_APPLICATIONS,id)));
    }

    protected void setupGrid(Grid<Application> grid)
    {
        grid.addColumn(Application::getId).setHeader("Id");
        grid.addColumn(Application::getCreatedDate).setHeader("Created date");
        grid.addColumn(Application::getLastModifiedDate).setHeader("Last modified date");
        grid.addColumn(Application::getCreatedBy).setHeader("Created by");
        grid.addColumn(Application::getLastModifiedBy).setHeader("Last modified by");
        grid.addColumn(Application::getOrganization).setHeader("Organization");
        grid.addColumn(Application::getState).setHeader("State");
        grid.addColumn(Application::getAppointmentDate).setHeader("Appointment date");
        grid.addColumn(Application::getDetails).setHeader("Details");
        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

        addEditColumn(grid);
    }

    private static CrudEditor<Application> createEmployeesEditor() {

        Select<ApplicationState> select = new Select<>();
        select.setLabel("State");
        List<ApplicationState> applicationStates = Arrays.asList(ApplicationState.values());

        // Choose which property from Application State is the presentation value
        select.setItemLabelGenerator(ApplicationState::getName);
        select.setItems(applicationStates);

        FormLayout layout = new FormLayout(select);
        var binder = new Binder<>(Application.class);

        binder.forField(select).bind(Application::getState,Application::setState);

        return new BinderCrudEditor<>(binder, layout);
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String applicationId)
    {
        if (applicationId != null) {
            var application = getEditor().getItem();
            if (application != null && applicationId.equals(application.getId())) {
                return;
            }
            application = presenter.findById(applicationId).orElseThrow(NotFoundException::new);
            edit(application, EditMode.EXISTING_ITEM);
        } else {
            setOpened(false);
        }
    }
}
