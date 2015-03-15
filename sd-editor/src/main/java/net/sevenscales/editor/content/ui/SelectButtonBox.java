package net.sevenscales.editor.content.ui;

import java.util.Set;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.content.ui.LineSelections.SelectionHandler;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.diagram.Diagram;

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

public class SelectButtonBox extends Composite implements SelectionHandler, LineSelections.IParent {

	private static SelectButtonBoxUiBinder uiBinder = GWT
			.create(SelectButtonBoxUiBinder.class);

	interface SelectButtonBoxUiBinder extends UiBinder<Widget, SelectButtonBox> {
	}

	public interface IParent {
		void show();
	}

	@UiField TableCellElement relationbutton;
	@UiField DivElement relationarrow;
	@UiField TableCellElement drop;
	@UiField HTMLPanel panel;
	
	PopupPanel popup;
	private RelationShipType currentRelationshipType = RelationShipType.DIRECTED;
	private EditorContext editorContext;
	private net.sevenscales.editor.diagram.SelectionHandler selectionHandler;
	private boolean popupUp;
	private IParent parent;

	public SelectButtonBox(IParent parent, EditorContext editorContext, net.sevenscales.editor.diagram.SelectionHandler selectionHandler) {
		this(parent, editorContext, selectionHandler, true);
	}

	public SelectButtonBox(IParent parent, EditorContext editorContext, net.sevenscales.editor.diagram.SelectionHandler selectionHandler, boolean popupUp) {
		this.parent = parent;
		this.editorContext = editorContext;
		this.selectionHandler = selectionHandler;
		this.popupUp = popupUp;
		initWidget(uiBinder.createAndBindUi(this));
		
		editorContext.set(EditorProperty.CURRENT_RELATIONSHIP_TYPE, currentRelationshipType);
		
		popup = new PopupPanel();
		popup.setStyleName("RelationshipDragEndHandler");
		LineSelections ls = new LineSelections(this);
		ls.setSelectionHandler(SelectButtonBox.this);
		popup.setWidget(ls);
		popup.setAutoHideEnabled(true);
		popup.addAutoHidePartner(drop);

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
							showPopup();
							break;
						}
					}
				});
		init(this);
	}

	public boolean isShowing() {
		return SelectButtonBox.this.popup.isShowing();
	}

	private native void init(SelectButtonBox me)/*-{
		$wnd.spaceKeyManager.stream.onValue(function(value) {
			$wnd.console.log("space... $wnd.globalState.contextMenuOpen", $wnd.globalState.contextMenuOpen, $wnd.isEditorOpen())
			if (!$wnd.globalState.contextMenuOpen && !$wnd.isEditorOpen() && 
					!$wnd.spaceKeyManager.isHandled()) {
				// do not allow to show switch if editor is open
				me.@net.sevenscales.editor.content.ui.SelectButtonBox::showPopup()();
			}
		})
		$wnd.cancelStream.onValue(function() {
			me.@net.sevenscales.editor.content.ui.SelectButtonBox::hidePopup()();
		})		
	}-*/;

	private boolean allSelectionsRelationShips() {
		Set<Diagram> selected = selectionHandler.getSelectedItems();
		for (Diagram d : selected) {
			if (!(d instanceof Relationship2)) {
				return false;
			}
		}
		// there needs to be at least one shape, and that is relationship
		return selected.size() > 0;
	}

	private void showPopup() {
		if (!popup.isShowing() && allSelectionsRelationShips()) {
			parent.show();
			int left = SelectButtonBox.this.getAbsoluteLeft() - 60;
			int top = SelectButtonBox.this.getAbsoluteTop() + 30;
			// popup.setWidth(panel.getElement().getStyle().getWidth());
			if (popupUp) {
				if (SelectButtonBox.this.editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
					top = SelectButtonBox.this.getAbsoluteTop() - 115;
				}
			}
			popup.setPopupPosition(left, top);
			popup.show();
		} else {
			hidePopup();
		}
	}

	private void hidePopup() {
		popup.hide();
	}

	private void removeLineClassNames() {
		relationarrow.removeClassName("arrow-icon-directed");
		relationarrow.removeClassName("arrow-icon-directed-both");
		relationarrow.removeClassName("arrow-icon-inheritance");
		relationarrow.removeClassName("arrow-icon-line");
		relationarrow.removeClassName("arrow-icon-dependency");
		relationarrow.removeClassName("arrow-icon-dependency-both");
		relationarrow.removeClassName("arrow-icon-dashedline");
		relationarrow.removeClassName("arrow-icon-aggregation");
		relationarrow.removeClassName("arrow-icon-aggregation-filled");
		relationarrow.removeClassName("arrow-icon-aggregationboth");
		relationarrow.removeClassName("arrow-icon-aggregationboth-filled");
		relationarrow.removeClassName("arrow-icon-synch");
	}

	@Override
	public void itemSelected(RelationShipType type) {
		removeLineClassNames();
		switch (type) {
		case DIRECTED:
			relationarrow.addClassName("arrow-icon-directed");
			break;
		case DIRECTED_BOTH:
			relationarrow.addClassName("arrow-icon-directed-both");
			break;
		case INHERITANCE:
			relationarrow.addClassName("arrow-icon-inheritance");
			break;
		case LINE:
			relationarrow.addClassName("arrow-icon-line");
			break;
		case DEPENDANCY_DIRECTED:
			relationarrow.addClassName("arrow-icon-dependency");
			break;
		case DEPENDANCY_DIRECTED_BOTH:
			relationarrow.addClassName("arrow-icon-dependency-both");
			break;
		case DEPENDANCY:
			relationarrow.addClassName("arrow-icon-dashedline");
			break;
		case AGGREGATION_DIRECTED:
			relationarrow.addClassName("arrow-icon-aggregation");
			break;
		case AGGREGATION_DIRECTED_FILLED:
			relationarrow.addClassName("arrow-icon-aggregation-filled");
			break;
		case AGGREGATION:
			relationarrow.addClassName("arrow-icon-aggregationboth");
			break;
		case AGGREGATION_FILLED:
			relationarrow.addClassName("arrow-icon-aggregationboth-filled");
			break;
		case SYNCHRONIZED:
			relationarrow.addClassName("arrow-icon-synch");
			break;
		case REALIZE:
			relationarrow.addClassName("arrow-icon-realize");
			break;
		}
		select(type);
	}

	private void select(RelationShipType type) {
		currentRelationshipType = type;

		hidePopup();
		
		editorContext.set(EditorProperty.CURRENT_RELATIONSHIP_TYPE, currentRelationshipType);
		editorContext.getEventBus().fireEvent(new RelationshipTypeSelectedEvent(type));
	}

}
