package org.hua.hermes.frontend.error.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import lombok.Getter;

/**
 * A Designer generated component for the error-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("error-view")
@JsModule("./src/error-view.js")
@Getter
public class ErrorView extends PolymerTemplate<ErrorView.ErrorViewModel> {

    @Id("errorCode")
    private H1 errorCode;
    @Id("errorShortText")
    private H2 shortText;
    @Id("errorDetails")
    private Paragraph details;
    @Id("homeButton")
    private Button homeButton;
    @Id("image")
    private Image image;

    /**
     * Creates a new ErrorView.
     */
    public ErrorView(String errorCode, String shortText, String description, String imgSrc, String imgAlt) {
       this.errorCode.setText(errorCode);
       this.shortText.setText(shortText);
       this.details.setText(description);
       this.image.setSrc(imgSrc);
       this.image.setAlt(imgAlt);
    }

    /**
     * This model binds properties between ErrorView and error-view
     */
    public interface ErrorViewModel extends TemplateModel {
        // Add setters and getters for template properties here.
    }
}
