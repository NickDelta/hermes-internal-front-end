package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.BinderCrudEditor;
import com.vaadin.componentfactory.enhancedcrud.Crud;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.backend.entity.ApplicationState;
import org.hua.hermes.frontend.repository.OrganizationEmployeesRepository;
import org.hua.hermes.frontend.view.presenter.OrganizationEmployeesCrudPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class OrganizationEmployeeView
        extends Crud<Application> {

    private final OrganizationEmployeesCrudPresenter presenter;

    public OrganizationEmployeeView(@Autowired OrganizationEmployeesRepository organizationEmployeesRepository){

        super(Application.class, new Grid<>(),createEmployeesEditor());

        //Initialize CRUD Presenter
        this.presenter = new OrganizationEmployeesCrudPresenter(organizationEmployeesRepository);
        presenter.setView((HasNotifications) this);

    }

    private static CrudEditor<Application> createEmployeesEditor() {

        Select<ApplicationState> labelSelect = new Select<>();
        labelSelect.setLabel("Application state");
        List<ApplicationState> applicationStates = Arrays.asList(ApplicationState.values());

        // Choose which property from Application State is the presentation value
        labelSelect.setItemLabelGenerator(ApplicationState::getName);
        labelSelect.setItems(applicationStates);

        //labelSelect.add();

        FormLayout layout = new FormLayout(labelSelect);
        var binder = new Binder<>(Application.class);

        binder.forField(labelSelect)
                .bind(Application::getState,Application::setState);

        return new BinderCrudEditor<>(binder, layout);
    }


}
