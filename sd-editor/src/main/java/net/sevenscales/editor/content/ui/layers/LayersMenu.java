package net.sevenscales.editor.content.ui.layers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.ui.PopupPanel;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.FastElementButton;


public class LayersMenu extends Composite {
	// private static final SLogger logger = SLogger.createLogger(LayersMenu.class);
	
	interface LayersMenuUiBinder extends UiBinder<Widget, LayersMenu> {
	}
	private static LayersMenuUiBinder uiBinder = GWT.create(LayersMenuUiBinder.class);

	@UiField AnchorElement moveFront;
	@UiField AnchorElement moveBack;
	@UiField AnchorElement moveBackward;
	@UiField AnchorElement moveForward;

	private ISurfaceHandler surface;
	private PopupPanel parent;

	public LayersMenu(ISurfaceHandler surface, PopupPanel parent) {
		this.surface = surface;
		this.parent = parent;

		initWidget(uiBinder.createAndBindUi(this));

		new FastElementButton(moveFront).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				moveToFront();
			}
		});

		new FastElementButton(moveBack).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				moveToBack();
			}
		});

		new FastElementButton(moveBackward).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				moveToBackward();
			}
		});

		new FastElementButton(moveForward).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				moveToForward();
			}
		});

	}

	private void moveToFront() {
		surface.moveSelectedToFront();
		parent.hide();
	}

	private void moveToBack() {
		surface.moveSelectedToBack();
		parent.hide();
	}

	private void moveToBackward() {
		surface.moveSelectedToBackward();
		// let's not hide menu if user wants to click more than once
	}

	private void moveToForward() {
		surface.moveSelectedToForward();
		// let's not hide menu if user wants to click more than once
	}

	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

}