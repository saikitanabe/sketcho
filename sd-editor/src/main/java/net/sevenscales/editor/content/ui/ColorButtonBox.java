package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.event.ColorSelectedEventHandler;
import net.sevenscales.editor.content.ui.ColorSelections.SelectionHandler;
import net.sevenscales.editor.diagram.utils.Color;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is deprecated, do not used, for now ui context menu is used
 * to launch color selections.
 */
public class ColorButtonBox extends Composite implements SelectionHandler {

	private static ColorButtonBoxUiBinder uiBinder = GWT
			.create(ColorButtonBoxUiBinder.class);

	interface ColorButtonBoxUiBinder extends UiBinder<Widget, ColorButtonBox> {
	}
	
	private PopupPanel popup;
	@UiField
	TableCellElement colorbutton;
	@UiField
	TableCellElement drop;
	
	private EditorContext editorContext;
	private Color color = new Color("444444", 0x44, 0x44, 0x44, "6699ff", 0x66, 0x99, 0xff, "FFFFFF", 255, 255, 255, AbstractDiagramItem.DEFAULT_FILL_OPACITY);
	
	private ColorSelectedEventHandler colorSelectionHandler = new ColorSelectedEventHandler() {
		@Override
		public void onSelection(ColorSelectedEvent event) {
			editorContext.set(EditorProperty.CURRENT_COLOR, event.getColor());
		}
	};

	public ColorButtonBox(EditorContext editorContext) {
		this.editorContext = editorContext;
		editorContext.set(EditorProperty.CURRENT_COLOR, color);

		initWidget(uiBinder.createAndBindUi(this));
		
		editorContext.getEventBus().addHandler(ColorSelectedEvent.TYPE, colorSelectionHandler);
		setCurrentColors();
		
		popup = new PopupPanel();
		popup.setStyleName("ColorButtonBox");

		ColorSelections cs = new ColorSelections(editorContext);
		cs.setSelectionHandler(this);
		popup.setWidget(cs);
		popup.setAutoHideEnabled(true);

		DOM.sinkEvents((com.google.gwt.user.client.Element) colorbutton.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) colorbutton.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							ColorButtonBox.this.editorContext.getEventBus().fireEvent(new ColorSelectedEvent(color, null));
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) drop.cast(),
				Event.ONCLICK);
		DOM.setEventListener((com.google.gwt.user.client.Element) drop.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							// popup.setWidth(panel.getElement().getStyle().getWidth());
							int left = ColorButtonBox.this.getAbsoluteLeft() + 0;
							int top = ColorButtonBox.this.getAbsoluteTop() + 30;
							
							if (ColorButtonBox.this.editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
								// HACK
								top = ColorButtonBox.this.getAbsoluteTop() - 191;
							}
							popup.setPopupPosition(left, top);
							popup.show();
							break;
						}
					}
				});
	}
	
	private void setCurrentColors() {
		colorbutton.getStyle().setBackgroundColor(color.getBackgroundColor4Web());
		colorbutton.getStyle().setColor(color.getTextColor4Web());
	}

	@Override
	public void itemSelected(Color color, ColorTarget colorTarget) {
		popup.hide();
		this.color = color;
		setCurrentColors();
		editorContext.getEventBus().fireEvent(new ColorSelectedEvent(color, colorTarget));
	}

}
