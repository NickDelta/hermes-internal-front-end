package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.repository.CitizenRepository;
import org.hua.hermes.frontend.util.NavigationUtil;
import org.hua.hermes.frontend.view.presenter.CitizenCrudPresenter;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hua.hermes.frontend.constant.RouteConstants.PAGE_ORGS_CITIZENS;
import static org.hua.hermes.frontend.constant.RouteConstants.TITLE_CITIZENS;

@Route(value = PAGE_ORGS_CITIZENS, layout = MainLayout.class)
@PageTitle(TITLE_CITIZENS)
@SecuredAccess(SecurityConstants.HAS_ORGS_ADMIN_ROLE)
public class CitizensView
        extends AbstractUsersView
        implements HasNotifications, HasUrlParameter<String> {

    private final CitizenCrudPresenter presenter;

    public CitizensView(@Autowired CitizenRepository repository,
                        @Autowired CrudEditor<UserRepresentation> crudEditor) {

        super(UserEntityConstants.CITIZEN_NAME, crudEditor);
        this.presenter = new CitizenCrudPresenter(repository,this);
    }

    @Override
    public void setupDataProvider()
    {
        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> presenter.findAll(fetch.getOffset(),fetch.getLimit()).stream(),
                count -> presenter.count()));
    }

    public void setupEventListeners()
    {
        this.addSaveListener(e -> {
            var user = e.getItem();
            if (user.getId() != null) {
                if (!presenter.update(user))
                    this.cancelSave();
            } else {
                if (!presenter.save(user))
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
        getUI().ifPresent(ui -> ui.navigate(NavigationUtil.generateLocation(PAGE_ORGS_CITIZENS, id)));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,@OptionalParameter String id) {
        if (id != null) {
            var user = getEditor().getItem();
            if (user != null && id.equals(user.getId())) {
                return;
            }
            try{
                user = presenter.findById(id).orElseThrow(NotFoundException::new);
                edit(user, EditMode.EXISTING_ITEM);
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }
        } else {
            setOpened(false);
        }
    }
}
