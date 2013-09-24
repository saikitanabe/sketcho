package net.sevenscales.editor.api.impl;

import java.util.Set;
import java.util.HashSet;

import net.sevenscales.domain.utils.SLogger;

import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.SelectionEvent;
import net.sevenscales.editor.api.event.SelectionEventHandler;
import net.sevenscales.editor.api.event.EditorClosedEvent;
import net.sevenscales.editor.api.event.EditorClosedEventHandler;
import net.sevenscales.editor.api.event.UnselectAllEvent;
import net.sevenscales.editor.api.event.UnselecteAllEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesEndedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;

import net.sevenscales.editor.diagram.Diagram;

import net.sevenscales.editor.uicomponents.AnchorElement;


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
	}

	public void fireEditorOpen() {
		surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_IS_OPEN, true);
		surface.getEditorContext().getEventBus().fireEvent(new EditDiagramPropertiesStartedEvent());
	}

	public void fireEditorClosed() {
		surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_IS_OPEN, false);
		surface.getEditorContext().getEventBus().fireEvent(new EditDiagramPropertiesEndedEvent());
	}

	public void fireChanged(Diagram diagram) {
		Set<Diagram> diagrams = new HashSet<Diagram>();
		for (AnchorElement ae : diagram.getAnchors()) {
			// this starts to fail, null pointer
			// but where is the actual problem!!
			// should be cleaned up!!, difficult to track down.
			if (ae.getHandler() != null) {
				diagrams.add(ae.getHandler().connection());
			}
		}
		
		diagrams.add(diagram);
    surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
	}

}