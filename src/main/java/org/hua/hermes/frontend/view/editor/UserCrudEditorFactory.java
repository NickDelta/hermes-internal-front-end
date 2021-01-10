package org.hua.hermes.frontend.view.editor;

import com.vaadin.componentfactory.EnhancedDatePicker;
import com.vaadin.componentfactory.enhancedcrud.BinderCrudEditor;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import org.apache.commons.validator.GenericValidator;
import org.hua.hermes.frontend.converter.LocalDateToStringConverter;
import org.hua.hermes.frontend.data.FormattingConstants;
import org.hua.hermes.frontend.data.GenericValidationMessages;

import org.hua.hermes.frontend.data.entity.constants.UserEntityConstants;
import org.hua.hermes.frontend.util.KeycloakBindUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Configuration
public class UserCrudEditorFactory
{

    @Bean
    @Scope(value = "prototype")
    public CrudEditor<UserRepresentation> createUserBinder(@Qualifier("countriesListBean") String[] countries)
    {

        //region Basic Info Form
        TextField username = new TextField(UserEntityConstants.USER_USERNAME_LABEL);
        TextField firstName = new TextField(UserEntityConstants.USER_FIRST_NAME_LABEL);
        TextField lastName = new TextField(UserEntityConstants.USER_LAST_NAME_LABEL);

        RadioButtonGroup<String> sex = new RadioButtonGroup<>();
        sex.setLabel(UserEntityConstants.USER_GENDER_LABEL);
        sex.setItems("Male", "Female", "Other");
        sex.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        TextField email = new TextField(UserEntityConstants.USER_EMAIL_LABEL);
        TextField phoneNumber = new TextField(UserEntityConstants.USER_PHONE_LABEL);

        Checkbox enabled = new Checkbox(UserEntityConstants.USER_ENABLED_LABEL);
        enabled.getElement().setAttribute("colspan", "2");
        enabled.getStyle().set("marginTop","10px");

        EnhancedDatePicker birthdateDatePicker = new EnhancedDatePicker();
        birthdateDatePicker.setPattern(FormattingConstants.DATE_FORMAT);
        birthdateDatePicker.setLabel(UserEntityConstants.USER_BIRTHDATE_LABEL);
        birthdateDatePicker.setClearButtonVisible(true);
        birthdateDatePicker.getElement().setAttribute("colspan", "2");

        var basicInfoForm = new FormLayout(username, firstName, lastName, sex, phoneNumber, email, birthdateDatePicker, enabled);
        //endregion

        //region Location Form
        TextField street = new TextField(UserEntityConstants.USER_STREET_ADDRESS_LABEL);
        TextField postalCode = new TextField(UserEntityConstants.USER_POSTAL_CODE_LABEL);
        TextField locality = new TextField(UserEntityConstants.USER_LOCALITY_LABEL);
        TextField region = new TextField(UserEntityConstants.USER_REGION_LABEL);

        ComboBox<String> country = new ComboBox<>(UserEntityConstants.USER_COUNTRY_LABEL);
        country.setDataProvider(DataProvider.ofItems(countries));

        var locationForm = new FormLayout(street,postalCode,locality,region,country);
        //endregion

        //region Password Form
        PasswordField password = new PasswordField(UserEntityConstants.USER_PASSWORD_LABEL);
        password.getElement().setAttribute("colspan", "2");

        PasswordField confirmPassword = new PasswordField(UserEntityConstants.USER_CONFIRM_PASSWORD_LABEL);
        confirmPassword.getElement().setAttribute("colspan", "2");

        Checkbox temporaryPassword = new Checkbox(UserEntityConstants.USER_TEMPORARY_PASSWORD_LABEL);
        temporaryPassword.getElement().setAttribute("colspan", "2");
        temporaryPassword.getStyle().set("marginTop","10px");

        FormLayout passwordForm = new FormLayout(password,confirmPassword,temporaryPassword);
        //endregion

        //region Accordion Construction
        Accordion accordion = new Accordion();
        accordion.add("Basic Info", basicInfoForm);
        accordion.add("Location Info",locationForm);
        accordion.add("Password",passwordForm);
        //endregion

        var binder = new Binder<>(UserRepresentation.class);
        var editor = new BinderCrudEditor<>(binder, accordion);

        //region Basic Info binds
        binder.forField(username)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .withValidator(x -> {
            //If user is not persisted or they are persisted and username hasn't changed then validation passes
            var user = editor.getItem();
            if (user == null) return true;
            return user.getId() == null || x.equals(user.getUsername());
        }, "You cannot change a username once it is assigned to the user.")
                .bind(UserRepresentation::getUsername, UserRepresentation::setUsername);

        binder.forField(firstName)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(UserRepresentation::getFirstName, UserRepresentation::setFirstName);

        binder.forField(lastName)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(UserRepresentation::getLastName, UserRepresentation::setLastName);

        binder.forField(sex)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind((user) -> user.firstAttribute(UserEntityConstants.USER_GENDER),
                      (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_GENDER,value));

        binder.forField(phoneNumber)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .withValidator(new RegexpValidator("Please enter a valid phone number","^[0-9]*$"))
                .bind((user) -> user.firstAttribute(UserEntityConstants.USER_PHONE),
                      (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_PHONE,value));

        binder.forField(email)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .withValidator(new EmailValidator(GenericValidationMessages.INVALID_EMAIL_TEXT))
                .bind(UserRepresentation::getEmail, UserRepresentation::setEmail);

        binder.forField(birthdateDatePicker)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .withConverter(new LocalDateToStringConverter(FormattingConstants.DATE_FORMAT))
                .withValidator(x -> GenericValidator.isDate(x, FormattingConstants.DATE_FORMAT, true),
                        GenericValidationMessages.INVALID_DATE_TEXT)
                .bind(user -> user.firstAttribute(UserEntityConstants.USER_BIRTHDATE),
                        (user, value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_BIRTHDATE, value));

        binder.forField(enabled)
                .bind(UserRepresentation::isEnabled, UserRepresentation::setEnabled);
        //endregion

        //region Location binds
        binder.forField(street)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(user -> user.firstAttribute(UserEntityConstants.USER_STREET_ADDRESS),
                      (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_STREET_ADDRESS,value));

        binder.forField(postalCode)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(user -> user.firstAttribute(UserEntityConstants.USER_POSTAL_CODE),
                      (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_POSTAL_CODE,value));

        binder.forField(locality)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(user -> user.firstAttribute(UserEntityConstants.USER_LOCALITY),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_LOCALITY,value));

