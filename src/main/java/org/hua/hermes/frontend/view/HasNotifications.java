package org.hua.hermes.frontend.view;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

/**
 * Interface for views showing notifications to users
 */
public interface HasNotifications extends HasElement {

	default void showNotification(String message) {
		Dialog dialog = new Dialog();

		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);

		Text text = new Text(message);
		Button okButton = new Button("OK", event -> dialog.close());
		dialog.add(text,okButton);

		dialog.open();
	}
}
