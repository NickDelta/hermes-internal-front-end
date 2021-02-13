package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.server.VaadinSession;
import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.util.DateTimeUtils;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.FormatStyle;

public abstract class AbstractUsersView extends AbstractCrudView<UserRepresentation>
{

    public AbstractUsersView(String entityName, CrudEditor<UserRepresentation> editor)
    {
        super(UserRepresentation.class, entityName, editor);
    }

    @Override
    public void setupGrid()
    {
        getGrid().addColumn(UserRepresentation::getId).setHeader(UserEntityConstants.ID_LABEL);
        getGrid().addColumn(UserRepresentation::getUsername).setHeader(UserEntityConstants.USERNAME_LABEL);
        getGrid().addColumn(UserRepresentation::getFirstName).setHeader(UserEntityConstants.FIRST_NAME_LABEL);
        getGrid().addColumn(UserRepresentation::getLastName).setHeader(UserEntityConstants.LAST_NAME_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.GENDER)).setHeader(UserEntityConstants.GENDER_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.PHONE)).setHeader(UserEntityConstants.PHONE_LABEL);
        getGrid().addColumn(UserRepresentation::getEmail).setHeader(UserEntityConstants.EMAIL_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.BIRTHDATE)).setHeader(UserEntityConstants.BIRTHDATE_LABEL);

        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.STREET_ADDRESS)).setHeader(UserEntityConstants.STREET_ADDRESS_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.POSTAL_CODE)).setHeader(UserEntityConstants.POSTAL_CODE_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.LOCALITY)).setHeader(UserEntityConstants.LOCALITY_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.REGION)).setHeader(UserEntityConstants.REGION_LABEL);
        getGrid().addColumn(user -> user.firstAttribute(UserEntityConstants.COUNTRY)).setHeader(UserEntityConstants.COUNTRY_LABEL);

        getGrid().addColumn(user -> {
            LocalDateTime creationDate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(user.getCreatedTimestamp()),
                    VaadinSession.getCurrent().getAttribute(ZoneId.class)
            );
            return DateTimeUtils.formatDate(FormatStyle.SHORT,creationDate);
        }).setHeader(UserEntityConstants.CREATED_ON_LABEL);

        getGrid().addComponentColumn(user -> {
            BadgeColor color;
            String status;
            if (user.isEnabled()) {
                color = BadgeColor.SUCCESS_PRIMARY;
                status = "Enabled";
            } else {
                color = BadgeColor.ERROR_PRIMARY;
                status = "Disabled";
            }
            var badge = new StatusBadge(status, color, BadgeSize.M, BadgeShape.PILL);
            badge.getElement().setProperty("title", status);
            return badge;
        }).setHeader(UserEntityConstants.ACCOUNT_STATUS_LABEL);

        addEditColumn(getGrid(),setupI18n());

        getGrid().getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));

    }
}