        binder.forField(region)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(user -> user.firstAttribute(UserEntityConstants.USER_REGION),
                     (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_REGION,value));

        binder.forField(country)
                .asRequired(GenericValidationMessages.REQUIRED_TEXT)
                .bind(user -> user.firstAttribute(UserEntityConstants.USER_COUNTRY),
                     (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.USER_COUNTRY,value));

        //Update formatted field whenever a location value changes
        street.addValueChangeListener(listener -> {
            if(editor.getItem() != null)
                KeycloakBindUtils.setAttribute(editor.getItem(), UserEntityConstants.USER_FORMATTED_LOCATION,createFormatted(editor.getItem()));
        });
        locality.addValueChangeListener(listener -> {
            if(editor.getItem() != null)
                KeycloakBindUtils.setAttribute(editor.getItem(), UserEntityConstants.USER_FORMATTED_LOCATION,createFormatted(editor.getItem()));
        });
        postalCode.addValueChangeListener(listener -> {
            if(editor.getItem() != null)
                KeycloakBindUtils.setAttribute(editor.getItem(), UserEntityConstants.USER_FORMATTED_LOCATION,createFormatted(editor.getItem()));
        });
        region.addValueChangeListener(listener -> {
            if(editor.getItem() != null)
                KeycloakBindUtils.setAttribute(editor.getItem(), UserEntityConstants.USER_FORMATTED_LOCATION,createFormatted(editor.getItem()));
        });
        country.addValueChangeListener(listener -> {
            if(editor.getItem() != null)
                KeycloakBindUtils.setAttribute(editor.getItem(), UserEntityConstants.USER_FORMATTED_LOCATION,createFormatted(editor.getItem()));
        });
        //endregion

        //region Password binds + Status listener for alert
        binder.forField(password)
                .asRequired(Validator.from(p -> {
                   if (editor.getItem().getId() != null) return true;
                   return !p.isEmpty();
                },GenericValidationMessages.REQUIRED_TEXT))
                .withValidator(pass -> pass.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})$"),
                        "Need 8 or more chars, mixing digits, lowercase and uppercase letters")
                .bind(user -> null, (user, value) -> {
                    if (!password.getEmptyValue().equals(password.getValue())) {
                        KeycloakBindUtils.setCredentials(user,value,temporaryPassword.getValue());
                    }
                });

        binder.forField(confirmPassword)
                .asRequired(Validator.from(p -> {
                            if (editor.getItem().getId() != null) return true;
                            return !p.isEmpty();
                        },GenericValidationMessages.REQUIRED_TEXT))
                .withValidator(pass -> pass.equals(password.getValue()),
                        "Passwords do not match")
                .bind(user -> null, (user,pass) ->{});

        //endregion

        return editor;
    }

    public String createFormatted(UserRepresentation user){
        return Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.USER_STREET_ADDRESS),"") + ","
                + Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.USER_LOCALITY), "") + ","
                + Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.USER_REGION),"") + ","
                + Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.USER_COUNTRY),"");
    }

    @Bean(name = "countriesListBean")
    public String[] createCountriesList()
    {
        return Arrays.stream(Locale.getISOCountries())
                .map(country -> new Locale("",country)
                .getDisplayCountry())
                .sorted()
                .toArray(String[]::new);
    }

}
