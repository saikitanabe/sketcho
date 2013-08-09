package net.sevenscales.appFrame.impl;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class ButtonAction extends Action { 
	private Button button;
	
	public ButtonAction(ClickHandler clickHandler) {
		super();
		addClickHandler(clickHandler);
		button = new Button();
		initWidget(button);
	}

	public void setName(String name) {
		button.setText(name);
	}
	
	public String getName() {
		return button.getText();
	}
	
	public Button getButton() {
		return button;
	}
}
