package org.hua.hermes.frontend.bean.editor;

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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.server.VaadinSession;
import org.hua.hermes.frontend.component.TrimmedTextField;
import org.hua.hermes.frontend.constant.entity.UserEntityConstants;
import org.hua.hermes.frontend.constant.MessageConstants;

import org.hua.hermes.frontend.util.KeycloakBindUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
        TrimmedTextField username = new TrimmedTextField(UserEntityConstants.USERNAME_LABEL);
        TrimmedTextField firstName = new TrimmedTextField(UserEntityConstants.FIRST_NAME_LABEL);
        TrimmedTextField lastName = new TrimmedTextField(UserEntityConstants.LAST_NAME_LABEL);

        RadioButtonGroup<String> sex = new RadioButtonGroup<>();
        sex.setLabel(UserEntityConstants.GENDER_LABEL);
        sex.setItems(UserEntityConstants.GENDER_CHOICES);
        sex.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        TrimmedTextField email = new TrimmedTextField(UserEntityConstants.EMAIL_LABEL);
        TrimmedTextField phoneNumber = new TrimmedTextField(UserEntityConstants.PHONE_LABEL);

        Checkbox enabled = new Checkbox(UserEntityConstants.ENABLED_LABEL);
        enabled.getElement().setAttribute("colspan", "2");
        enabled.getStyle().set("marginTop","10px");

        EnhancedDatePicker birthdateDatePicker = new EnhancedDatePicker();

        DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, VaadinSession.getCurrent().getLocale());
        String pattern = ((SimpleDateFormat)f).toPattern();
        birthdateDatePicker.setPattern(pattern);

        birthdateDatePicker.setLabel(UserEntityConstants.BIRTHDATE_LABEL + " (" + pattern + ")");
        birthdateDatePicker.setClearButtonVisible(true);
        birthdateDatePicker.getElement().setAttribute("colspan", "2");

        var basicInfoForm = new FormLayout(username, firstName, lastName, sex, phoneNumber, email, birthdateDatePicker, enabled);
        //endregion

        //region Location Form
        TrimmedTextField street = new TrimmedTextField(UserEntityConstants.STREET_ADDRESS_LABEL);
        TrimmedTextField postalCode = new TrimmedTextField(UserEntityConstants.POSTAL_CODE_LABEL);
        TrimmedTextField locality = new TrimmedTextField(UserEntityConstants.LOCALITY_LABEL);
        TrimmedTextField region = new TrimmedTextField(UserEntityConstants.REGION_LABEL);

        ComboBox<String> country = new ComboBox<>(UserEntityConstants.COUNTRY_LABEL);
        country.setDataProvider(DataProvider.ofItems(countries));

        var locationForm = new FormLayout(street,postalCode,locality,region,country);
        //endregion

        //region Password Form
        PasswordField password = new PasswordField(UserEntityConstants.PASSWORD_LABEL);
        password.getElement().setAttribute("colspan", "2");

        PasswordField confirmPassword = new PasswordField(UserEntityConstants.CONFIRM_PASSWORD_LABEL);
        confirmPassword.getElement().setAttribute("colspan", "2");

        Checkbox temporaryPassword = new Checkbox(UserEntityConstants.TEMPORARY_PASSWORD_LABEL);
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
                .asRequired(MessageConstants.REQUIRED)
                .withValidator(x -> {
                    //If user is not persisted or they are persisted and username hasn't changed then validation passes
                    var user = editor.getItem();
                    if (user == null) return true;
                    return user.getId() == null || x.equals(user.getUsername());
                }, "You cannot change a username once it is assigned to the user.")
                .bind(UserRepresentation::getUsername, UserRepresentation::setUsername);

        binder.forField(firstName)
                .asRequired(MessageConstants.REQUIRED)
                .bind(UserRepresentation::getFirstName, UserRepresentation::setFirstName);

        binder.forField(lastName)
                .asRequired(MessageConstants.REQUIRED)
                .bind(UserRepresentation::getLastName, UserRepresentation::setLastName);

        binder.forField(sex)
                .asRequired(MessageConstants.REQUIRED)
                .bind((user) -> user.firstAttribute(UserEntityConstants.GENDER),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.GENDER,value));

        binder.forField(phoneNumber)
                .asRequired(MessageConstants.REQUIRED)
                .withValidator(new RegexpValidator("Please enter a valid phone number","^[0-9]*$"))
                .bind((user) -> user.firstAttribute(UserEntityConstants.PHONE),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.PHONE,value));

        binder.forField(email)
                .asRequired(MessageConstants.REQUIRED)
                .withValidator(new EmailValidator(MessageConstants.INVALID_EMAIL))
                .bind(UserRepresentation::getEmail, UserRepresentation::setEmail);

        binder.forField(birthdateDatePicker)
                .asRequired("Please choose a date")
                .bind(user ->
                        {
                            if(user == null || user.firstAttribute(UserEntityConstants.BIRTHDATE) == null)
                                return null;
                            return LocalDate.parse(
                                        user.firstAttribute(UserEntityConstants.BIRTHDATE),
                                        UserEntityConstants.BIRTHDATE_FORMATTER
                                );
                        },
                        (user, value) ->
                            KeycloakBindUtils.setAttribute(
                                    user,
                                    UserEntityConstants.BIRTHDATE,
                                    UserEntityConstants.BIRTHDATE_FORMATTER.format(value)
                            )
                        );

        binder.forField(enabled).bind(UserRepresentation::isEnabled, UserRepresentation::setEnabled);
        //endregion

        //region Location binds
        binder.forField(street)
                .asRequired(MessageConstants.REQUIRED)
                .bind(user -> user.firstAttribute(UserEntityConstants.STREET_ADDRESS),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.STREET_ADDRESS,value));

        binder.forField(postalCode)
                .asRequired(MessageConstants.REQUIRED)
                .bind(user -> user.firstAttribute(UserEntityConstants.POSTAL_CODE),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.POSTAL_CODE,value));

        binder.forField(locality)
                .asRequired(MessageConstants.REQUIRED)
                .bind(user -> user.firstAttribute(UserEntityConstants.LOCALITY),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.LOCALITY,value));

        binder.forField(region)
                .asRequired(MessageConstants.REQUIRED)
                .bind(user -> user.firstAttribute(UserEntityConstants.REGION),
                        (user,value) -> KeycloakBindUtils.setAttribute(user, UserEntityConstants.REGION,value));

        binder.forField(country)
                .asRequired(MessageConstants.REQUIRED)
                   .bind(user -> user.firstAttribute(UserEntityConstants.COUNTRY),
                        (user,value) ->
                        {
                            KeycloakBindUtils.setAttribute(user, UserEntityConstants.COUNTRY,value);
                            //Also bind formatted here
                            KeycloakBindUtils.setAttribute(editor.getItem(),
                                    UserEntityConstants.FORMATTED_LOCATION,
                                    createFormatted(editor.getItem()));
                        });
        //endregion

        //region Password binds + Status listener for alert
        binder.forField(password)
                .asRequired(Validator.from(p -> {
                    if (editor.getItem() == null || editor.getItem().getId() != null) return true;
                    return !p.isEmpty();
                }, MessageConstants.REQUIRED))
                .withValidator(pass -> pass.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})$"),
                        "Need 8 or more chars, mixing digits, lowercase and uppercase letters")
                .bind(user -> null, (user, value) -> {
                    if (!password.getEmptyValue().equals(password.getValue())) {
                        KeycloakBindUtils.setCredentials(user,value,temporaryPassword.getValue());
                    }
                });

        binder.forField(confirmPassword)
                .asRequired(Validator.from(p -> {
                    if (editor.getItem() == null || editor.getItem().getId() != null) return true;
                    return !p.isEmpty();
                }, MessageConstants.REQUIRED))
                .withValidator(pass -> pass.equals(password.getValue()),
                        "Passwords do not match")
                .bind(user -> null, (user,pass) ->{});

        //endregion

        return editor;
    }

    public String createFormatted(UserRepresentation user){
        return Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.STREET_ADDRESS),"") + ","
                + Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.LOCALITY), "") + ","
                + Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.REGION),"") + ","
                + Objects.requireNonNullElse(user.firstAttribute(UserEntityConstants.COUNTRY),"");
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