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

		surface.getEditorContext().getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
			@Override
			public void on(FreehandModeChangedEvent event) {
				if (event.isEnabled()) {
					enabled(event);
				} else {
					disabled();
				}
			}
		});

		handleButtons(this, map, freehand, undo, redo);
		handleUndoRedoShortcuts();
		handleUndoRedoStreams(this);
	}

	private native void handleUndoRedoStreams(ToolBar me)/*-{
		$wnd.globalStreams.undoStackStream.onValue(function(value) {
			me.@net.sevenscales.editor.api.ToolBar::onUndoStackStream(Z)(value);
		})
		$wnd.globalStreams.redoStackStream.onValue(function(value) {
			me.@net.sevenscales.editor.api.ToolBar::onRedoStackStream(Z)(value);
		})
	}-*/;

	private void onUndoStackStream(boolean value) {
		if (value) {
			activateUndo(undo);
		} else {
			inactivateUndo(undo);
		}
	}

	private void onRedoStackStream(boolean value) {
		if (value) {
			activateRedo(redo);
		} else {
			inactivateRedo(redo);
		}
	}

	private native void inactivateUndo(Element e)/*-{
		$wnd.$(e).find('i').addClass('menu-ico2-undo-inactive')
		$wnd.$(e).find('a').addClass('menu-btn2-left-inactive')
	}-*/;

	private native void activateUndo(Element e)/*-{
		$wnd.$(e).find('i').removeClass('menu-ico2-undo-inactive')
		$wnd.$(e).find('a').removeClass('menu-btn2-left-inactive')
	}-*/;

	private native void inactivateRedo(Element e)/*-{
		$wnd.$(e).find('a').removeClass('menu-btn2-join-active')
		$wnd.$(e).find('i').addClass('menu-ico2-redo-inactive')
	}-*/;

	private native void activateRedo(Element e)/*-{
		$wnd.$(e).find('a').addClass('menu-btn2-join-active')
		$wnd.$(e).find('i').removeClass('menu-ico2-redo-inactive')
	}-*/;

	private void handleUndoRedoShortcuts() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {
		  @Override
		  public void onPreviewNativeEvent(NativePreviewEvent event) {
		  	undoRedoCheck(event);
		  }
		});
	}

	private void undoRedoCheck(NativePreviewEvent event) {
    if (event.getTypeInt() == Event.ONKEYDOWN && !surface.getEditorContext().isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
      NativeEvent ne = event.getNativeEvent();
      if ( (ne.getCtrlKey() || ne.getMetaKey()) && !ne.getShiftKey() && ne.getKeyCode() == 'Z') {
      	// ctrl/cmd + z
        event.cancel();
        // JQuery.flashStyleClass("#" + undo.getElement().getId()+ " > a", "btn-custom-active");
        onUndo();
      } else if ((ne.getCtrlKey() || ne.getMetaKey()) && ne.getShiftKey() && ne.getKeyCode() == 'Z') {
      	// ctrl/cmd + shift + z
        event.cancel();
        // JQuery.flashStyleClass("#" + redo.getElement().getId()+ " > a", "btn-custom-active");
				onRedo();
      }
    }
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
	}
	private native void toggleButton(Element e, String type)/*-{
		$wnd.$(e).find('span').toggleClass('menu-btn2-big-active')
		$wnd.$(e).find('a').toggleClass('menu-btn2-active')
		$wnd.$(e).find('i').toggleClass('menu-ico2-' + type + '-active')
	}-*/;

private void enabled(FreehandModeChangedEvent event) {
		toggleButton(freehand, "freehand");
		// if (event.isModeTypeChanged()) {
		// 	editorContext.set(EditorProperty.FREEHAND_MODE_TYPE, event.getModeType());
		// }
		surface.getEditorContext().set(EditorProperty.FREEHAND_MODE, true);
		trigger("freehand-on");
	}

  private native void trigger(String event)/*-{
    $wnd.$($doc).trigger(event);
  }-*/;

	private void disabled() {
		toggleButton(freehand, "freehand");
	}

	private void onUndo() {
		surface.getEditorContext().getEventBus().fireEvent(new UndoEvent());
		toggleButton(undo, "undo");
		Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
			@Override
			public boolean execute() {
				toggleButton(undo, "undo");
				return false;
			}
		}, 100);
	}
	private void onRedo() {
		surface.getEditorContext().getEventBus().fireEvent(new RedoEvent());
	}

}