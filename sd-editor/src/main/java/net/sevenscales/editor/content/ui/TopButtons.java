package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.CommentModeEventHandler;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TopButtons extends Composite {

	private static TopButtonsUiBinder uiBinder = GWT
			.create(TopButtonsUiBinder.class);

	interface TopButtonsUiBinder extends UiBinder<Widget, TopButtons> {
	}

	private EditorContext editorContext;
	
	@UiField ButtonElement freehandOn;
	@UiField ButtonElement commentModeOn;

	public TopButtons(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);
		
		editorContext.getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {

			@Override
			public void on(FreehandModeChangedEvent event) {
				// hack due to event order
				setVisible(event);
			}
		});

		editorContext.getEventBus().addHandler(CommentModeEvent.TYPE, new CommentModeEventHandler() {
			@Override
			public void on(CommentModeEvent event) {
				showHideCommentMode(event.isEnabled());
			}
		});
		
		DOM.sinkEvents((com.google.gwt.user.client.Element) freehandOn.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) freehandOn.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							setVisible(false);
							TopButtons.this.editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(false));
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) commentModeOn.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) commentModeOn.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							hideCommentMode();
							break;
						}
					}
				});
	}

	public void setVisible(FreehandModeChangedEvent event) {
		super.setVisible(Tools.isCommentMode());
		freehandOn.getStyle().setDisplay(Display.NONE);
		if (event.isEnabled()) {
			// do not set visible if freehand mode is not on
			// this is due to initial load
			super.setVisible(event.isEnabled());
			freehandOn.getStyle().setDisplay(Display.INLINE);
			// String text = editorContext.<FreehandModeType>getAs(EditorProperty.FREEHAND_MODE_TYPE).toString();
			// if (event.isModeTypeChanged()) {
			// 	text = event.getModeType().toString();
			// }
			// // freehandOn.setInnerText("Freehand " + text);
			// freehandOn.setInnerText("Freehand ON");
		} 
	}

	private void hideCommentMode() {
		Tools.setCommentTool(false);
	}

	private void showHideCommentMode(boolean show) {
		Tools.setCommentTool(show);

		if (Tools.isCommentMode()) {
			super.setVisible(true);
			commentModeOn.getStyle().setDisplay(Display.INLINE);
		} else {
			super.setVisible(editorContext.isTrue(EditorProperty.FREEHAND_MODE));
			commentModeOn.getStyle().setDisplay(Display.NONE);
		}
	}
	
}
