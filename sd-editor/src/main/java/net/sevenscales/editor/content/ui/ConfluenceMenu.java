package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.CancelButtonClickedEvent;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.PotentialOnChangedEventHandler;
import net.sevenscales.editor.api.event.SaveButtonClickedEvent;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.content.utils.JQuery;
import net.sevenscales.editor.content.utils.KeyHelpers;
import net.sevenscales.editor.diagram.Diagram;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ConfluenceMenu extends Composite {

	private static ConfluenceMenuUiBinder uiBinder = GWT
			.create(ConfluenceMenuUiBinder.class);

	interface ConfluenceMenuUiBinder extends UiBinder<Widget, ConfluenceMenu> {
	}
	
	@UiField FastButton btnSave;
	@UiField FastButton btnCancel;
	@UiField AnchorElement btnSaveAnchor;
	
	private PotentialOnChangedEventHandler operationHandler = new PotentialOnChangedEventHandler() {
		@Override
		public void on(PotentialOnChangedEvent event) {
			if (event.getDiagrams().size() > 0) {
				// mark board as edited
				btnSaveAnchor.addClassName("btn-primary");
			}
		}
	};
	
	private BoardRemoveDiagramsEventHandler removeHandler = new BoardRemoveDiagramsEventHandler() {
		@Override
		public void on(BoardRemoveDiagramsEvent event) {
			// mark board as edited
			btnSaveAnchor.addClassName("btn-primary");
		}
	};
	private EditorContext editorContext;

	public ConfluenceMenu(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
    editorContext.getEventBus().addHandler(PotentialOnChangedEvent.TYPE, operationHandler);
    editorContext.getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, removeHandler);
    
		Event.addNativePreviewHandler(new NativePreviewHandler() {
		  @Override
		  public void onPreviewNativeEvent(NativePreviewEvent event) {
		    if (event.getTypeInt() == Event.ONKEYDOWN) {
		      if ( KeyHelpers.isSave(event)) {
		      	// ctrl/cmd + s
		        event.cancel();
		        fireSave();
		      }
		    }
		  }
		});
	}

//	private void setClickHandlerToElement(ButtonElement btn, EventListener clickHandler) {
//		DOM.sinkEvents((com.google.gwt.user.client.Element) btn.cast(),
//				Event.ONCLICK);
//		DOM.setEventListener(
//				(com.google.gwt.user.client.Element) btn.cast(), clickHandler);
//	}

//	public void addSaveClickHandler(EventListener clickHandler) {
//		setClickHandlerToElement(btnSave, clickHandler);
//	}
//
//	public void addCancelClickHandler(EventListener clickHandler) {
//		setClickHandlerToElement(btnCancel, clickHandler);
//	}

	public void clear() {
		btnSaveAnchor.removeClassName("btn-primary");
	}
	
	@UiHandler("btnSave")
	public void onSave(ClickEvent event) {
		fireSave();
	}
	
	private void fireSave() {
		editorContext.getEventBus().fireEvent(new SaveButtonClickedEvent());
	}
	
	@UiHandler("btnCancel")
	public void onCancel(ClickEvent event) {
		editorContext.getEventBus().fireEvent(new CancelButtonClickedEvent());
	}

}
