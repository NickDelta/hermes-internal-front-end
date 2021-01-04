package org.hua.hermes.frontend.component;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;

import java.util.StringJoiner;

import static org.hua.hermes.frontend.util.style.css.lumo.BadgeShape.PILL;

/**
 * Taken from the  <a href="https://vaadin.com/start/v14/business-app">Business Starter App</a>
 * @author Vaadin Ltd
 */
@Tag("status-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge")
public class StatusBadge extends Span {

	public StatusBadge(String text) {
		this(text, BadgeColor.NORMAL);
	}

	public StatusBadge(String text, BadgeColor color) {
		super(text);
		UIUtils.setTheme(color.getThemeName(), this);
	}

	public StatusBadge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
		super(text);
		StringJoiner joiner = new StringJoiner(" ");
		joiner.add(color.getThemeName());
		if (shape.equals(PILL)) {
			joiner.add(shape.getThemeName());
		}
		if (size.equals(BadgeSize.S)) {
			joiner.add(size.getThemeName());
		}
		UIUtils.setTheme(joiner.toString(), this);
	}

}
