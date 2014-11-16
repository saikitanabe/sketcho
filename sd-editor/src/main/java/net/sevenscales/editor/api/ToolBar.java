package net.sevenscales.editor.api;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;


public class ToolBar extends Composite {

  private static ToolBarUiBinder uiBinder = GWT.create(ToolBarUiBinder.class);
	interface ToolBarUiBinder extends UiBinder<Widget, ToolBar> {
	}

	@UiField Element map;
	@UiField Element freehand;
	@UiField Element undo;
	@UiField Element redo;

	private ISurfaceHandler surface;

	public ToolBar(ISurfaceHandler surface) {
		this.surface = surface;

		initWidget(uiBinder.createAndBindUi(this));

		setStyleName("toolbar2");

		handleButtons(this, map, freehand, undo, redo);
	}

	private native void handleButtons(ToolBar me, Element map, Element freehand, Element undo, Element redo)/*-{
		$wnd.Hammer(map).on('tap', function() {
			me.@net.sevenscales.editor.api.ToolBar::onMap()()
		})
		$wnd.Hammer(freehand).on('tap', function() {
			me.@net.sevenscales.editor.api.ToolBar::onFreehand()()
		})

		$wnd.Hammer(undo).on('tap', function() {
			me.@net.sevenscales.editor.api.ToolBar::onUndo()()
		})
		$wnd.Hammer(redo).on('tap', function() {
			me.@net.sevenscales.editor.api.ToolBar::onRedo()()
		})
	}-*/;

	private void onMap() {

	}
	private void onFreehand() {
		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)));

		enableButton(freehand, surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE));
	}
	private native void enableButton(Element e, boolean enable)/*-{
		$wnd.$(e).find('span').toggleClass('menu-btn2-big-active')
		$wnd.$(e).find('a').toggleClass('menu-btn2-active')
		$wnd.$(e).find('i').toggleClass('menu-ico2-freehand-active')
	}-*/;

	private void onUndo() {
	}
	private void onRedo() {
	}

}