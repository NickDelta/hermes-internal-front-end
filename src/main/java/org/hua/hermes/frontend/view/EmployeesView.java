package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import de.codecamp.vaadin.security.spring.access.route.RouteAccessDeniedException;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.EmployeeRepository;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.NavigationUtil;
import org.hua.hermes.frontend.view.presenter.OrganizationUserCrudPresenter;
import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.hua.hermes.keycloak.KeycloakTokenHelper;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = RouteConstants.PAGE_ORG_EMPLOYEES, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORG_SUPERVISOR_ROLE)
public class EmployeesView
        extends AbstractUsersView
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle {

    private GroupRepresentation organization;
    private final OrganizationCrudPresenter orgPresenter;
    private final OrganizationUserCrudPresenter userPresenter;
    private final KeycloakTokenHelper keycloakTokenHelper;

    private String title = "";

    public EmployeesView(@Autowired OrganizationRepository orgRepository,
                         @Autowired EmployeeRepository organizationEmployeesRepository,
                         @Autowired CrudEditor<UserRepresentation> userCrudEditor,
                         @Autowired KeycloakTokenHelper keycloakTokenHelper) {
        super(UserEntityConstants.EMPLOYEE_NAME,userCrudEditor);

        this.keycloakTokenHelper = keycloakTokenHelper;
        this.orgPresenter = new OrganizationCrudPresenter(orgRepository,this);
        this.userPresenter = new OrganizationUserCrudPresenter(organizationEmployeesRepository,this);

    }

    @Override
    public void setupDataProvider() {
        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> userPresenter.findAll(organization,fetch.getOffset(),fetch.getLimit()).stream(),
                count -> userPresenter.count(organization)));
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
            navigateToUser(e.getItem().getId());
            this.getEditor().setItem(e.getItem());
        });

        this.addCancelListener(e -> navigateToUser(null));

    }

    protected void navigateToUser(String id) {
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(RouteConstants.PAGE_ORG_EMPLOYEES, organization.getName(), id)));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String path) {

        //Expected contents are ["organizations","employees",{organization},{userId}]
        //Items in {} are optional
        var paths = beforeEvent.getLocation().getSegments();

        if(paths.size() > 4)
            throw new NotFoundException(); //Prevent possible exploits, we expect 2 paths at most

        //Get name of the organization where the supervisor works
        var orgName = keycloakTokenHelper.getOrganization().getName();

        if(paths.size() >= 3){
            if(!paths.get(2).equals(orgName))
                throw new RouteAccessDeniedException("User does not belong to the specified organization");
        }

        //Fetch the GroupRepresentation of the organization
        try{
            this.organization = orgPresenter
                    .findById(orgName)
                    .orElseThrow(NotFoundException::new);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }

        this.title = organization.getName() +"'s " + UserEntityConstants.EMPLOYEE_NAME + "s";

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

    @Override
    public String getPageTitle() {
        return title;
    }
}
