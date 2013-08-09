package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.RedoEvent;
import net.sevenscales.editor.api.event.UndoEvent;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.content.utils.EffectHelpers;
import net.sevenscales.editor.content.utils.JQuery;

import com.google.gwt.core.client.GWT;
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

public class UndoMenu extends Composite {

	private static UndoMenuUiBinder uiBinder = GWT.create(UndoMenuUiBinder.class);

	interface UndoMenuUiBinder extends UiBinder<Widget, UndoMenu> {
	}

	private EditorContext editorContext;
	
	@UiField FastButton undo;
	@UiField FastButton redo;

	public UndoMenu(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
		
		undo.getElement().setId("undo-button");
		redo.getElement().setId("redo-button");
		
		// do not handle undo/redo if property editor is open
		Event.addNativePreviewHandler(new NativePreviewHandler() {
		  @Override
		  public void onPreviewNativeEvent(NativePreviewEvent event) {
		    if (event.getTypeInt() == Event.ONKEYDOWN && !UndoMenu.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
		      NativeEvent ne = event.getNativeEvent();
		      if ( (ne.getCtrlKey() || ne.getMetaKey()) && !ne.getShiftKey() && ne.getKeyCode() == 'Z') {
		      	// ctrl/cmd + z
		        event.cancel();
		        JQuery.flashStyleClass("#" + undo.getElement().getId()+ " > a", "btn-custom-active");
		        fireUndo();
		      } else if ((ne.getCtrlKey() || ne.getMetaKey()) && ne.getShiftKey() && ne.getKeyCode() == 'Z') {
		      	// ctrl/cmd + shift + z
		        event.cancel();
		        JQuery.flashStyleClass("#" + redo.getElement().getId()+ " > a", "btn-custom-active");
		        fireRedo();
		      }
		    }
		  }
		});
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		EffectHelpers.tooltipper();
	}
	
	@UiHandler("undo")
	public void onUndo(ClickEvent event) {
		fireUndo();
	}

	@UiHandler("redo")
	public void onRedo(ClickEvent event) {
		fireRedo();
	}

	private void fireUndo() {
		editorContext.getEventBus().fireEvent(new UndoEvent());
	}

	private void fireRedo() {
		editorContext.getEventBus().fireEvent(new RedoEvent());
	}

}
