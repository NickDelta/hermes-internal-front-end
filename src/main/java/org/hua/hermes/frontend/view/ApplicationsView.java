package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.backend.entity.ApplicationState;
import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.repository.ApplicationRepository;
import org.hua.hermes.frontend.util.DateTimeUtils;
import org.hua.hermes.frontend.util.NavigationUtil;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.presenter.ApplicationCrudPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORG_APPLICATIONS;

@Route(value = RouteConstants.PAGE_ORG_APPLICATIONS, layout = MainLayout.class)
@PageTitle(RouteConstants.TITLE_APPLICATIONS)
@SecuredAccess(SecurityConstants.HAS_ORG_EMPLOYEE_ROLE)
public class ApplicationsView
        extends AbstractCrudView<Application>
        implements HasNotifications, HasUrlParameter<String> {

    private final ApplicationCrudPresenter presenter;

    public ApplicationsView(@Autowired ApplicationRepository repository){

        super(Application.class, Application.ENTITY_NAME, createEmployeesEditor());

        this.presenter = new ApplicationCrudPresenter(repository,this);

        //Overlay CRUD Editor is more beautiful here
        this.setEditorPosition(CrudEditorPosition.OVERLAY);

        //Employees cannot create new applications
        //Remove New button by setting an empty toolbar
        this.setToolbar();

    }

    @Override
    public void setupDataProvider()
    {
        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> presenter.findAll(fetch.getOffset(),fetch.getLimit()).stream(),
                count -> presenter.count()));
    }

    @Override
    public void setupEventListeners() {

        //Handling only update
        this.addSaveListener(e -> {
            var application = e.getItem();
            if (!presenter.update(application)) {
                this.cancelSave();
            }
        });

        this.addEditListener(e -> {
            navigateToApplication(e.getItem().getId());
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToApplication(null));
    }

    private void navigateToApplication(String id) {
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(PAGE_ORG_APPLICATIONS, id)));
    }

    @Override
    public void setupGrid()
    {
        getGrid().addColumn(Application::getId).setHeader(Application.ID_LABEL);
        getGrid().addColumn(Application::getCreatedBy).setHeader(Application.CREATED_BY_LABEL);
        getGrid().addColumn((application) -> DateTimeUtils.formatDateTime(application.getCreatedDate())).setHeader(Application.CREATED_DATE_LABEL);
        getGrid().addColumn(Application::getLastModifiedBy).setHeader(Application.LAST_MODIFIED_BY_LABEL);
        getGrid().addColumn((application) -> DateTimeUtils.formatDateTime(application.getLastModifiedDate())).setHeader(Application.LAST_MODIFIED_ON_LABEL);

        getGrid().addComponentColumn(application -> {
            BadgeColor color;
            String status;
            switch (application.getState()){
                case APPROVED:
                case SUBMITTED:
                case COMPLETED:
                    color = BadgeColor.SUCCESS_PRIMARY;
                    break;
                case CANCELED:
                case REJECTED:
                    color = BadgeColor.ERROR_PRIMARY;
                    break;
                case RESUBMISSION_REQUIRED:
                    color = BadgeColor.CONTRAST_PRIMARY;
                    break;
                default:
                    throw new IllegalStateException("Illegal application state");
            }
            status = application.getState().getName();
            var badge = new StatusBadge(status, color, BadgeSize.M, BadgeShape.PILL);
            badge.getElement().setProperty("title", status);
            return badge;
        }).setHeader(Application.STATE_LABEL);

        getGrid().addColumn((application) -> DateTimeUtils.formatDateTime(application.getAppointmentDate())).setHeader(Application.APPOINTMENT_DATE_LABEL);

        getGrid().setItemDetailsRenderer(new ComponentRenderer<>(item -> {
            var label = new Label("Details:");
            var text = new Label(item.getDetails());
            text.getStyle().set("font-weight", "bold");
            return new HorizontalLayout(label,text);
        }));

        getGrid().getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

        addEditColumn(getGrid());
    }

    private static CrudEditor<Application> createEmployeesEditor() {

        Select<ApplicationState> select = new Select<>();
        select.setLabel(Application.STATE_LABEL);
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
    public void setParameter(BeforeEvent event, @OptionalParameter String id)
    {
        //Our back-end will throw a 403 if someone tries to access an application of an another organization
        //So this scenario is already covered.
        //It is different than keycloak's cases where we needed to manually validate in which organization the user works.

        if (id != null) {
            var application = getEditor().getItem();
            if (application != null && id.equals(application.getId())) {
                return;
            }
            try{
                application = presenter
                        .findById(id)
                        .orElseThrow(NotFoundException::new);
                edit(application, EditMode.EXISTING_ITEM);
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }
        } else {
            setOpened(false);
        }
    }
}
