package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.BinderCrudEditor;
import com.vaadin.componentfactory.enhancedcrud.Crud;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.backend.entity.ApplicationState;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.repository.impl.OrganizationApplicationRepositoryImpl;
import org.hua.hermes.frontend.view.presenter.OrganizationApplicationsCrudPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

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

        //Remove New Button by setting an empty toolbar
        this.setToolbar();

        setSizeFull();

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
    public void setParameter(BeforeEvent event, @OptionalParameter String path)
    {
        //logic must be filled here
    }
}
