package net.sevenscales.editor.api;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.dom.client.NativeEvent;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.RedoEvent;
import net.sevenscales.editor.api.event.UndoEvent;


public class MainMenu extends Composite {

  private static MainMenuUiBinder uiBinder = GWT.create(MainMenuUiBinder.class);
	interface MainMenuUiBinder extends UiBinder<Widget, MainMenu> {
	}

	@UiField Element newBoard;

	private ISurfaceHandler surface;

	public MainMenu(ISurfaceHandler surface) {
		this.surface = surface;

		initWidget(uiBinder.createAndBindUi(this));

		setStyleName("main-menu");

	}
}

