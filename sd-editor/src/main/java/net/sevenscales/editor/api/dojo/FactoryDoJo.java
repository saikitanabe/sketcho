package net.sevenscales.editor.api.dojo;

import com.google.gwt.core.client.GWT;

import com.google.gwt.user.client.ui.UIObject;

import net.sevenscales.editor.api.IModelingPanel;
import net.sevenscales.editor.api.ISurfaceHandler;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.content.UiSketchoBoardEditContent;
import net.sevenscales.editor.content.ui.IModeManager;

public class FactoryDoJo {
	public static IModelingPanel createModelingPanel(UIObject parent, int width, int height, boolean editable, IModeManager modeManager, EditorContext editorContext) {
		return new ModelingPanel(parent, width, height, editable, modeManager, editorContext, false, true);
	}

	public static ISurfaceHandler createSurfaceHandler() {
		return GWT.create(SurfaceHandlerImplFirefox.class);
	}
}