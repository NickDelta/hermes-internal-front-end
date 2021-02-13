package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.repository.SupervisorRepository;
import org.hua.hermes.frontend.util.NavigationUtil;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.view.presenter.OrganizationUserCrudPresenter;
import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = RouteConstants.PAGE_ORG_SUPERVISORS, layout = MainLayout.class)
@SecuredAccess(SecurityConstants.HAS_ORGS_ADMIN_ROLE)
public class SupervisorsView
        extends AbstractUsersView
        implements HasNotifications, HasUrlParameter<String>, HasDynamicTitle {

    private final OrganizationCrudPresenter orgPresenter;
    private final OrganizationUserCrudPresenter userPresenter;
    private GroupRepresentation organization;
    private String title = "";

    public SupervisorsView(@Autowired OrganizationRepository orgRepository,
                           @Autowired SupervisorRepository supervisorsRepository,
                           @Autowired CrudEditor<UserRepresentation> userCrudEditor)
    {
        super(UserEntityConstants.SUPERVISOR_NAME,userCrudEditor);

        this.orgPresenter = new OrganizationCrudPresenter(orgRepository,this);
        this.userPresenter = new OrganizationUserCrudPresenter(supervisorsRepository,this);

        //Create custom toolbar - Needed to put a nice back button to navigate to the Organizations view
        Button backButton = UIUtils.createButton("Back", VaadinIcon.ARROW_LEFT, ButtonVariant.LUMO_PRIMARY);
        backButton.getStyle().set("position","absolute");
        backButton.getStyle().set("bottom","10px");
        backButton.getStyle().set("left","10px"); //I waited for the buttons not to be aligned but somehow they are with this style

        backButton.addClickListener(listener -> UI.getCurrent().navigate(RouteConstants.PAGE_ORGS_ADMIN));

        Button newItemButton = UIUtils.createButton("New " + UserEntityConstants.SUPERVISOR_NAME, ButtonVariant.LUMO_PRIMARY);
        newItemButton.addClickListener(e -> this.edit(new UserRepresentation(), Crud.EditMode.NEW_ITEM));

        this.setToolbar(backButton, newItemButton);
    }

    @Override
    public void setupDataProvider() {
        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> userPresenter.findAll(organization,fetch.getOffset(),fetch.getLimit()).stream(),
                count -> userPresenter.count(organization)));
    }

    @Override
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

    private void navigateToUser(String id) {
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(RouteConstants.PAGE_ORG_SUPERVISORS, organization.getName(), id)));
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
