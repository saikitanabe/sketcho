package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.content.ui.LineSelections.RelationShipType;
import net.sevenscales.editor.content.ui.LineSelections.SelectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectButtonBox extends Composite implements SelectionHandler {

	private static SelectButtonBoxUiBinder uiBinder = GWT
			.create(SelectButtonBoxUiBinder.class);

	interface SelectButtonBoxUiBinder extends UiBinder<Widget, SelectButtonBox> {
	}

	@UiField
	TableCellElement relationbutton;
	@UiField DivElement relationarrow;
	@UiField
	TableCellElement drop;
	@UiField
	HTMLPanel panel;
	
	PopupPanel popup;
	private RelationShipType currentRelationshipType = RelationShipType.DIRECTED;
	private EditorContext editorContext;

	public SelectButtonBox(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
		
		editorContext.set(EditorProperty.CURRENT_RELATIONSHIP_TYPE, currentRelationshipType);
		
		popup = new PopupPanel();
		popup.setStyleName("SelectButtonBoxPopup");
		LineSelections ls = new LineSelections();
		ls.setSelectionHandler(SelectButtonBox.this);
		popup.setWidget(ls);
		popup.setAutoHideEnabled(true);

		DOM.sinkEvents((com.google.gwt.user.client.Element) relationbutton.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) relationbutton.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							SelectButtonBox.this.editorContext.getEventBus().fireEvent(new RelationshipTypeSelectedEvent(currentRelationshipType));
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
							int left = SelectButtonBox.this.getAbsoluteLeft() + 0;
							int top = SelectButtonBox.this.getAbsoluteTop() + 30;
							if (SelectButtonBox.this.editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
								top = SelectButtonBox.this.getAbsoluteTop() - 207;
							}
							popup.setPopupPosition(left, top);
							popup.show();
							break;
						}
					}
				});
	}

	private void removeLineClassNames() {
		relationarrow.removeClassName("icon-conn-directed");
		relationarrow.removeClassName("icon-conn-inheritance");
		relationarrow.removeClassName("icon-conn-line");
		relationarrow.removeClassName("icon-conn-dependency");
		relationarrow.removeClassName("icon-conn-dashedline");
		relationarrow.removeClassName("icon-conn-aggregation");
		relationarrow.removeClassName("icon-conn-aggregationboth");
		relationarrow.removeClassName("icon-conn-reverse");
	}

	@Override
	public void itemSelected(RelationShipType type) {
		removeLineClassNames();
		switch (type) {
		case DIRECTED:
			relationarrow.addClassName("icon-conn-directed");
			break;
		case INHERITANCE:
			relationarrow.addClassName("icon-conn-inheritance");
			break;
		case LINE:
			relationarrow.addClassName("icon-conn-line");
			break;
		case DEPENDANCY_DIRECTED:
			relationarrow.addClassName("icon-conn-dependency");
			break;
		case DEPENDANCY:
			relationarrow.addClassName("icon-conn-dashedline");
			break;
		case AGGREGATION_DIRECTED:
			relationarrow.addClassName("icon-conn-aggregation");
			break;
		case AGGREGATION:
			relationarrow.addClassName("icon-conn-aggregationboth");
			break;
		case REVERSE:
			relationarrow.addClassName("icon-conn-reverse");
			break;
		}
		select(type);
	}

	private void select(RelationShipType type) {
		currentRelationshipType = type;
		popup.hide();
		
		editorContext.set(EditorProperty.CURRENT_RELATIONSHIP_TYPE, currentRelationshipType);
		System.out.println("type: " + type);
		editorContext.getEventBus().fireEvent(new RelationshipTypeSelectedEvent(type));
	}

}
