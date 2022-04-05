package net.sevenscales.editor.api.impl;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.BackgroundMoveStartedEvent;
import net.sevenscales.editor.api.event.BackgroundMoveStartedEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesEndedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditorClosedEvent;
import net.sevenscales.editor.api.event.EditorClosedEventHandler;
import net.sevenscales.editor.api.event.SelectionEvent;
import net.sevenscales.editor.api.event.SelectionEventHandler;
import net.sevenscales.editor.api.event.UnselectAllEvent;
import net.sevenscales.editor.api.event.UnselecteAllEventHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;



public class EditorCommon {
	private static final SLogger logger = SLogger.createLogger(EditorCommon.class);

	public interface HideEditor {
		void hide();
	}

	private ISurfaceHandler surface;
	private HideEditor hideEditor;

	public EditorCommon(ISurfaceHandler surface, HideEditor hideEditor) {
		this.surface = surface;
		this.hideEditor = hideEditor;

		surface.getEditorContext().getEventBus().addHandler(SelectionEvent.TYPE, new SelectionEventHandler() {
			@Override
			public void onSelection(SelectionEvent event) {
        // Debug.callstack("EditorCommon.onSelection...");
				EditorCommon.this.hideEditor.hide();
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(EditorClosedEvent.TYPE, new EditorClosedEventHandler() {
			@Override
			public void onSelection(EditorClosedEvent event) {
				EditorCommon.this.hideEditor.hide();
			}
		});

		surface.getEditorContext().getEventBus().addHandler(UnselectAllEvent.TYPE, new UnselecteAllEventHandler() {
			@Override
			public void onUnselectAll(UnselectAllEvent event) {
				logger.info("onUnselectAll...");
				// if surface background is selected => hide popup
				EditorCommon.this.hideEditor.hide();
			}
		});

		surface.getEditorContext().getEventBus().addHandler(BackgroundMoveStartedEvent.TYPE, new BackgroundMoveStartedEventHandler() {
			public void on(BackgroundMoveStartedEvent event) {
				EditorCommon.this.hideEditor.hide();	
			}
		});
	}

	public static void fireEditorOpen(ISurfaceHandler surface, Diagram diagram) {
		surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_IS_OPEN, true);
		surface.getEditorContext().getEventBus().fireEvent(new EditDiagramPropertiesStartedEvent(diagram));
	}

	public static void fireEditorClosed(ISurfaceHandler surface) {
		surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_IS_OPEN, false);
		surface.getEditorContext().getEventBus().fireEvent(new EditDiagramPropertiesEndedEvent());
	}


	public void fireEditorOpen(Diagram diagram) {
		EditorCommon.fireEditorOpen(surface, diagram);
	}

	public void fireEditorClosed() {
		EditorCommon.fireEditorClosed(surface);
	}

	public void fireChangedWithRelatedRelationships(Diagram diagram) {
		MouseDiagramEventHelpers.fireChangedWithRelatedRelationships(surface, diagram, ActionType.FONT_CHANGE);
	}

}